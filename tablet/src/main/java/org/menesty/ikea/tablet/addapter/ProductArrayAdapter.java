package org.menesty.ikea.tablet.addapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.menesty.ikea.tablet.R;
import org.menesty.ikea.tablet.domain.ProductItem;
import org.menesty.ikea.tablet.util.NumberUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Menesty on 12/6/13.
 */
public class ProductArrayAdapter extends ArrayAdapter<ProductItem> {
    private final List<ProductItem> list;

    static class ViewHolder {
        protected TextView text;
        protected TextView count;
        protected TextView price;
        protected TextView weight;
    }

    public ProductArrayAdapter(Context context) {
        this(context, new ArrayList<ProductItem>());
    }

    public ProductArrayAdapter(Context context, List<ProductItem> list) {
        super(context, R.layout.row_product_layout, list);
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_product_layout, parent, false);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.label);
            viewHolder.count = (TextView) view.findViewById(R.id.count);
            viewHolder.price = (TextView) view.findViewById(R.id.price);
            viewHolder.weight = (TextView) view.findViewById(R.id.weight);

            view.setTag(viewHolder);
        } else
            view = convertView;

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.text.setText(list.get(position).productName);
        holder.count.setText(NumberUtil.toString(list.get(position).count));
        holder.price.setText(NumberUtil.toString(list.get(position).price));
        holder.weight.setText(NumberFormat.getInstance().format(list.get(position).weight) + " kg");

        if (list.get(position).checked)
            holder.weight.setBackgroundResource(R.color.green_light);
        else
            holder.weight.setBackground(holder.price.getBackground());

        int columnWidth = (int) (parent.getMeasuredWidth() * 0.1);

        holder.text.setMinWidth(parent.getMeasuredWidth() - columnWidth * 3);
        holder.price.setMinWidth(columnWidth);
        holder.count.setMinWidth(columnWidth);
        holder.weight.setMinWidth(columnWidth);

        return view;
    }

    public ProductItem[] getItems() {
        ProductItem[] items = new ProductItem[getCount()];

        for (int i = 0; i < getCount(); i++)
            items[i] = getItem(i);

        return items;
    }

    @Override
    public void add(ProductItem object) {
        for (ProductItem item : getItems())
            if (item.productName.equals(object.productName) && item.price == object.price) {
                item.count += object.count;
                notifyDataSetChanged();
                return;
            }

        super.insert(object, 0);
    }

    @Override
    public void addAll(Collection<? extends ProductItem> collection) {
        for (ProductItem item : collection)
            add(item);

    }

    @Override
    public void addAll(ProductItem... items) {
        for (ProductItem item : items)
            add(item);
    }
}
