package ru.obsession.merchandising.order;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.main.MainActivity;
import ru.obsession.merchandising.server.ServerApi;
import ru.obsession.merchandising.shops.ShopsFragment;

public class OrderFragment extends Fragment {

    public static final String ORDERS = "order";
    private ListView listView;
    public ArrayList<Order> orders;
    private Response.Listener<String> listener = new Response.Listener<String>() {

        @Override
        public void onResponse(String s) {
            try {
                orders = new ArrayList<Order>();
                progressBar.setVisibility(View.GONE);
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    String company = jsonObject.getString("company");
                    String format = jsonObject.getString("format");
                    String weight = jsonObject.getString("weight");
                    int id = jsonObject.getInt("id");
                    orders.add(new Order(id, name, company, weight, format));
                }
                listView.setAdapter(new OrderAdapter(getActivity(), orders));
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
    private EditText editDateOrder;
    private EditText editDateDone;
    public static final String DATE_FORMAT = "dd.MM.yyyy";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.order_fragment, container, false);
        editDateOrder = (EditText) root.findViewById(R.id.editDateOrder);
        editDateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runDialog(editDateOrder);
            }
        });
        editDateDone = (EditText) root.findViewById(R.id.editDateDone);
        editDateDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runDialog(editDateDone);
            }
        });
        listView = (ListView) root.findViewById(R.id.listView);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        Bundle bundle = getArguments();
        int shopId = bundle.getInt(ShopsFragment.SHOP_ID);
        if (orders == null) {
            ServerApi serverApi = ServerApi.getInstance(getActivity());
            serverApi.getAccortiment(shopId, listener, errorListener);
        } else {
            progressBar.setVisibility(View.GONE);
            listView.setAdapter(new OrderAdapter(getActivity(), orders));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new EditOrderFragment();
                Bundle bundle = new Bundle();
                Order order = ((OrderAdapter)listView.getAdapter()).getItem(position);
                bundle.putSerializable(ORDERS, order);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment).addToBackStack("tag").commit();
            }
        });
        return root;
    }

    private void runDialog(final EditText editText){
        Calendar calendar = Calendar.getInstance();
        String dateStr = editText.getText().toString();
        if (dateStr.equals("")){
            SimpleDateFormat date = new SimpleDateFormat(DATE_FORMAT);
            dateStr = date.format(new Date());
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date date;
        try {
            date = simpleDateFormat.parse(dateStr);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month++;
                String monthString = String.valueOf(month);
                if (month < 10) {
                    monthString = "0" + monthString;
                }
                String dayString = String.valueOf(day);
                if (day < 10) {
                    dayString = "0" + dayString;
                }
                editText.setText(dayString + "." + monthString + "." + String.valueOf(year));
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 1) {
            ((OrderAdapter) listView.getAdapter()).notifyDataSetChanged();
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
                OrderAdapter adapter = (OrderAdapter) listView.getAdapter();
                if (adapter != null) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    adapter.getFilter().filter(newText);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                OrderAdapter adapter = (OrderAdapter) listView.getAdapter();
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
    private boolean rightDate() {
        String sDone = editDateDone.getText().toString();
        String sOrder = editDateOrder.getText().toString();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date dateDone;
        Date dateOrder;
        try {
            dateDone = simpleDateFormat.parse(sDone);
            dateOrder = simpleDateFormat.parse(sOrder);
            int res = dateDone.compareTo(dateOrder);
            return res == 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void send() {
        ServerApi serverApi = ServerApi.getInstance(getActivity());
        int userId = getArguments().getInt(MainActivity.USER_ID);
        int shopId = getArguments().getInt(ShopsFragment.SHOP_ID);

        String done = editDateDone.getText().toString();
        boolean allRight = true;
        if (done.equals("")) {
            editDateDone.setError(getString(R.string.input_data));
            allRight = false;
        }
        String orderText = editDateOrder.getText().toString();
        if (orderText.equals("")) {
            editDateOrder.setError(getString(R.string.input_data));
            allRight = false;
        }
        if (allRight) {
            if (!rightDate()) {
                Toast.makeText(getActivity(), R.string.wrong_date, Toast.LENGTH_LONG).show();
                return;
            }
        }
        JSONArray jsonArray = createJSONArray();
        if (jsonArray.length() == 0){
            Toast.makeText(getActivity(),R.string.nothing_send,Toast.LENGTH_LONG).show();
            return;
        }
        serverApi.sendOrder(userId, shopId, jsonArray,orderText, done, new Response.Listener<String>() {
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
        for (Order order : orders) {
            if (order.isFiel()) {
                JSONObject object = new JSONObject();
                  try {
                  object.put("id", order.id);
                    object.put("count", order.count);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(object);
            }
        }
        return jsonArray;
    }
}
