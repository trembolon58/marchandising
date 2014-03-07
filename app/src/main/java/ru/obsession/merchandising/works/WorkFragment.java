package ru.obsession.merchandising.works;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.clients.ClientFragment;
import ru.obsession.merchandising.main.MainActivity;
import ru.obsession.merchandising.report.ReportFragment;
import ru.obsession.merchandising.server.ServerApi;
import ru.obsession.merchandising.shops.Shop;
import ru.obsession.merchandising.shops.ShopsAdapter;
import ru.obsession.merchandising.shops.ShopsFragment;

public class WorkFragment extends Fragment {

    private final static String USER_ID = "user_id";
    private static final String WORK_TAG = "work_tag";
    public static final String WORK_ID = "work_id";
    private ListView listView;
    private ProgressBar progressBar;
    private ArrayList<Shop> works;
    private int userId;
    private int shopId;
    private int clientId;
    private Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                ArrayList<Shop> works = new ArrayList<Shop>();
                progressBar.setVisibility(View.GONE);
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    String desc = jsonObject.getString("desc");
                    boolean done = jsonObject.getInt("ready") == 1;
                    works.add(new Shop(id, name, desc, done));
                }
                listView.setAdapter(new ShopsAdapter(getActivity(), works));
            } catch (JSONException e) {
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            try {
                Toast.makeText(getActivity(), volleyError.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void retainInstance(Bundle savedState) {
        userId = savedState.getInt(USER_ID, -1);
        works = savedState.getParcelableArrayList(WORK_TAG);
        listView.setAdapter(new ShopsAdapter(getActivity(), works));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.list_view_progress, container, false);
        listView = (ListView) root.findViewById(R.id.listView);
        Bundle bundle = getArguments();
        shopId = bundle.getInt(ShopsFragment.SHOP_ID);
        clientId = bundle.getInt(ClientFragment.CLIENT_ID);
        userId = bundle.getInt(MainActivity.USER_ID);
        if (savedInstanceState == null) {
            ServerApi serverApi = ServerApi.getInstance(getActivity());
            serverApi.getWorks(userId, shopId, clientId, listener, errorListener);
        } else {
            retainInstance(savedInstanceState);
        }
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new ReportFragment();
                Bundle bundle = getArguments();
                bundle.putInt(WORK_ID, works.get(position).id);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment).addToBackStack("tag").commit();
            }
        });
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(WORK_TAG, works);
        outState.putInt(USER_ID, userId);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                progressBar.setVisibility(View.GONE);
                listView.setAdapter(null);
                ServerApi serverApi = ServerApi.getInstance(getActivity());
                serverApi.getWorks(userId, shopId, clientId, listener, errorListener);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
