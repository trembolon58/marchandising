package ru.obsession.merchandising.returned;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.obsession.merchandising.R;

public class ReturnedAdapter extends ArrayAdapter<Goods> {

    private final LayoutInflater lInflater;

    public ReturnedAdapter(Context context, List<Goods> objects) {
        super(context, R.layout.return_item, objects);
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder {
        TextView textDescription;
        TextView textName;
        TextView textCount;
        TextView textReson;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = lInflater.inflate(R.layout.return_item, null);
            holder.textName = (TextView) v.findViewById(R.id.textName);
            holder.textDescription = (TextView) v.findViewById(R.id.textDescription);
            holder.textCount = (TextView) v.findViewById(R.id.textCount);
            holder.textReson = (TextView) v.findViewById(R.id.textResone);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        Goods goods = getItem(position);
        holder.textName.setText(goods.nameCompany);
        holder.textDescription.setText(goods.description);
        holder.textCount.setText(goods.count);
        holder.textReson.setText(goods.reasone);
        return v;
    }
}
