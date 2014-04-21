package org.menesty.ikea.tablet.addapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import org.menesty.ikea.tablet.R;
import org.menesty.ikea.tablet.domain.ProductItem;
import org.menesty.ikea.tablet.util.NumberUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Menesty on 12/10/13.
 */
public class ProductAvailableAdapter extends ArrayAdapter<ProductItem> {

    private AvailableFilter filter;

    private final Object lock = new Object();

    private List<ProductItem> list;

    public ProductAvailableAdapter(Context context) {
        this(context, new ArrayList<ProductItem>());
    }

    public ProductAvailableAdapter(Context context, List<ProductItem> list) {
        super(context, R.layout.alert_list_row, list);
        this.list = new ArrayList<ProductItem>();
    }

    @Override
    public void add(ProductItem object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public void addAll(Collection<? extends ProductItem> collection) {
        super.addAll(collection);
        list.addAll(collection);
    }

    @Override
    public void addAll(ProductItem... items) {
        super.addAll(items);
        list.addAll(Arrays.asList(items));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.alert_list_row, null);

            holder.productId = (TextView) convertView.findViewById(R.id.productId);
            holder.count = (TextView) convertView.findViewById(R.id.count);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();


        ProductItem item = getItem(position);
        holder.productId.setText(item.productName);
        holder.count.setText(NumberUtil.toString(item.count));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new AvailableFilter();

        return filter;
    }

    private static class ViewHolder {
        TextView productId;
        TextView count;
    }

    class AvailableFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence filterStr) {
            FilterResults results = new FilterResults();

            if (filterStr == null || filterStr.length() == 0) {
                synchronized (lock) {
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                final String prefixString = filterStr.toString().toLowerCase();

                ArrayList<ProductItem> values = new ArrayList<ProductItem>(list);
                int count = values.size();

                ArrayList<ProductItem> newValues = new ArrayList<ProductItem>(count);

                for (ProductItem item : values) {
                    if (item.productName.toLowerCase().contains(prefixString))
                        newValues.add(item);
                }

                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            ProductAvailableAdapter.super.addAll((List<ProductItem>) filterResults.values);
            if (filterResults.count > 0)
                notifyDataSetChanged();
            else
                notifyDataSetInvalidated();
        }
    }
}
