package org.menesty.ikea.tablet.component;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import org.menesty.ikea.tablet.R;
import org.menesty.ikea.tablet.addapter.ProductArrayAdapter;
import org.menesty.ikea.tablet.domain.ProductItem;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Created by Menesty on 12/9/13.
 */
public class ProductViewLayout extends LinearLayout {
    private int selectedIndex = -1;

    public ProductViewLayout(Activity context, ViewGroup parent) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addView(inflater.inflate(R.layout.product_view_layout, parent, false));

        final ListView listView = (ListView) findViewById(R.id.product_view);
        listView.setAdapter(new ProductArrayAdapter(context));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedIndex = i;
                Log.e(getClass().getSimpleName(), "setOnItemClickListener");
            }
        });

        listView.setSelector(R.drawable.listitem_selector);

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setViewOnTouchListener(View.OnTouchListener listener) {
        findViewById(R.id.product_view).setOnTouchListener(listener);
    }

    public void setItems(ProductItem[] items) {
        getAdapter().addAll(items);
        updateInformation();
    }

    public ProductItem[] getItems() {
        return getAdapter().getItems();
    }

    public void add(ProductItem productItem) {
        getAdapter().add(productItem);
        updateInformation();
    }

    private ProductArrayAdapter getAdapter() {
        ListView listView = (ListView) findViewById(R.id.product_view);
        ProductArrayAdapter adapter = (ProductArrayAdapter) listView.getAdapter();
        return adapter;
    }

    public ProductItem getSelected() {
        if (selectedIndex >= 0)
            return getAdapter().getItem(selectedIndex);

        return null;
    }

    private void updateInformation() {
        TextView weightView = createStatusTextView();
        TextView priceView = createStatusTextView();

        BigDecimal price = BigDecimal.ZERO;
        BigDecimal weight = BigDecimal.ZERO;
        //recalculate
        for (ProductItem item : getItems()) {
            price = price.add(BigDecimal.valueOf(item.count).multiply(BigDecimal.valueOf(item.price)));
            weight = weight.add(BigDecimal.valueOf(item.count).multiply(BigDecimal.valueOf(item.weight)));
        }

        priceView.setText(price.doubleValue() + " PL");
        weightView.setText(NumberFormat.getInstance().format(BigDecimal.valueOf(weight.doubleValue() / 1000).doubleValue()) + " Kg");


        LinearLayout c = (LinearLayout) findViewById(R.id.status_panel);
        c.removeAllViews();
        c.addView(weightView);
        c.addView(priceView);

    }

    private TextView createStatusTextView() {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        return textView;
    }

    public void update(ProductItem updateItem) {
        for (ProductItem item : getAdapter().getItems()) {
            if (item.artNumber.equals(updateItem.artNumber)) {
                if (updateItem.count <= 0) {
                    if (item.equals(getSelected()))
                        selectedIndex = -1;
                    getAdapter().remove(item);
                } else
                    item.count = updateItem.count;

                getAdapter().notifyDataSetChanged();
                updateInformation();
            }
        }


    }
}
