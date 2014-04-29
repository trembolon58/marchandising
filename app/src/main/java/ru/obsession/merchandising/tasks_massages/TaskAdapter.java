package ru.obsession.merchandising.tasks_massages;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import java.util.List;

import ru.obsession.merchandising.R;

public class TaskAdapter extends ArrayAdapter<Task> {


    private final LayoutInflater lInflater;

    public TaskAdapter(Context context, List<Task> objects) {
        super(context, R.layout.task_item, objects);
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
}
