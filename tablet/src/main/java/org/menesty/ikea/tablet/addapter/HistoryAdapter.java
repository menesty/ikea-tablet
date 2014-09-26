package org.menesty.ikea.tablet.addapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.menesty.ikea.tablet.R;
import org.menesty.ikea.tablet.domain.History;
import org.menesty.ikea.tablet.util.NumberUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Menesty on
 * 9/7/14.
 * 9:25.
 */
public class HistoryAdapter extends ArrayAdapter<History>{
    private final List<History> list;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    static class ViewHolder {
        protected TextView date;
        protected TextView price;
    }

    public HistoryAdapter(Context context) {
        this(context, new ArrayList<History>());
    }

    public HistoryAdapter(Context context, List<History> list) {
        super(context, R.layout.row_history_layout, list);
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_history_layout, parent, false);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.date = (TextView) view.findViewById(R.id.date);
            viewHolder.price = (TextView) view.findViewById(R.id.price);

            view.setTag(viewHolder);
        } else
            view = convertView;

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.date.setText(sdf.format(list.get(position).getCreateDate()));
        holder.price.setText(NumberUtil.toString(list.get(position).getPrice()));

        int columnWidth = (int) (parent.getMeasuredWidth() * 0.1);
        holder.price.setMinWidth(columnWidth);
        holder.date.setMinWidth(columnWidth);

        return view;
    }

}
