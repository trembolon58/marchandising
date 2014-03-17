package ru.obsession.merchandising.returned;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

public class ReturnedFragment extends Fragment {

    public static final String GOODS = "orders";
    private ListView listView;
    public ArrayList<Goods> goods;
    private Response.Listener<String> listener = new Response.Listener<String>() {

        @Override
        public void onResponse(String s) {
            try {
                goods = new ArrayList<Goods>();
                progressBar.setVisibility(View.GONE);
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    String company = jsonObject.getString("company");
                    String format = jsonObject.getString("format");
                    String weight = jsonObject.getString("weight");
                    int id = jsonObject.getInt("id");
                    goods.add(new Goods(id,name, company, weight, format));
                }
                listView.setAdapter(new ReturnedAdapter(getActivity(), goods));
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
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.requests_error, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.list_view_progress, container, false);
        listView = (ListView) root.findViewById(R.id.listView);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        Bundle bundle = getArguments();
        int shopId = bundle.getInt(ShopsFragment.SHOP_ID);
        if (goods == null) {
            ServerApi serverApi = ServerApi.getInstance(getActivity());
            serverApi.getAccortiment(shopId, listener, errorListener);
        } else {
            progressBar.setVisibility(View.GONE);
            listView.setAdapter(new ReturnedAdapter(getActivity(), goods));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new EditReturnedFragment();
                Bundle bundle = new Bundle();
                Goods good = ((ReturnedAdapter)listView.getAdapter()).getItem(position);
                bundle.putSerializable(GOODS, good);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment).addToBackStack("tag").commit();
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 1) {
            ((ReturnedAdapter) listView.getAdapter()).notifyDataSetChanged();
        } else {
            send();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.done_search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_button);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                ReturnedAdapter adapter = (ReturnedAdapter) listView.getAdapter();
                if (adapter != null) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    adapter.getFilter().filter(newText);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ReturnedAdapter adapter = (ReturnedAdapter) listView.getAdapter();
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
                send();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void send() {
        ServerApi serverApi = ServerApi.getInstance(getActivity());
        int userId = getArguments().getInt(MainActivity.USER_ID);
        int shopId = getArguments().getInt(ShopsFragment.SHOP_ID);
        JSONArray jsonArray = createJSONArray();
        if (jsonArray.length() == 0){
            Toast.makeText(getActivity(),R.string.nothing_send,Toast.LENGTH_LONG).show();
            return;
        }
        serverApi.sendGroupReturn(userId, shopId, jsonArray, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Toast.makeText(getActivity(), R.string.sexes, Toast.LENGTH_LONG).show();
                    getFragmentManager().popBackStack();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, errorListener);
    }

    private JSONArray createJSONArray() {
        JSONArray jsonArray = new JSONArray();
        for (Goods good : goods) {
            if (good.isFiel()) {
                JSONObject object = new JSONObject();
                try {
                    object.put("id", good.id);
                    object.put("count", good.count);
                    object.put("reason", good.reasone);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(object);
            }
        }
        return jsonArray;
    }
}
