package ru.obsession.merchandising.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.obsession.merchandising.R;

public class OrderAdapter extends ArrayAdapter<Order> {

    private final LayoutInflater lInflater;

    public OrderAdapter(Context context, List<Order> objects) {
        super(context, R.layout.order_item, objects);
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder {
        TextView textDescription;
        TextView textName;
        TextView textCount;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = lInflater.inflate(R.layout.order_item, null);
            holder.textName = (TextView) v.findViewById(R.id.textName);
            holder.textDescription = (TextView) v.findViewById(R.id.textDescription);
            holder.textCount = (TextView) v.findViewById(R.id.textCount);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        Order order = getItem(position);
        holder.textName.setText(order.nameCompany);
        holder.textDescription.setText(order.description);
        holder.textCount.setText(order.count);
        return v;
    }
}
