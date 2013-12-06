package org.menesty.ikea.tablet.addapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.menesty.ikea.tablet.R;
import org.menesty.ikea.tablet.domain.ProductItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Menesty on 12/6/13.
 */
public class ProductArrayAdapter extends ArrayAdapter<ProductItem> {
    private final List<ProductItem> list;
    private final Activity context;

    static class ViewHolder {
        protected TextView text;
    }

    public ProductArrayAdapter(Activity context) {
        this(context, new ArrayList<ProductItem>());
    }

    public ProductArrayAdapter(Activity context, List<ProductItem> list) {
        super(context, R.layout.row_product_layout, list);
        this.list = list;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.row_product_layout, null);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.label);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(list.get(position).artNumber);
        return view;
    }

    public ProductItem[] getItems() {
        ProductItem[] items = new ProductItem[getCount()];

        for (int i = 0; i < getCount(); i++)
            items[i] = getItem(i);

        return items;
    }
}
