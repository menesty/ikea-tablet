package org.menesty.ikea.tablet;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;
import org.menesty.ikea.tablet.component.ActiveParagonViewFragment;
import org.menesty.ikea.tablet.component.ParagonViewFragment;
import org.menesty.ikea.tablet.data.DataJsonService;
import org.menesty.ikea.tablet.db.DatabaseHelper;
import org.menesty.ikea.tablet.db.HistoryReaderContract;
import org.menesty.ikea.tablet.dialog.HistoryChoiceDialog;
import org.menesty.ikea.tablet.domain.AvailableProductItem;
import org.menesty.ikea.tablet.domain.History;
import org.menesty.ikea.tablet.domain.ProductItem;
import org.menesty.ikea.tablet.task.*;
import org.menesty.ikea.tablet.util.TaskFragment;

import java.io.*;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Locale;

public abstract class BaseActivity extends Activity implements TaskCallbacks {
    private static final String BACKUP_DATA_FILE = "backup.data";
    private DatabaseHelper db;

    public BaseActivity() {
        SettingService.init(this);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                sendErrorReport(errors.toString());
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initDefaultLanguage();

        db = new DatabaseHelper(getApplicationContext());
    }

    private void initDefaultLanguage() {
        Locale locale = SettingService.getSetting(this).getLanguage();

        Locale.setDefault(locale);

        Resources res = getResources();
        Configuration conf = res.getConfiguration();

        conf.locale = locale;

        res.updateConfiguration(conf, res.getDisplayMetrics());

    }

    private void sendErrorReport(String errorData) {
        new ErrorDataTask().execute(SettingService.getSetting(this), errorData);
    }

    //backup data to file
    public void fatalClose() {
        //save data to disk
        Bundle bundle = new Bundle();
        Parcel p = Parcel.obtain();
        FileOutputStream outputStream;
        try {
            saveApplicationState(bundle);

            bundle.writeToParcel(p, 0);

            outputStream = openFileOutput(BACKUP_DATA_FILE, Context.MODE_PRIVATE);
            outputStream.write(p.marshall());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(null);
        }
    }

    //restore data after application start
    public boolean fatalRestore() {
        Parcel p = Parcel.obtain();
        try {
            FileInputStream fis = openFileInput(BACKUP_DATA_FILE);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            while (fis.available() > 0)
                bos.write(fis.read());

            fis.close();
            deleteFile(BACKUP_DATA_FILE);

            byte[] bytes = bos.toByteArray();

            p.unmarshall(bytes, 0, bytes.length);
            p.setDataPosition(0);

            restoreApplicationState(true, p.readBundle(AvailableProductItem.class.getClassLoader()));
            return true;
        } catch (FileNotFoundException e) {
            //skip
        } catch (Exception e1) {
            deleteFile(BACKUP_DATA_FILE);
        }

        return false;
    }

    protected abstract void saveApplicationState(Bundle state);

    protected abstract void restoreApplicationState(boolean afterCrash, Bundle state);

    public void close(MenuItem menuItem) {
        finish();
        System.exit(0);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] convert(Class<T> claszz, Parcelable[] data) {
        Object array = Array.newInstance(claszz, data.length);

        T[] afterCasting = (T[]) array;

        for (int i = 0; i < data.length; i++)
            afterCasting[i] = cast(data[i]);

        return afterCasting;
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object view) {
        return (T) view;
    }

    protected void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;

        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

    }

    protected void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    /*
    move all item from active tab and send it to server
     */
    protected ParagonViewFragment uploadData(String actionId, List<ProductItem[]> data) {
        ActionBar actionBar = getActionBar();

        ParagonViewFragment tab = new ParagonViewFragment(actionId == null ? ParagonViewFragment.generateUUID() : actionId, data);

        ActionBar.Tab aTab = actionBar.newTab().setTabListener(new TabListener(tab));
        aTab.setIcon(R.drawable.ic_action_upload);

        actionBar.addTab(aTab, 1);

        //update tab names
        updateTabNames();

        sendData(tab.getUUID(), data);

        return tab;
    }

    protected void updateTabNames() {
        ActionBar actionBar = getActionBar();

        for (int i = 0; i < actionBar.getTabCount(); i++)
            if (i != 0)
                actionBar.getTabAt(i).setText(i + "");
    }

    protected void sendData(final String uuid, List<ProductItem[]> data) {
        DataJsonService service = new DataJsonService();
        String result = service.serializeParagons(uuid, data);

        uploadData(uuid, result);
        HistoryReaderContract.save(db.getWritableDatabase(), uuid, data);

    }

    private void uploadData(String uuid, String data) {
        TaskFragment<Boolean> mTaskFragment = new TaskFragment<Boolean>(false, false);

        mTaskFragment.start(new UploadDataTask(uuid, data), SettingService.getSetting(this));
        getFragmentManager().beginTransaction().add(mTaskFragment, uuid).commit();
    }

    @Override
    public void onPostExecute(BaseAsyncTask task, Object result) {
        if (task instanceof UploadDataTask) {
            UploadDataTask _task = cast(task);

            TaskFragment<Boolean> mTaskFragment = cast(getFragmentManager().findFragmentByTag(_task.UUID));

            if (!BaseActivity.<Boolean>cast(result)) {
                lockScreenOrientation();
                Toast.makeText(this, R.string.internetConnectionProblemTitle, Toast.LENGTH_LONG).show();

                getFragmentManager().beginTransaction().remove(mTaskFragment).commit();

                uploadData(_task.UUID, _task.data);

                unlockScreenOrientation();
            } else {
                onUpload(_task.UUID);
                getFragmentManager().beginTransaction().remove(mTaskFragment).commit();
            }

        } else if (task instanceof CancelParagonTask) {
            CancelParagonTask _task = cast(task);

            TaskFragment<Boolean> mTaskFragment = cast(getFragmentManager().findFragmentByTag("cancel_" + _task.UUID));

            if (BaseActivity.<Boolean>cast(result))
                onCancel(_task.UUID);

            getFragmentManager().beginTransaction().remove(mTaskFragment).commit();
        }
    }

    protected abstract void onUpload(String uuid);

    protected abstract void onCancel(String uuid);

    public void cancel(final String uuid) {
        TaskFragment<Boolean> mTaskFragment = new TaskFragment<Boolean>(true, false);

        mTaskFragment.start(new CancelParagonTask(uuid), SettingService.getSetting(this));
        getFragmentManager().beginTransaction().add(mTaskFragment, "cancel_" + uuid).commit();
    }

    public void history(MenuItem menuItem) {
        FragmentManager fm = getFragmentManager();
        HistoryChoiceDialog dialog = (HistoryChoiceDialog) fm.findFragmentByTag("HistoryChoiceDialog");

        if (dialog == null) {
            dialog = new HistoryChoiceDialog();
            dialog.setListener(new HistoryChoiceDialog.ItemSelectListener() {
                @Override
                public void onItemSelect(History item) {
                    List<ProductItem[]> data = HistoryReaderContract.loadItems(item, db.getReadableDatabase());
                    setActiveTabData(item.getActionId(), data);
                }
            });
        }

        History[] items = HistoryReaderContract.loadItems(db.getReadableDatabase());
        dialog.setAvailableHistories(items);
        dialog.show(fm, "ProductChoiceDialog");

    }

    private void setActiveTabData(String uuid, List<ProductItem[]> data) {
        setActiveTab(new ActiveParagonViewFragment(uuid, data));
    }

    protected abstract void setActiveTab(ActiveParagonViewFragment activeTab);

}