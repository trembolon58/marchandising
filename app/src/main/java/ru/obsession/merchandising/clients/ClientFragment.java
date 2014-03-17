package ru.obsession.merchandising.clients;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import ru.obsession.merchandising.customized_schedule.GetAccessFragment;
import ru.obsession.merchandising.main.MainActivity;
import ru.obsession.merchandising.report.ReportFragment;
import ru.obsession.merchandising.server.ServerApi;
import ru.obsession.merchandising.shops.ShopsFragment;
import ru.obsession.merchandising.works.WorkFragment;

public class ClientFragment extends Fragment {

    public static final String CLIENT_ID = "client_id";
    private ListView listView;
    private ArrayList<Client> clients;
    private ProgressBar progressBar;
    private int userId;
    private int shopId;
    private Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                progressBar.setVisibility(View.GONE);
                JSONArray jsonArray = new JSONArray(s);
                if (listView.getAdapter() == null || clients.size() != jsonArray.length()) {
                    addClients(jsonArray);
                } else {
                    updateClients(jsonArray);
                }
            } catch (JSONException e) {
                try {
                    Integer.valueOf(s);
                    FragmentManager manager = getFragmentManager();
                    manager.popBackStack();
                    Fragment fragment = new GetAccessFragment();
                    fragment.setArguments(getArguments());
                    manager.beginTransaction().replace(R.id.container, fragment).addToBackStack("tag").commit();
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void addClients(JSONArray jsonArray) throws JSONException {
        clients = new ArrayList<Client>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int id = jsonObject.getInt("id");
            String name = jsonObject.getString("name");
            boolean done = jsonObject.getInt("ready") == 1;
            clients.add(new Client(id, name, done));
        }
        listView.setAdapter(new ClientAdapter(getActivity(), clients));
    }

    private void updateClients(JSONArray jsonArray) throws JSONException {
        listView.setVisibility(View.VISIBLE);
        for (int i = 0; i < jsonArray.length(); ++i) {
            Client client = clients.get(i);
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            client.id = jsonObject.getInt("id");
            client.name = jsonObject.getString("name");
            client.done = jsonObject.getInt("ready") == 1;
        }
        ((ClientAdapter) listView.getAdapter()).notifyDataSetChanged();
    }

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            try {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.requests_error, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.list_view_progress, container, false);
        Bundle bundle = getArguments();
        shopId = bundle.getInt(ShopsFragment.SHOP_ID);
        listView = (ListView) root.findViewById(R.id.listView);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        userId = bundle.getInt(MainActivity.USER_ID, -1);
        ServerApi serverApi = ServerApi.getInstance(getActivity());
        serverApi.getClients(userId, shopId, listener, errorListener);
        if (clients != null) {
            progressBar.setVisibility(View.GONE);
            listView.setAdapter(new ClientAdapter(getActivity(), clients));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = getArguments();
                bundle.putInt(CLIENT_ID, clients.get(position).id);
                Fragment fragment = new WorkFragment();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment).addToBackStack("tag").commit();
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.client_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment;
        FragmentTransaction transaction;
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                progressBar.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                ServerApi serverApi = ServerApi.getInstance(getActivity());
                serverApi.getClients(userId, shopId, listener, errorListener);
                return true;
            case R.id.menu_report:
                fragment = new ReportFragment();
                fragment.setArguments(getArguments());
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment).addToBackStack("tag").commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
