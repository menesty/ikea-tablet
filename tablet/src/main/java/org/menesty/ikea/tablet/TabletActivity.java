package org.menesty.ikea.tablet;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ViewFlipper;
import org.menesty.ikea.tablet.addapter.ProductArrayAdapter;
import org.menesty.ikea.tablet.domain.ProductItem;

public class TabletActivity extends Activity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        createParagon(null);
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
        adapter.add(new ProductItem("bla bla bla"));
        listView.requestLayout();
    }

    public void createParagon(View view) {
        RadioGroup paragonGroup = cast(findViewById(R.id.paragon_group));
        RadioButton currentRadio = new RadioButton(this);
        paragonGroup.addView(currentRadio);

        ListView listView = new ListView(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        listView.setLayoutParams(layoutParams);

        listView.setAdapter(new ProductArrayAdapter(this));

        ProductArrayAdapter adapter = cast(listView.getAdapter());
        adapter.add(new ProductItem("bla bla bla"));

        ViewFlipper flipper = cast(findViewById(R.id.listViewContainer));
        flipper.addView(listView);

        paragonGroup.check(currentRadio.getId());

    }


    public void deleteParagon(View view) {
        RadioGroup paragonGroup = cast(findViewById(R.id.paragon_group));
        View selected = findViewById(paragonGroup.getCheckedRadioButtonId());

        if (selected != null) {
            int index = paragonGroup.indexOfChild(selected);
            paragonGroup.removeView(findViewById(paragonGroup.getCheckedRadioButtonId()));

            if (paragonGroup.getChildCount() != 0)
                paragonGroup.check(paragonGroup.getChildAt(paragonGroup.getChildCount() == index ? 0 : index).getId());
        }
    }


    private <T> T cast(Object view) {
        return (T) view;
    }
}

