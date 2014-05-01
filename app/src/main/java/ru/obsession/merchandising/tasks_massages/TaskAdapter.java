package ru.obsession.merchandising.tasks_massages;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.List;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.main.MainActivity;
import ru.obsession.merchandising.server.ServerApi;

public class TaskAdapter extends ArrayAdapter<Task> {

    private final LayoutInflater lInflater;
    private int userId;
    private Response.ErrorListener errorListener;
    private ServerApi serverApi;
    private String taked;
    private String refused;

    public TaskAdapter(final ActionBarActivity context, List<Task> objects, int userId) {
        super(context, R.layout.task_item, objects);
        this.userId = userId;
        taked = context.getString(R.string.taked);
        refused = context.getString(R.string.refused);
        serverApi = ServerApi.getInstance(context);
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                try {
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.setSupportProgressBarIndeterminateVisibility(false);
                    Toast.makeText(mainActivity, R.string.requests_error, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
    private class ViewHolder {
        TextView text;
        TextView date;
        Button take;
        Button refuse;
        TextView status;
        RelativeLayout container;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = lInflater.inflate(R.layout.task_item, null);
            holder.text = (TextView) v.findViewById(R.id.textMessage);
            holder.date = (TextView) v.findViewById(R.id.textDate);
            holder.status = (TextView) v.findViewById(R.id.textStatus);
            holder.take = (Button) v.findViewById(R.id.buttonTake);
            holder.refuse = (Button) v.findViewById(R.id.buttonRefuse);
            holder.container = (RelativeLayout) v.findViewById(R.id.containerControls);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        final Task task = getItem(position);
        holder.text.setText(task.text);
        holder.date.setText(task.date);
        if ( task.statusCode == Task.TAKED || task.statusCode == Task.REFUSED){
            holder.container.setVisibility(View.GONE);
            holder.status.setText(task.status);
        } else {
            holder.take.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverApi.sendAnsver(userId, task.id, Task.TAKED, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            holder.container.setVisibility(View.GONE);
                            task.status = taked;
                            holder.status.setText(task.status);
                        }
                    }, errorListener);
                }
            });
            holder.refuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverApi.sendAnsver(userId, task.id, Task.REFUSED, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            holder.container.setVisibility(View.GONE);
                            task.status = refused;
                            holder.status.setText(task.status);
                        }
                    }, errorListener);
                }
            });
        }
        return v;
    }
}
