package ru.obsession.merchandising.customized_schedule;

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
import android.widget.ArrayAdapter;
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

public class CustomizedFragment extends Fragment {

    public final static String SHOPNET_ID = "shop_id";
    private static final String SHOPS_TAG = "shops_tag";
    private static final String USER_ID = "user_id";
    private static final String IDS_TAG = "ids_tag";
    private ListView listView;
    private ArrayList<String> shops;
    private ArrayList<Integer> shopsIds;
    private ProgressBar progressBar;
    private int userId;
    private Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                shops = new ArrayList<String>();
                shopsIds = new ArrayList<Integer>();
                progressBar.setVisibility(View.GONE);
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    shops.add(name);
                    shopsIds.add(id);
                }
                listView.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.shop_text_view, shops));
            } catch(JSONException e) {
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
            }catch (Exception e) {
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
        listView = (ListView) root.findViewById(R.id.listView);
        if (savedInstanceState == null) {
            SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            userId = preferences.getInt(MainActivity.USER_ID, -1);
            if (userId == -1) {
                ((MainActivity) getActivity()).logOut();
                return root;
            } else {
                ServerApi serverApi = ServerApi.getInstance(getActivity());
                serverApi.getShopsNet(listener, errorListener);
            }
        } else {
            retainInstance(savedInstanceState);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                ClientFragment fragment = new ClientFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(SHOPNET_ID, shopsIds.get(position));
                fragment.setArguments(bundle);
                transaction.replace(R.id.container, fragment).addToBackStack("tag").commit();
            }
        });
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        return root;
    }

    private void retainInstance(Bundle savedState) {
        userId = savedState.getInt(USER_ID,-1);
        shops = savedState.getStringArrayList(SHOPS_TAG);
        shopsIds = savedState.getIntegerArrayList(IDS_TAG);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.shop_text_view, shops));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(SHOPS_TAG, shops);
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
                serverApi.getShopsNet(listener, errorListener);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

