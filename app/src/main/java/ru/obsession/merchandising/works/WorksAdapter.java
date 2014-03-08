package ru.obsession.merchandising.works;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.obsession.merchandising.R;

public class WorksAdapter extends ArrayAdapter<Work> {

    private final LayoutInflater lInflater;

    public WorksAdapter(Context context,  List<Work> objects) {
        super(context, R.layout.work_item, objects);
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder {
        TextView name;
        TextView description;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = lInflater.inflate(R.layout.work_item, null);
            holder.description = (TextView) v.findViewById(R.id.textDescription);
            holder.name = (TextView) v.findViewById(R.id.textName);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        Work work = getItem(position);
        holder.description.setText(work.description);
        holder.name.setText(work.name);
        return v;
    }
}
