package ru.obsession.merchandising.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.obsession.merchandising.R;

public class DrawerAdapter extends ArrayAdapter<DrawerItem> {
    private final LayoutInflater lInflater;


    public static class ViewHolder {
        TextView name;
        ImageView icon;
    }

    public DrawerAdapter(Context context, List<DrawerItem> objects) {
        super(context, R.layout.drawer_item, objects);
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = lInflater.inflate(R.layout.drawer_item, null);
            holder.name = (TextView) v.findViewById(R.id.textView);
            holder.icon = (ImageView) v.findViewById(R.id.imageView);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        DrawerItem item = getItem(position);
        if (item.icon == null) {
            holder.icon.setImageResource(item.iconRes);
        } else {
            holder.icon.setImageBitmap(item.icon);
        }
        if (item.text == null) {
            holder.name.setText(item.textRes);
        } else {
            holder.name.setText(item.text);
        }
        return v;
    }
}
