package ru.obsession.merchandising.shops;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.obsession.merchandising.R;

public class ShopsAdapter extends ArrayAdapter<Shop> {

    private final LayoutInflater lInflater;

    public ShopsAdapter(Context context, List<Shop> objects) {
        super(context, R.layout.shop_item, R.id.textName, objects);
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder {
        TextView name;
        TextView addres;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = lInflater.inflate(R.layout.shop_item, null);
            holder.addres = (TextView) v.findViewById(R.id.textAddress);
            holder.name = (TextView) v.findViewById(R.id.textName);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        Shop shop = getItem(position);
        holder.addres.setText(shop.address);
        holder.name.setText(shop.name);
        return v;
    }
}
