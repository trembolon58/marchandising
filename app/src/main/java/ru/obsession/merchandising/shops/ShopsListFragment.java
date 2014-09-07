package ru.obsession.merchandising.shops;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.clients.ClientsListFragment;
import ru.obsession.merchandising.database.DatabaseApi;
import ru.obsession.merchandising.main.MainActivity;

public class ShopsListFragment extends Fragment {

    public static final String ALL = "all";
    public static final String TYPE = "type";
    public static final String IN_DAY = "in_day";
    public static final String SHOP_TAG = "shop_tag";
    private ListView listView;
    private int userId;
    private ArrayList<Shop> shops;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.list_view_fragment, container, false);
        listView = (ListView) root.findViewById(R.id.listView);
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Shop shop = (Shop) listView.getAdapter().getItem(position);
                Fragment fragment = new ClientsListFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(MainActivity.USER_ID, userId);
                bundle.putSerializable(ShopsListFragment.SHOP_TAG, shop);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack("tag").commit();
            }
        });
        SharedPreferences preferences = getActivity()
                .getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);
        userId = preferences.getInt(MainActivity.USER_ID, -1);
        if (userId == -1) {
            ((MainActivity) getActivity()).logOut();
        } else {
           refreshView();
        }
        return root;
    }

    private void refreshView() {
        Bundle bundle = getArguments();
        String type = bundle.getString(TYPE);
        if (type.equals(ALL)) {
            shops = DatabaseApi.getInstance(getActivity()).getAllShops(userId);
        } else {
            MainActivity mainActivity = (MainActivity) getActivity();
            shops = DatabaseApi.getInstance(getActivity()).getDayShops(userId, mainActivity.timeServer);
        }
        listView.setAdapter(new ShopsAdapter(getActivity(), shops));

    }
}
