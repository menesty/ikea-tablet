package org.menesty.ikea.tablet;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import org.menesty.ikea.tablet.domain.AvailableProductItem;
import org.menesty.ikea.tablet.task.ErrorDataTask;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Locale;

public abstract class BaseActivity extends Activity {
    private static final String BACKUP_DATA_FILE = "backup.data";

    public BaseActivity() {
        SettingService.init(this);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                ex.printStackTrace();
                sendErrorReport(errors.toString());
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDefaultLanguage();
    }

    private void initDefaultLanguage() {
        Locale locale = SettingService.getSetting(this).getLanguage();

        Locale.setDefault(locale);

        Resources res = getResources();
        res.getConfiguration().locale = locale;

        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);

    }

    private void sendErrorReport(String errorData) {
        new ErrorDataTask().execute(SettingService.getSetting(this), errorData);
    }

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

            restoreApplicationState(p.readBundle(AvailableProductItem.class.getClassLoader()));
            return true;
        } catch (FileNotFoundException e) {
            //skip
        } catch (Exception e1) {
            deleteFile(BACKUP_DATA_FILE);
        }

        return false;
    }

    protected abstract void saveApplicationState(Bundle state);

    protected abstract void restoreApplicationState(Bundle state);

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

}
