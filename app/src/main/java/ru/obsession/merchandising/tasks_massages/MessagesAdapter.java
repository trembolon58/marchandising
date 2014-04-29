package ru.obsession.merchandising.tasks_massages;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import java.util.List;

import ru.obsession.merchandising.R;

public class MessagesAdapter extends ArrayAdapter<Message> {

    private final LayoutInflater lInflater;

    public MessagesAdapter(Context context, List<Message> objects) {
        super(context, R.layout.message_item, objects);
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}
