package ru.obsession.merchandising.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.customized_schedule.GetAccessFragment;
import ru.obsession.merchandising.server.ServerApi;
import ru.obsession.merchandising.shops.ShopsListFragment;
import ru.obsession.merchandising.tasks_massages.MessagesFragment;
import ru.obsession.merchandising.tasks_massages.TasksFragment;

public class MainFragment extends Fragment {
    private static final String WIFI_ENABLE_DIALOG = "wifi_Enable_Dialog";
    private Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.setSupportProgressBarIndeterminateVisibility(false);
                if (s.equals("no")) {
                    Fragment fragment = new GetAccessFragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, fragment).addToBackStack("tag").commit();
                } else {
                    Toast.makeText(mainActivity, s, Toast.LENGTH_LONG).show();
                    Fragment fragment = new ShopsListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(ShopsListFragment.TYPE, ShopsListFragment.ALL);
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, fragment).addToBackStack("tag").commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.main_fragment, container, false);
        Button buttonCustom = (Button) root.findViewById(R.id.buttonCustom);
        buttonCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.setSupportProgressBarIndeterminateVisibility(true);
                SharedPreferences preferences =
                        mainActivity.getSharedPreferences(MainActivity.PREFERENSES_NAME, Context.MODE_PRIVATE);
                int userId = preferences.getInt(MainActivity.USER_ID, -1);
                ServerApi.getInstance(getActivity()).testPermision(userId, responseListener, errorListener);
            }
        });
        Button buttonSchedule = (Button) root.findViewById(R.id.buttonSchedule);
        buttonSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ShopsListFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ShopsListFragment.TYPE, ShopsListFragment.IN_DAY);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment).addToBackStack("tag").commit();
            }
        });
        Button messages = (Button) root.findViewById(R.id.buttonMessages);
        messages.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MessagesFragment();
                fragment.setArguments(getArguments());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment).addToBackStack("tag").commit();
            }
        });
        Button tasks = (Button) root.findViewById(R.id.buttonSpecialTascs);
        tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new TasksFragment();
                fragment.setArguments(getArguments());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment).addToBackStack("tag").commit();
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.photo, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload_button:
                WifiManager wifiManager = (WifiManager) getActivity().getSystemService(getActivity().WIFI_SERVICE);
                if (wifiManager.isWifiEnabled()) {
                    Intent intent = new Intent(getActivity(), PhotoReportingService.class);
                    getActivity().startService(intent);
                } else {
                    dialogAlertWifiEnable();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dialogAlertWifiEnable() {
        WifiEnableDialog wifiEnableDialog = new WifiEnableDialog();
        wifiEnableDialog.show(getFragmentManager(), WIFI_ENABLE_DIALOG);
    }
}
