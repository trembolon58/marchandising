package ru.obsession.merchandising.works;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.main.MainActivity;
import ru.obsession.merchandising.report.FaceReportFragment;
import ru.obsession.merchandising.report.OrderFragment;
import ru.obsession.merchandising.report.PhotoReportFragment;
import ru.obsession.merchandising.shops.Shop;
import ru.obsession.merchandising.shops.ShopsListFragment;

public class WorkFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.work_fragment, container, false);
        Bundle bundle = getArguments();
        Shop shop = (Shop) bundle.getSerializable(ShopsListFragment.SHOP_TAG);
        Button photoReport = (Button) root.findViewById(R.id.buttonPhotoReport);
        photoReport.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PhotoReportFragment();
                fragment.setArguments(getArguments());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment, MainActivity.REPORT_FRAGMENT).addToBackStack("tag").commit();
            }
        });
        Button report = (Button) root.findViewById(R.id.buttonReport);
        report.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FaceReportFragment();
                fragment.setArguments(getArguments());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment).addToBackStack("tag").commit();
            }
        });
        Button order = (Button) root.findViewById( R.id.buttonOrder);
        if (! shop.needOrder ){
            order.setVisibility(View.GONE);
        } else {
            order.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new OrderFragment();
                    fragment.setArguments(getArguments());
                    getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack("tag").commit();
                }
            });
        }
        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
