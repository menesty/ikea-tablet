package org.menesty.ikea.tablet;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;
import org.menesty.ikea.tablet.component.ActiveParagonViewFragment;
import org.menesty.ikea.tablet.component.ParagonViewFragment;
import org.menesty.ikea.tablet.data.DataJsonService;
import org.menesty.ikea.tablet.dialog.InternetConnectionDialog;
import org.menesty.ikea.tablet.dialog.NumberDialog;
import org.menesty.ikea.tablet.dialog.ProductChoiceDialog;
import org.menesty.ikea.tablet.domain.AvailableProductItem;
import org.menesty.ikea.tablet.domain.ProductItem;
import org.menesty.ikea.tablet.task.BaseAsyncTask;
import org.menesty.ikea.tablet.task.LoadServerDataTask;
import org.menesty.ikea.tablet.task.TaskCallbacks;
import org.menesty.ikea.tablet.task.UploadDataTask;
import org.menesty.ikea.tablet.util.TaskFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TabletActivity extends BaseActivity implements TaskCallbacks, LoadDataListener, NumberDialog.ProductWeightChangeListener {

    private ProductIdKeyboardHandler productIdKeyboardHandler;

    private ProductState productState = new ProductState();

    private static TabletActivity instance;

    private static final int DATA_LOADING = 1;

    private static final int DATA_NOT_LOADED = 0;

    private static final int DATA_LOADED = 2;

    private int dataLoadState;

    private List<ParagonViewFragment> tabs = new ArrayList<ParagonViewFragment>();

    public TabletActivity() {
        instance = this;
    }

    public static TabletActivity get() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        init();

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null) {
            if (!fatalRestore() || tabs.size() == 0) {
                ActionBar.Tab tab = actionBar.newTab().setText("Active");
                tabs.add(new ActiveParagonViewFragment(null));

                tab.setTabListener(new TabListener(tabs.get(0)));
                actionBar.addTab(tab);

                loadData();
            }
        }
    }

    private void saveTabState(Bundle outState) {
        for (int i = 0; i < tabs.size(); i++) {
            List<ProductItem[]> data = tabs.get(i).getData();

            for (int j = 0; j < data.size(); j++)
                outState.putParcelableArray("tab_" + i + "_view_" + j, data.get(j));

            outState.putInt("tab_" + i + "_view_count", +data.size());
        }

        outState.putInt("tab_count", +tabs.size());
    }

    private void restoreTabState(Bundle outState) {
        ActionBar actionBar = getActionBar();

        int tabCount = outState.getInt("tab_count");

        for (int i = 0; i < tabCount; i++) {
            ParagonViewFragment tab;
            ActionBar.Tab aTab;

            if (i == 0)
                aTab = actionBar.newTab().setText("Active").setTabListener(new TabListener(tab = new ActiveParagonViewFragment(getTabData(i, outState))));
            else
                aTab = actionBar.newTab().setText(i + "").setTabListener(new TabListener(tab = new ParagonViewFragment(getTabData(i, outState))));

            actionBar.addTab(aTab);
            tabs.add(tab);
        }
    }

    private List<ProductItem[]> getTabData(int tabIndex, Bundle outState) {
        List<ProductItem[]> data = new ArrayList<ProductItem[]>();

        int viewCount = outState.getInt("tab_" + tabIndex + "_view_count");

        for (int i = 0; i < viewCount; i++)
            data.add(convert(ProductItem.class, outState.getParcelableArray("tab_" + tabIndex + "_view_" + i)));

        return data;
    }

    @Override
    public void loadData() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected && dataLoadState == DATA_NOT_LOADED) {
            TaskFragment<List<AvailableProductItem>> mTaskFragment = cast(getFragmentManager().findFragmentByTag("task"));

            if (mTaskFragment == null) {
                mTaskFragment = new TaskFragment<List<AvailableProductItem>>();
                getFragmentManager().beginTransaction().add(mTaskFragment, "task").commit();
            }

            dataLoadState = DATA_LOADING;
            mTaskFragment.start(new LoadServerDataTask(), SettingService.getSetting(this));
        } else {
            showInternetConnectionDialog(dataLoadState == DATA_LOADING);

            dataLoadState = DATA_NOT_LOADED;
        }
    }

    private void showInternetConnectionDialog(boolean serverError) {
        InternetConnectionDialog internetConnectionDialog = cast(getFragmentManager().findFragmentByTag(InternetConnectionDialog.class.getName()));

        if (internetConnectionDialog == null)
            internetConnectionDialog = new InternetConnectionDialog(serverError);

        getFragmentManager().beginTransaction().add(internetConnectionDialog, InternetConnectionDialog.class.getName()).commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!isActiveTab())
            return false;

        findViewById(R.id.focus_button).requestFocus();

        if (KeyEvent.KEYCODE_BACK == keyCode)
            return false;

        productIdKeyboardHandler.handleChar((char) event.getUnicodeChar());
        Log.e(getClass().getSimpleName(), (char) event.getUnicodeChar() + " " + (event.getAction() == KeyEvent.ACTION_DOWN));
        return true;
    }

    private void init() {
        productIdKeyboardHandler = new ProductIdKeyboardHandler() {
            @Override
            public void onProductId(String productId) {
                Log.e(getClass().getSimpleName(), productId);

                if (productId.length() > 8)
                    productId = productId.substring(0, 8);

                Toast.makeText(getBaseContext(), "Scan :" + productId, Toast.LENGTH_LONG).show();
                addProduct(productId);
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void addProduct(String productId) {
        if (!isActiveTab())
            return;

        ProductItem product = productState.find(productId);

        if (product == null) {
            Toast.makeText(getApplicationContext(), "Product not available in order", Toast.LENGTH_SHORT).show();
            return;
        }

        showProductChoiceCount(product);
    }

    private void addProduct(ProductItem productItem) {
        if (!isActiveTab())
            return;

        if (getActive().addProduct(productItem))
            productState.takeProduct(productItem);
    }


    public void createParagon(MenuItem menuItem) {
        if (isActiveTab())
            getActive().createParagon();
    }

    public void sendToServer(MenuItem menuItem) throws IOException {
        DataJsonService service = new DataJsonService();
        String result = service.serializeParagons(getActive().getData());

        TaskFragment<Boolean> mTaskFragment = cast(getFragmentManager().findFragmentByTag("task-upload"));

        if (mTaskFragment == null)
            mTaskFragment = new TaskFragment<Boolean>();

        if (!mTaskFragment.isRunning()) {
            mTaskFragment.start(new UploadDataTask(), SettingService.getSetting(this), result);
            getFragmentManager().beginTransaction().add(mTaskFragment, "task-upload").commit();
        }

        System.out.println(result);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveApplicationState(outState);

        productIdKeyboardHandler.cancel();
    }

    @Override
    protected void saveApplicationState(Bundle state) {
        saveTabState(state);

        state.putParcelableArray("product_base_state", productState.getBaseState());
        state.putStringArray("product_state", productState.getState());
        state.putInt("loadDataState", dataLoadState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        ProductChoiceDialog dialog = (ProductChoiceDialog) getFragmentManager().findFragmentByTag("ProductChoiceDialog");

        if (dialog != null) {
            dialog.setAvailableProductItem(productState.getCurrentState());
            dialog.setListener(new ProductChoiceDialog.ItemSelectListener() {
                @Override
                public void onItemSelect(ProductItem item) {
                    showProductChoiceCount(item);
                }
            });
        }

        restoreApplicationState(savedInstanceState);
    }

    @Override
    protected void restoreApplicationState(Bundle state) {
        restoreTabState(state);

        AvailableProductItem[] items = convert(AvailableProductItem.class, state.getParcelableArray("product_base_state"));
        productState.setBaseState(items);
        productState.setState(state.getStringArray("product_state"));

        dataLoadState = state.getInt("loadDataState");

        if (dataLoadState == DATA_NOT_LOADED)
            loadData();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && data.getBooleanExtra("connectionDialog", false))
            loadData();
    }


    public void showSelectProductDialog(View view) {
        FragmentManager fm = getFragmentManager();
        ProductChoiceDialog dialog = (ProductChoiceDialog) fm.findFragmentByTag("ProductChoiceDialog");

        if (dialog == null) {
            dialog = new ProductChoiceDialog();
            dialog.setListener(new ProductChoiceDialog.ItemSelectListener() {
                @Override
                public void onItemSelect(ProductItem item) {
                    showProductChoiceCount(item);
                }
            });
            dialog.show(fm, "ProductChoiceDialog");
        }

        dialog.setAvailableProductItem(productState.getCurrentState());
    }

    public void deleteProductItem(View view) {
        if (isActiveTab()) {
            ProductItem item = getActive().deleteProductItem();

            if (item != null)
                try {
                    productState.returnBack(item, 1);
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
        }
    }


    public void deleteParagon(MenuItem view) {
        if (isActiveTab())
            getActive().deleteParagon();
    }

    private boolean isActiveTab() {
        return getActionBar().getSelectedNavigationIndex() == 0;
    }

    private ActiveParagonViewFragment getActive() {
        return this.cast(tabs.get(0));
    }


    @Override
    public void onPreExecute() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());

        wl.acquire();
    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onCancelled() {
    }

    @Override
    public void onPostExecute(BaseAsyncTask task, Object result) {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());

        if (wl.isHeld())
            wl.release();

        if (task instanceof LoadServerDataTask) {
            List<AvailableProductItem> data = cast(result);

            if (data != null) {
                productState.setBaseState(data);
                dataLoadState = DATA_LOADED;
            } else
                loadData();

        }

        if (task instanceof UploadDataTask) {
            if (this.<Boolean>cast(result))
                archiveParagon();
            else {
                Toast.makeText(getBaseContext(), R.string.serverConnectionProblemMessage, Toast.LENGTH_LONG).show();

                TaskFragment<Boolean> mTaskFragment = cast(getFragmentManager().findFragmentByTag("task-upload"));
                getFragmentManager().beginTransaction().remove(mTaskFragment).commit();
            }
        }

    }

    private void archiveParagon() {
        ActionBar actionBar = getActionBar();
        ParagonViewFragment tab = new ParagonViewFragment(getActive().getData());

        ActionBar.Tab aTab = actionBar.newTab().setText("1").setTabListener(new TabListener(tab));
        actionBar.addTab(aTab, 1);

        tabs.add(1, tab);

        //rename tab names
        for (int i = 0; i < actionBar.getTabCount(); i++)
            if (i != 0)
                actionBar.getTabAt(i).setText(i + "");

        getActive().reset();
    }

    public void refresh(MenuItem menuItem) {
        dataLoadState = 0;
        ActionBar actionBar = getActionBar();

        while (tabs.size() > 1) {
            tabs.remove(1);
            actionBar.removeTabAt(1);
        }

        getActive().reset();
        loadData();
    }

    public void settings(MenuItem menuItem) {
        showApplicationSettings(this, false);
    }

    public static void showApplicationSettings(Activity context, boolean connectionDialog) {
        Intent intentSetPref = new Intent(context, PrefActivity.class);
        intentSetPref.putExtra("connectionDialog", connectionDialog);
        context.startActivityForResult(intentSetPref, 10);
    }

    public void showProductChoiceCount(final ProductItem item) {
        if (item.count == 1) {
            addProduct(item);
            return;
        }

        final Dialog d = new Dialog(TabletActivity.get());
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.number_dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue((int) item.count);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.count = np.getValue();
                addProduct(item);
                d.dismiss();
            }
        });
        d.show();
    }


    @Override
    public void onWeightChange(String productName, double weight) {
        //update in state and already added
        productState.updateWeight(productName, weight);

        if (isActiveTab())
            getActive().updateWeight(productName, weight);
    }
}



