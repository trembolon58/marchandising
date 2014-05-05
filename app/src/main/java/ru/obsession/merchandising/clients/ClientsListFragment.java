package ru.obsession.merchandising.clients;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.database.DatabaseApi;
import ru.obsession.merchandising.main.MainActivity;
import ru.obsession.merchandising.shops.Shop;
import ru.obsession.merchandising.shops.ShopsListFragment;
import ru.obsession.merchandising.works.WorkFragment;

public class ClientsListFragment extends Fragment {

    private ListView listView;
    public static final String CLIENT_TAG = "client_tag";
    private int userId;
    private Shop shop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.list_view_fragment, container, false);
        Bundle bundle = getArguments();
        shop = (Shop) bundle.getSerializable(ShopsListFragment.SHOP_TAG);
        userId = bundle.getInt(MainActivity.USER_ID);
        listView = (ListView) root.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = getArguments();
                Fragment fragment = new WorkFragment();
                Client client = (Client) listView.getAdapter().getItem(position);
                bundle.putSerializable(CLIENT_TAG, client);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment).addToBackStack("tag").commit();
            }
        });
        refreshView();
        return root;
    }

    private void refreshView() {
        ArrayList<Client> clients = DatabaseApi.getInstance(getActivity()).getClients(userId, shop.id);
        listView.setAdapter(new ClientAdapter(getActivity(), clients));
    }

}
