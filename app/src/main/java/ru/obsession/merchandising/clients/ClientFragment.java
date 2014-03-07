package ru.obsession.merchandising.clients;

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
import ru.obsession.merchandising.main.MainActivity;
import ru.obsession.merchandising.server.ServerApi;
import ru.obsession.merchandising.shops.ShopsFragment;
import ru.obsession.merchandising.works.WorkFragment;

public class ClientFragment extends Fragment {

    public static final String CLIENT_ID = "client_id";
    private static final String CLIENT_TAG = "client_tag";
    private static final String USER_ID = "uset_id";
    private ListView listView;
    private ArrayList<Client> clients;
    private ProgressBar progressBar;
    private int userId;
    private int shopId;
    private Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                clients = new ArrayList<Client>();
                progressBar.setVisibility(View.GONE);
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    boolean done = jsonObject.getInt("ready") == 1;
                    clients.add(new Client(id, name, done));
                }
                listView.setAdapter(new ClientAdapter(getActivity(), clients));
            } catch(JSONException e) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View root = inflater.inflate(R.layout.list_view_progress, container, false);
        Bundle bundle = getArguments();
        shopId = bundle.getInt(ShopsFragment.SHOP_ID);
        listView = (ListView) root.findViewById(R.id.listView);
        if (savedInstanceState == null) {
            userId = bundle.getInt(MainActivity.USER_ID, -1);
            ServerApi serverApi = ServerApi.getInstance(getActivity());
            serverApi.getClients(userId, shopId, listener, errorListener);
        } else {
            retainInstance(savedInstanceState);
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
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        return root;
    }

    private void retainInstance(Bundle savedState) {
        userId = savedState.getInt(USER_ID, -1);
        clients = savedState.getParcelableArrayList(CLIENT_TAG);
        listView.setAdapter(new ClientAdapter(getActivity(), clients));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(CLIENT_TAG, clients);
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
                serverApi.getClients(userId, shopId, listener, errorListener);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
