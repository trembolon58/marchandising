package ru.obsession.merchandising.tasks_massages;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.main.MainActivity;
import ru.obsession.merchandising.server.ServerApi;

public class TasksFragment extends Fragment {

    private ListView listView;
    private int userId;
    private Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                ArrayList<Task> tasks = new ArrayList<Task>();
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.setSupportProgressBarIndeterminateVisibility(false);
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Task task = new Task();
                    task.getDate(jsonObject.getString("date"));
                    task.text = jsonObject.getString("text");
                    task.statusCode = jsonObject.getInt("response");
                    task.id = jsonObject.getInt("id");
                    if (task.statusCode == Task.TAKED) {
                        task.status = getString(R.string.taked);
                    } else if (task.statusCode == Task.REFUSED) {
                        task.status = getString(R.string.refused);
                    }
                    tasks.add(task);
                }
                listView.setAdapter(new TaskAdapter(mainActivity, tasks, userId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            try {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.setSupportProgressBarIndeterminateVisibility(false);
                Toast.makeText(mainActivity, R.string.requests_error, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.list_view_fragment, container, false);
        listView = (ListView) root.findViewById(R.id.listView);
        listView.setClickable(false);
        SharedPreferences preferences = getActivity().getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);
        userId = preferences.getInt(MainActivity.USER_ID, -1);
        listView.setSelector(android.R.color.transparent);
        refresh();
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        MainActivity activity = (MainActivity) getActivity();
        activity.setSupportProgressBarIndeterminateVisibility(true);
        SharedPreferences preferences =
                activity.getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);
        int userId = preferences.getInt(MainActivity.USER_ID, -1);
        listView.setAdapter(null);
        ServerApi.getInstance(getActivity()).getTasks(userId, listener, errorListener);
    }
}
