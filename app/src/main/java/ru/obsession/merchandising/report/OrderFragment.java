package ru.obsession.merchandising.report;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.clients.Client;
import ru.obsession.merchandising.clients.ClientsListFragment;
import ru.obsession.merchandising.database.DatabaseApi;
import ru.obsession.merchandising.main.MainActivity;
import ru.obsession.merchandising.shops.Shop;
import ru.obsession.merchandising.shops.ShopsListFragment;

public class OrderFragment extends Fragment {

    private ListView listView;
    private ArrayList<Goods> goodses;
    private Client client;
    private Shop shop;
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.list_view_fragment, container, false);
        Bundle bundle = getArguments();
        shop = (Shop) bundle.getSerializable(ShopsListFragment.SHOP_TAG);
        client = (Client) bundle.getSerializable(ClientsListFragment.CLIENT_TAG);
        userId = bundle.getInt(MainActivity.USER_ID);
        goodses = DatabaseApi.getInstance(getActivity()).getAssortment(userId, client.id, shop);
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
}
