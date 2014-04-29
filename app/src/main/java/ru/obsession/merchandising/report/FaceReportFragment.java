package ru.obsession.merchandising.report;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
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
import ru.obsession.merchandising.shops.Shop;
import ru.obsession.merchandising.shops.ShopsListFragment;

public class FaceReportFragment extends Fragment {

    private ListView listView;
    public ArrayList<Goods> goods;
    private int incountHour;
    private int incountMin;
    private Client client;
    private Shop shop;
    private int userId;

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            try {
                Toast.makeText(getActivity(), R.string.requests_error, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private EditText editIncomerTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.face_report_fragment, container, false);
        editIncomerTime = (EditText) root.findViewById(R.id.editIncounterTime);
        editIncomerTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        incountHour = hourOfDay;
                        incountMin = minute;
                        editIncomerTime.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
                    }
                }, incountHour, incountMin, true);
                dialog.show();
            }
        });
        listView = (ListView) root.findViewById(R.id.listView);
        listView.setClickable(false);
        listView.setItemsCanFocus(true);
        Bundle bundle = getArguments();
        shop = (Shop) bundle.getSerializable(ShopsListFragment.SHOP_TAG);
        client = (Client) bundle.getSerializable(ClientsListFragment.CLIENT_TAG);
        userId = bundle.getInt(MainActivity.USER_ID);
        goods = DatabaseApi.getInstance(getActivity()).getAssortment(userId, client.id, shop);
        listView.setAdapter( new FaceReportAdapter(getActivity(), goods));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DatabaseApi.getInstance(getActivity()).saveReport(goods, userId, client.id, shop.id);
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
                FaceReportAdapter adapter = (FaceReportAdapter) listView.getAdapter();
                if (adapter != null) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    adapter.getFilter().filter(newText);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                FaceReportAdapter adapter = (FaceReportAdapter) listView.getAdapter();
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
                sendResult();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendResult() {
        String inTime = String.valueOf(incountHour) + ":" + String.valueOf(incountMin);
        String allFace = ((EditText) getView().findViewById(R.id.editAllFace)).getText().toString();
        JSONArray jsonArray = createJSONArray();
        if (jsonArray.length() == 0){
            Toast.makeText(getActivity(),R.string.nothing_send,Toast.LENGTH_LONG).show();
        }
    }

    private JSONArray createJSONArray() {
        JSONArray jsonArray = new JSONArray();
        for (Goods good : goods) {
                JSONObject object = new JSONObject();
                try {
                    object.put("id", good.id);
                    object.put("price", good.cost);
                    object.put("face_count", good.faces);
                    object.put("shelf_residue", good.residue);
                    object.put("additional_seat", good.place);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(object);
        }
        return jsonArray;
    }
}
