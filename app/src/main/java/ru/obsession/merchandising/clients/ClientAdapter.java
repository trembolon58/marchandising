package ru.obsession.merchandising.clients;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.obsession.merchandising.R;

public class ClientAdapter extends ArrayAdapter<Client> {
    private final LayoutInflater lInflater;

    public ClientAdapter(Context context, List<Client> objects) {
        super(context, R.layout.client_item, objects);
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder {
        TextView name;
        TextView phone;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = lInflater.inflate(R.layout.client_item, null);
            holder.name = (TextView) v.findViewById(R.id.textName);
            holder.phone = (TextView) v.findViewById(R.id.textPhone);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        Client client = getItem(position);
        holder.name.setText(client.name);
        holder.phone.setText(client.phone);
        return v;
    }
}
