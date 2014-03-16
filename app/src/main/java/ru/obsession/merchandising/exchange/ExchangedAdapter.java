package ru.obsession.merchandising.exchange;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.obsession.merchandising.R;

public class ExchangedAdapter extends ArrayAdapter<Goods> {

    private final LayoutInflater lInflater;

    public ExchangedAdapter(Context context, List<Goods> objects) {
        super(context, R.layout.exchange_item, objects);
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder {
        TextView textDescription;
        TextView textName;
        TextView textCount;
        TextView textDate;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = lInflater.inflate(R.layout.exchange_item, null);
            holder.textName = (TextView) v.findViewById(R.id.textName);
            holder.textDescription = (TextView) v.findViewById(R.id.textDescription);
            holder.textCount = (TextView) v.findViewById(R.id.textCount);
            holder.textDate = (TextView) v.findViewById(R.id.textDateInput);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        Goods goods = getItem(position);
        holder.textName.setText(goods.nameCompany);
        holder.textDescription.setText(goods.description);
        holder.textCount.setText(goods.count);
        holder.textDate.setText(goods.date);
        return v;
    }
}
