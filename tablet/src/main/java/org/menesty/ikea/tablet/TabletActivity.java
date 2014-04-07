package org.menesty.ikea.tablet;

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
import org.menesty.ikea.tablet.component.ParagonControlComponent;
import org.menesty.ikea.tablet.component.ProductViewLayout;
import org.menesty.ikea.tablet.data.DataJsonService;
import org.menesty.ikea.tablet.dialog.InternetConnectionDialog;
import org.menesty.ikea.tablet.dialog.ProductChoiceDialog;
import org.menesty.ikea.tablet.domain.AvailableProductItem;
import org.menesty.ikea.tablet.domain.ProductItem;
import org.menesty.ikea.tablet.task.BaseAsyncTask;
import org.menesty.ikea.tablet.task.LoadServerDataTask;
import org.menesty.ikea.tablet.task.TaskCallbacks;
import org.menesty.ikea.tablet.task.UploadDataTask;
import org.menesty.ikea.tablet.util.TaskFragment;

import java.io.IOException;
import java.util.List;

public class TabletActivity extends Activity implements TaskCallbacks, LoadDataListener {

    private ProductIdKeyboardHandler productIdKeyboardHandler;

    private ProductState productState = new ProductState();

    private ParagonControlComponent paragonControlComponent;

    private static TabletActivity instance;

    private static final int DATA_LOADING = 1;

    private static final int DATA_NOT_LOADED = 0;

    private static final int DATA_LOADED = 2;

    private int dataLoadState;

    private MenuItem refreshMenuItem;

    public TabletActivity() {
        instance = this;
    }

    public static TabletActivity get() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.init(this);
        setContentView(R.layout.main);
        init();
        paragonControlComponent = new ParagonControlComponent(this);

        if (savedInstanceState == null) {
            createParagon(null);
            loadData();
        }
        //new AutoUpdateApk(getApplicationContext());
    }

    @Override
    public void loadData() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected && dataLoadState == DATA_NOT_LOADED) {
            TaskFragment<List<AvailableProductItem>> mTaskFragment = cast(getFragmentManager().findFragmentByTag("task"));

            if (mTaskFragment == null) {
                mTaskFragment = new TaskFragment<List<AvailableProductItem>>();
                getFragmentManager().beginTransaction().add(mTaskFragment, "task").commit();
            }

            dataLoadState = DATA_LOADING;
            mTaskFragment.start(new LoadServerDataTask(), Config.getServerUrl(), Config.getUser(), Config.getPassword());

        } else {
            InternetConnectionDialog internetConnectionDialog = cast(getFragmentManager().findFragmentByTag("internetConnectionDialog"));

            if (internetConnectionDialog == null)
                internetConnectionDialog = new InternetConnectionDialog();

            getFragmentManager().beginTransaction().add(internetConnectionDialog, "internetConnectionDialog").commit();
            dataLoadState = DATA_NOT_LOADED;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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
        refreshMenuItem = menu.findItem(R.id.refresh);
        return true;
    }

    private void addProduct(String productId) {
        ProductItem product = productState.find(productId);

        if (product == null) {
            Toast.makeText(getApplicationContext(), "Product not available in order", Toast.LENGTH_SHORT).show();
            return;
        }

        showProductChoiceCount(product);
    }

    private void addProduct(ProductItem productItem) {
        if (paragonControlComponent.addProduct(productItem))
            productState.takeProduct(productItem);
    }


    public ProductViewLayout createParagon(MenuItem menuItem) {
        return paragonControlComponent.createParagon();
    }

    public void sendToServer(MenuItem menuItem) throws IOException {
        DataJsonService service = new DataJsonService();
        String result = service.serializeParagons(paragonControlComponent.getData());

        TaskFragment<Void> mTaskFragment = cast(getFragmentManager().findFragmentByTag("task-upload"));

        if (mTaskFragment == null)
            mTaskFragment = new TaskFragment<Void>();

        if (!mTaskFragment.isRunning()) {
            mTaskFragment.start(new UploadDataTask(), Config.getServerUrl(), Config.getUser(), Config.getPassword(), result);
            getFragmentManager().beginTransaction().add(mTaskFragment, "task-upload").commit();
        }

        System.out.println(result);
    }

    private void restoreState(ProductItem[] items) {
        ProductViewLayout listView = createParagon(null);
        listView.setItems(items);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        List<ProductItem[]> data = paragonControlComponent.getData();

        for (int i = 0; i < data.size(); i++)
            outState.putParcelableArray("view_" + i, data.get(i));

        outState.putInt("viewCount", data.size());
        outState.putParcelableArray("product_base_state", productState.getBaseState());
        outState.putStringArray("product_state", productState.getState());
        outState.putInt("loadDataState", dataLoadState);

        productIdKeyboardHandler.cancel();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int viewCount = savedInstanceState.getInt("viewCount");

        for (int i = 0; i < viewCount; i++)
            restoreState(this.<ProductItem[]>cast(savedInstanceState.getParcelableArray("view_" + i)));

        productState.setBaseState(this.<AvailableProductItem[]>cast(savedInstanceState.getParcelableArray("product_base_state")));
        productState.setState(savedInstanceState.getStringArray("product_state"));

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

        dataLoadState = savedInstanceState.getInt("loadDataState");

        if (dataLoadState == DATA_NOT_LOADED)
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
        ProductItem item = paragonControlComponent.deleteProductItem();

        if (item != null)
            productState.returnBack(item, 1);
    }


    public void deleteParagon(MenuItem view) {
        paragonControlComponent.deleteParagon();
    }

    @SuppressWarnings("unchecked")
    private <T> T cast(Object view) {
        return (T) view;
    }

    @Override
    public void onPreExecute() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
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

        if (task instanceof UploadDataTask)
            enableControl(false);

    }

    private void enableControl(boolean enable) {
        findViewById(R.id.add_paragon).setEnabled(enable);
        findViewById(R.id.delete_paragon).setEnabled(enable);
        findViewById(R.id.send_product).setEnabled(enable);

        findViewById(R.id.show_product_dialog).setEnabled(enable);
        findViewById(R.id.delete_product).setEnabled(enable);

        refreshMenuItem.setVisible(!enable);
    }

    public void refresh(MenuItem menuItem) {
        dataLoadState = 0;
        paragonControlComponent.reset();
        enableControl(true);
        loadData();
    }

    public void settings(MenuItem menuItem) {
        Intent intentSetPref = new Intent(getApplicationContext(), PrefActivity.class);
        startActivityForResult(intentSetPref, 0);
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
}

