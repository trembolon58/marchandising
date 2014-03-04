package ru.obsession.merchandising.shops;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class ShopsAdapter extends ArrayAdapter<Shop> {

    public ShopsAdapter(Context context, int resource, List<Shop> objects) {
        super(context, resource, objects);
    }
}
