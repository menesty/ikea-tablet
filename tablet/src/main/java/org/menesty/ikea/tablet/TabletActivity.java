package org.menesty.ikea.tablet;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import org.menesty.ikea.tablet.addapter.ProductArrayAdapter;
import org.menesty.ikea.tablet.domain.ProductItem;
import org.menesty.ikea.tablet.task.LoadServerDataTask;
import org.menesty.ikea.tablet.task.TaskCallbacks;
import org.menesty.ikea.tablet.util.TaskFragment;

public class TabletActivity extends Activity implements TaskCallbacks {

    private static final String KEY_CURRENT_PROGRESS = "current_progress";
    private int currentActiveParagonIndex = 0;
    private static volatile ProgressDialog mProgressDialog;
    private TaskFragment mTaskFragment;

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

        FragmentManager fm = getFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag("task");

        mTaskFragment = new TaskFragment(this);
        mTaskFragment.start(new LoadServerDataTask(), Config.getServerUrl(), Config.getUser(), Config.getPassword());
        fm.beginTransaction().add(mTaskFragment, "task").commit();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

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
                updateNavigationButtons();
                currentActiveParagonIndex = index;
            }
        });

        final EditText productNumberField = cast(findViewById(R.id.productNumberField));
        productNumberField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v == productNumberField) {
                    if (hasFocus) {
                        //open keyboard
                        ((InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(productNumberField,
                                InputMethodManager.SHOW_FORCED);

                    } else { //close keyboard
                        ((InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                                productNumberField.getWindowToken(), 0);
                    }
                }
            }
        });
        productNumberField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditTextFocus(true);
            }
        });
        setEditTextFocus(false);
    }

    public void setEditTextFocus(boolean isFocused) {
        final EditText productNumberField = cast(findViewById(R.id.productNumberField));
        productNumberField.setCursorVisible(isFocused);
        productNumberField.setFocusable(isFocused);
        productNumberField.setFocusableInTouchMode(isFocused);

        if (isFocused) {
            productNumberField.requestFocus();
        }
    }

    private void updateNavigationButtons() {
        RadioGroup paragonGroup = cast(findViewById(R.id.paragon_group));
        int index = checkedRadioButtonIndex(paragonGroup);

        if (index <= 0 || paragonGroup.getChildCount() == 1)
            this.<ImageButton>cast(findViewById(R.id.previousButton)).setVisibility(View.GONE);
        else
            this.<ImageButton>cast(findViewById(R.id.previousButton)).setVisibility(View.VISIBLE);

        if (index == paragonGroup.getChildCount() - 1 || paragonGroup.getChildCount() <= 1)
            this.<ImageButton>cast(findViewById(R.id.nextButton)).setVisibility(View.GONE);
        else
            this.<ImageButton>cast(findViewById(R.id.nextButton)).setVisibility(View.VISIBLE);
    }

    private int checkedRadioButtonIndex(RadioGroup group) {
        return group.indexOfChild(group.findViewById(group.getCheckedRadioButtonId()));
    }

    public void nextParagon(View view) {
        RadioGroup paragonGroup = cast(findViewById(R.id.paragon_group));
        int index = checkedRadioButtonIndex(paragonGroup);
        paragonGroup.check(paragonGroup.getChildAt(index + 1).getId());
    }

    public void previousParagon(View view) {
        RadioGroup paragonGroup = cast(findViewById(R.id.paragon_group));
        int index = checkedRadioButtonIndex(paragonGroup);
        paragonGroup.check(paragonGroup.getChildAt(index - 1).getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void addProductItem(View view) {
        RadioGroup paragonGroup = cast(findViewById(R.id.paragon_group));
        int index = paragonGroup.indexOfChild(paragonGroup.findViewById(paragonGroup.getCheckedRadioButtonId()));
        Log.i("RadioGroup", "Current selected radio button position : " + index);

        ViewFlipper flipper = cast(findViewById(R.id.listViewContainer));
        ListView listView = cast(flipper.getChildAt(index));
        ProductArrayAdapter adapter = cast(listView.getAdapter());
        adapter.add(new ProductItem("bla bla bla" + index));
        listView.requestLayout();


        findViewById(R.id.productNumberField).clearFocus();
    }

    public ListView createParagon(View view) {
        RadioGroup paragonGroup = cast(findViewById(R.id.paragon_group));

        RadioButton currentRadio = new RadioButton(this);
        paragonGroup.addView(currentRadio);

        ListView listView = new ListView(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        listView.setLayoutParams(layoutParams);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(new ProductArrayAdapter(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("");
            }
        });

        ViewFlipper flipper = cast(findViewById(R.id.listViewContainer));
        flipper.addView(listView);

        paragonGroup.check(currentRadio.getId());

        updateNavigationButtons();
        return listView;
    }

    private void restoreState(ProductItem[] items) {
        ListView listView = createParagon(null);
        ProductArrayAdapter adapter = cast(listView.getAdapter());
        adapter.addAll(items);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ViewFlipper flipper = cast(findViewById(R.id.listViewContainer));
        int viewCount = 0;
        for (int i = 0; i < flipper.getChildCount(); i++) {
            if (flipper.getChildAt(i) instanceof ListView) {
                ListView listView = cast(flipper.getChildAt(i));
                ProductArrayAdapter adapter = cast(listView.getAdapter());
                outState.putParcelableArray("view_" + viewCount, adapter.getItems());
                viewCount++;
            }

        }
        outState.putInt("viewCount", viewCount);

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            outState.putInt(KEY_CURRENT_PROGRESS, mProgressDialog.getProgress());
        } else {
            outState.putInt(KEY_CURRENT_PROGRESS, -1);
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int viewCount = savedInstanceState.getInt("viewCount");
        for (int i = 0; i < viewCount; i++)
            restoreState(this.<ProductItem[]>cast(savedInstanceState.getParcelableArray("view_" + i)));

        if (savedInstanceState.getInt(KEY_CURRENT_PROGRESS) != -1)
            mProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.load_data_from_server), true);

    }


    public void deleteParagon(View view) {
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

