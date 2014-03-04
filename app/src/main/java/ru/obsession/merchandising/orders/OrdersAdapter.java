package ru.obsession.merchandising.orders;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class OrdersAdapter extends ArrayAdapter<Order> {
    public OrdersAdapter(Context context, int resource, List<Order> objects) {
        super(context, resource, objects);
    }
}
