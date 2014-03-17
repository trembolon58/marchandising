package ru.obsession.merchandising.shops;

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
import ru.obsession.merchandising.server.ServerApi;


public class ShopsFragment extends Fragment {

    public final static String SHOP_ID = "shop_id";
    private ListView listView;
    private ArrayList<Shop> shops;
    private ProgressBar progressBar;
    private int userId;
    private Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                progressBar.setVisibility(View.GONE);
                JSONArray jsonArray = new JSONArray(s);
                if (listView.getAdapter() == null || shops.size() != jsonArray.length()) {
                    addShops(jsonArray);
                } else {
                    updateShops(jsonArray);
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void updateShops(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Shop shop = shops.get(i);
            shop.id = jsonObject.getInt("id");
            shop.name = jsonObject.getString("name");
            shop.address = jsonObject.getString("address");
            shop.done = jsonObject.getInt("ready") == 1;
        }
        listView.setVisibility(View.VISIBLE);
        ((ShopsAdapter) listView.getAdapter()).notifyDataSetChanged();
    }

    private void addShops(JSONArray jsonArray) throws JSONException {
        shops = new ArrayList<Shop>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int id = jsonObject.getInt("id");
            String name = jsonObject.getString("name");
            String address = jsonObject.getString("address");
            boolean done = jsonObject.getInt("ready") == 1;
            shops.add(new Shop(id, name, address, done));
        }
        listView.setAdapter(new ShopsAdapter(getActivity(), shops));
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
        listView = (ListView) root.findViewById(R.id.listView);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
            SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            userId = preferences.getInt(MainActivity.USER_ID, -1);
            if (userId == -1) {
                ((MainActivity) getActivity()).logOut();
                return root;
            } else {
                ServerApi serverApi = ServerApi.getInstance(getActivity());
                serverApi.getShops(userId, listener, errorListener);
            }
        if (shops != null) {
            listView.setAdapter(new ShopsAdapter(getActivity(),shops));
            progressBar.setVisibility(View.GONE);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                ClientFragment fragment = new ClientFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(SHOP_ID, shops.get(position).id);
                bundle.putInt(MainActivity.USER_ID, userId);
                fragment.setArguments(bundle);
                transaction.replace(R.id.container, fragment).addToBackStack("tag").commit();
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                progressBar.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                ServerApi serverApi = ServerApi.getInstance(getActivity());
                serverApi.getShops(userId, listener, errorListener);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
