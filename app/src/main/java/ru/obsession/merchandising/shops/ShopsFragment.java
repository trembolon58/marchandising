package ru.obsession.merchandising.shops;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import ru.obsession.merchandising.R;


public class ShopsFragment extends Fragment {

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.list_view_fragment,container,false);
        listView = (ListView) root.findViewById(R.id.listView);
        return root;
    }
}
