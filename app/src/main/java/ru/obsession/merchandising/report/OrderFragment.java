package ru.obsession.merchandising.report;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.clients.Client;
import ru.obsession.merchandising.clients.ClientsListFragment;
import ru.obsession.merchandising.database.DatabaseApi;
import ru.obsession.merchandising.main.MainActivity;
import ru.obsession.merchandising.server.ServerApi;
import ru.obsession.merchandising.shops.Shop;
import ru.obsession.merchandising.shops.ShopsListFragment;

public class OrderFragment extends Fragment {

    private ListView listView;
    private ArrayList<Goods> goodses;
    private Shop shop;
    private Client client;
    private int userId;
    private Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.setSupportProgressBarIndeterminateVisibility(false);
                Toast.makeText(mainActivity, R.string.report_sended, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    public Response.ErrorListener errorListener = new Response.ErrorListener() {
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
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity mainActivity = (MainActivity) getActivity();
        DatabaseApi.getInstance(getActivity()).saveOrder(goodses, userId, client.id, shop, mainActivity.timeServer);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.list_view_fragment, container, false);
        Bundle bundle = getArguments();
        shop = (Shop) bundle.getSerializable(ShopsListFragment.SHOP_TAG);
        client = (Client) bundle.getSerializable(ClientsListFragment.CLIENT_TAG);
        userId = bundle.getInt(MainActivity.USER_ID);
        MainActivity mainActivity = (MainActivity) getActivity();
        goodses = DatabaseApi.getInstance(getActivity()).getAssortmentOrder(userId, client.id, shop, mainActivity.timeServer);
        listView = (ListView) root.findViewById(R.id.listView);
        listView.setClickable(false);
        listView.setItemsCanFocus(true);
        listView.setAdapter(new OrderAdapter(getActivity(), goodses));
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.done_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
                sendResult();
                return true;
            case R.id.menu_clear:
                clear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clear() {
        for (Goods goods : goodses) {
            goods.needOrder = false;
            goods.orderNumber = null;
        }
        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();

    }

    private void sendResult() {
        ServerApi.getInstance(getActivity()).sendOrder(userId, shop.id, calcJSON(), listener, errorListener);
    }

    private JSONArray calcJSON() {
        JSONArray jsonArray = new JSONArray();
        for (Goods goods : goodses) {
            if (!goods.needOrder) {
                continue;
            }
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", goods.id);
                jsonObject.put("order_number", goods.orderNumber);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

}
