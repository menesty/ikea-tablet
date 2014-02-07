package org.menesty.ikea.tablet;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;
import org.menesty.ikea.tablet.addapter.ProductArrayAdapter;
import org.menesty.ikea.tablet.component.ProductViewLayout;
import org.menesty.ikea.tablet.dialog.ProductChoiceDialog;
import org.menesty.ikea.tablet.domain.AvailableProductItem;
import org.menesty.ikea.tablet.domain.ProductItem;
import org.menesty.ikea.tablet.task.LoadServerDataTask;
import org.menesty.ikea.tablet.task.TaskCallbacks;
import org.menesty.ikea.tablet.util.TaskFragment;

public class TabletActivity extends Activity implements TaskCallbacks {

    private int currentActiveParagonIndex = 0;

    private static volatile ProgressDialog mProgressDialog;

    private ProductIdKeyboardHandler productIdKeyboardHandler;

    private View.OnTouchListener listViewOnTouchListener;

    public TabletActivity() {
        Config.init();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();

        if (savedInstanceState == null) {
            createParagon(null);
            loadDataFromServer();
        }

    }

    private void loadDataFromServer() {
        mProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.load_data_from_server), true);

        TaskFragment mTaskFragment = new TaskFragment(this);
        mTaskFragment.start(new LoadServerDataTask(), Config.getServerUrl(), Config.getUser(), Config.getPassword());
        getFragmentManager().beginTransaction().add(mTaskFragment, "task").commit();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        findViewById(R.id.focus_button).requestFocus();
        productIdKeyboardHandler.handleChar((char) event.getUnicodeChar());
        Log.e(getClass().getSimpleName(), (char) event.getUnicodeChar() + " " + (event.getAction() == KeyEvent.ACTION_DOWN));
        return true;
    }

    private void init() {
        RadioGroup paragonGroup = cast(findViewById(R.id.paragon_group));
        paragonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int index = group.indexOfChild(group.findViewById(checkedId));
                if (currentActiveParagonIndex == index)
                    return;

                ViewFlipper flipper = cast(findViewById(R.id.listViewContainer));

                if (currentActiveParagonIndex > index) {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.go_prev_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.go_prev_out));
                } else {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.go_next_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.go_next_out));
                }

                flipper.setDisplayedChild(index);
                currentActiveParagonIndex = index;
            }
        });

        listViewOnTouchListener = new View.OnTouchListener() {
            private float fromPosition;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fromPosition = motionEvent.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        float delta = fromPosition - motionEvent.getX();
                        float percentage = view.getMeasuredWidth() > view.getMeasuredHeight() ? 0.2f : 0.3f;
                        if (Math.abs(delta) > view.getMeasuredWidth() * percentage)
                            if (delta > 0)
                                scrollFlipperView(1);
                            else
                                scrollFlipperView(-1);

                    default:
                        break;
                }
                return false;
            }
        };
        productIdKeyboardHandler = new ProductIdKeyboardHandler() {
            @Override
            public void onProductId(String productId) {
                Log.e(getClass().getSimpleName(), productId);
                addProduct(productId);
            }
        };
    }


    private int checkedRadioButtonIndex(RadioGroup group) {
        return group.indexOfChild(group.findViewById(group.getCheckedRadioButtonId()));
    }


    private void scrollFlipperView(int direction) {
        RadioGroup paragonGroup = cast(findViewById(R.id.paragon_group));
        View radioBox = paragonGroup.getChildAt(checkedRadioButtonIndex(paragonGroup) + direction);

        if (radioBox != null)
            paragonGroup.check(radioBox.getId());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void addProduct(String productId) {
        ProductViewLayout listView = getActiveView();

        if (listView == null)
            return;

        listView.add(new ProductItem(productId, 1, 110.2, 1000));
        listView.requestLayout();

    }

    public ProductViewLayout createParagon(MenuItem menuItem) {
        RadioGroup paragonGroup = cast(findViewById(R.id.paragon_group));

        RadioButton currentRadio = new RadioButton(this);
        paragonGroup.addView(currentRadio);

        ViewFlipper flipper = cast(findViewById(R.id.listViewContainer));

        ProductViewLayout productViewLayout = new ProductViewLayout(this, flipper);
        productViewLayout.setViewOnTouchListener(listViewOnTouchListener);

        flipper.addView(productViewLayout);
        paragonGroup.check(currentRadio.getId());

        return productViewLayout;
    }

    private void restoreState(ProductItem[] items) {
        ProductViewLayout listView = createParagon(null);
        listView.setItems(items);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ViewFlipper flipper = cast(findViewById(R.id.listViewContainer));
        int viewCount = 0;
        for (int i = 0; i < flipper.getChildCount(); i++)
            if (flipper.getChildAt(i) instanceof ProductViewLayout) {
                ProductViewLayout listView = cast(flipper.getChildAt(i));
                outState.putParcelableArray("view_" + viewCount, listView.getItems());
                viewCount++;
            }

        outState.putInt("viewCount", viewCount);

        if (mProgressDialog != null)
            mProgressDialog.dismiss();


        productIdKeyboardHandler.cancel();
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int viewCount = savedInstanceState.getInt("viewCount");
        for (int i = 0; i < viewCount; i++)
            restoreState(this.<ProductItem[]>cast(savedInstanceState.getParcelableArray("view_" + i)));

        TaskFragment mTaskFragment = (TaskFragment) getFragmentManager().findFragmentByTag("task");
        if (mTaskFragment != null && mTaskFragment.isRunning())
            mProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.load_data_from_server), true);

    }

    public void showSelectProductDialog(View view) {
        FragmentManager fm = getFragmentManager();
        ProductChoiceDialog dialog = (ProductChoiceDialog) fm.findFragmentByTag("ProductChoiceDialog");
        if (dialog == null) {
            dialog = new ProductChoiceDialog() {
                @Override
                public void onItemSelect(AvailableProductItem item) {
                    addProduct(item.productName);
                }
            };
            dialog.show(fm, "dialog");
        }
    }

    public void deleteProductItem(View view) {
        ProductViewLayout viewLayout = getActiveView();

        if (viewLayout == null || viewLayout.getSelected() == null)
            return;

        ProductItem item = viewLayout.getSelected();
        item.count--;
        viewLayout.update(item);
    }

    private ProductViewLayout getActiveView() {
        RadioGroup paragonGroup = cast(findViewById(R.id.paragon_group));
        int index = checkedRadioButtonIndex(paragonGroup);

        if (index >= 0)
            return cast(((ViewFlipper) findViewById(R.id.listViewContainer)).getChildAt(index));

        return null;
    }

    public void deleteParagon(MenuItem view) {
        RadioGroup paragonGroup = cast(findViewById(R.id.paragon_group));
        int index = checkedRadioButtonIndex(paragonGroup);
        if (index >= 0) {
            paragonGroup.removeViewAt(index);
            ViewFlipper flipper = cast(findViewById(R.id.listViewContainer));
            flipper.removeViewAt(index);

            if (paragonGroup.getChildCount() != 0)
                paragonGroup.check(paragonGroup.getChildAt(paragonGroup.getChildCount() == index ? 0 : index).getId());
        }
    }


    private <T> T cast(Object view) {
        return (T) view;
    }

    @Override
    public void onPreExecute() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        wl.acquire();

        mProgressDialog.show();
    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onCancelled() {
        mProgressDialog.setProgress(0);
        mProgressDialog.dismiss();
    }

    @Override
    public void onPostExecute() {
        mProgressDialog.dismiss();
        mProgressDialog = null;

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());

        if (wl.isHeld())
            wl.release();

    }

}

