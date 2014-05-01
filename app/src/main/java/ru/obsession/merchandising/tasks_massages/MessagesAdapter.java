package ru.obsession.merchandising.tasks_massages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.obsession.merchandising.R;

public class MessagesAdapter extends ArrayAdapter<Message> {

    private final LayoutInflater lInflater;

    public MessagesAdapter(Context context, List<Message> objects) {
        super(context, R.layout.message_item, objects);
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    private class ViewHolder {
        TextView text;
        TextView date;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = lInflater.inflate(R.layout.message_item, null);
            holder.text = (TextView) v.findViewById(R.id.textMessage);
            holder.date = (TextView) v.findViewById(R.id.textDate);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        final Message message = getItem(position);
        holder.text.setText(message.text);
        holder.date.setText(message.date);

        return v;
    }
}
