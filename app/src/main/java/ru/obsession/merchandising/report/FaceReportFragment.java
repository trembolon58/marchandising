package ru.obsession.merchandising.report;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
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

public class FaceReportFragment extends Fragment {

    public static final String GOODS = "orders";
    private ListView listView;
    public ArrayList<Goods> goods;
    private int incountHour;
    private int outcountHour;
    private int incountMin;
    private int outcountMin;
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
                listView.setAdapter(new FaceReportAdapter(getActivity(), goods));
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
                Toast.makeText(getActivity(), volleyError.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private ProgressBar progressBar;
    private EditText editIncounterTime;
    private EditText editOutcointerTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.face_report_fragment, container, false);
        editIncounterTime = (EditText) root.findViewById(R.id.editIncounterTime);
        editIncounterTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        incountHour = hourOfDay;
                        incountMin = minute;
                        editIncounterTime.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
                    }
                }, incountHour, incountMin, false);
                dialog.show();
            }
        });
        editOutcointerTime = (EditText) root.findViewById(R.id.editOutCounterTime);
        editOutcointerTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        outcountHour = hourOfDay;
                        outcountMin = minute;
                        editOutcointerTime.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
                    }
                }, outcountHour, outcountMin, false);
                dialog.show();
            }
        });
        listView = (ListView) root.findViewById(R.id.listView);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        Bundle bundle = getArguments();
        int shopId = bundle.getInt(ShopsFragment.SHOP_ID);
        if (goods == null) {
            ServerApi serverApi = ServerApi.getInstance(getActivity());
            serverApi.getAccortiment(shopId, listener, errorListener);
        } else {
            progressBar.setVisibility(View.GONE);
            listView.setAdapter(new FaceReportAdapter(getActivity(), goods));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new EditFaceReportFragment();
                Bundle bundle = new Bundle();
                Goods good = ((FaceReportAdapter)listView.getAdapter()).getItem(position);
                bundle.putSerializable(GOODS, good);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment).addToBackStack("tag").commit();
            }
        });
        return root;
    }
 private void startDialog(){

 }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 1) {
            ((FaceReportAdapter) listView.getAdapter()).notifyDataSetChanged();
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
        boolean inputed = true;
        if (incountHour > outcountHour || incountHour == outcountHour && incountMin >= outcountMin) {
            Toast.makeText(getActivity(), R.string.wrong_time, Toast.LENGTH_LONG).show();
            return;
        }
        for (Goods good : goods) {
            if (!good.isFiel()) {
                inputed = false;
                break;
            }
        }
        if (inputed) {
            send();
        } else {
            DialogFragment fragment = new NotFeelDialog();
            fragment.setArguments(getArguments());
            fragment.show(getFragmentManager(), "not_feel_dialog");
        }
    }

    private void send() {
        String inTime = String.valueOf(incountHour) + ":" + String.valueOf(incountMin);
        String outTime = String.valueOf(outcountHour) + ":" + String.valueOf(outcountMin);
        ServerApi serverApi = ServerApi.getInstance(getActivity());
        int userId = getArguments().getInt(MainActivity.USER_ID);
        int shopId = getArguments().getInt(ShopsFragment.SHOP_ID);
        serverApi.sendFacesReport(userId, shopId, createJSONArray(), inTime, outTime, new Response.Listener<String>() {
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
                    object.put("price", good.cost);
                    object.put("face_count", good.faces);
                    object.put("shelf_residue", good.recidue);
                    object.put("additional_seat", good.place);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(object);
            }
        }
        return jsonArray;
    }

    private static class NotFeelDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            setTargetFragment(getFragmentManager().findFragmentByTag(MainActivity.FASE_REPORT), 1);
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.conformtion)
                    .setMessage(R.string.not_all_fields_filled)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getTargetFragment().onActivityResult(1, 1, null);
                        }
                    }).setNegativeButton(R.string.cancel, null).create();
        }
    }
}
