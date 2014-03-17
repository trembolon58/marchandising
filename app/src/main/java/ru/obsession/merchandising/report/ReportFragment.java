package ru.obsession.merchandising.report;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.exchange.ExchengedFragment;
import ru.obsession.merchandising.main.MainActivity;
import ru.obsession.merchandising.order.OrderFragment;
import ru.obsession.merchandising.returned.ReturnedFragment;
import ru.obsession.merchandising.server.ServerApi;
import ru.obsession.merchandising.shops.ShopsFragment;
import ru.obsession.merchandising.visyaky.VisyakyFragment;

public class ReportFragment extends Fragment {
    private CheckBox checkFace;
    private CheckBox checkOrder;
    private CheckBox checkReturn;
    private CheckBox checkExchange;
    private CheckBox checkVisyaky;
    private Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                checkExchange.setEnabled(true);
                checkExchange.setChecked(jsonObject.getBoolean("group_exchange"));
                checkFace.setEnabled(true);
                checkFace.setChecked(jsonObject.getBoolean("assortment_report"));
                checkOrder.setEnabled(true);
                checkOrder.setChecked(jsonObject.getBoolean("send_order"));
                checkReturn.setEnabled(true);
                checkReturn.setChecked(jsonObject.getBoolean("group_return"));
                checkVisyaky.setEnabled(true);
                checkVisyaky.setChecked(jsonObject.getBoolean("group_dangling"));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.all_reports_fragment, container, false);
        ServerApi serverApi = ServerApi.getInstance(getActivity());
        setHasOptionsMenu(true);
        int userId = getArguments().getInt(MainActivity.USER_ID);
        int shopId = getArguments().getInt(ShopsFragment.SHOP_ID);
        Button buttonFaceReport = (Button) root.findViewById(R.id.buttonFaceReport);
        checkExchange = (CheckBox) root.findViewById(R.id.checkBoxExchange);
        checkFace = (CheckBox) root.findViewById(R.id.checkBoxFaceReport);
        checkVisyaky = (CheckBox) root.findViewById(R.id.checkBoxVisyaky);
        checkReturn = (CheckBox) root.findViewById(R.id.checkBoxReturn);
        checkOrder = (CheckBox) root.findViewById(R.id.checkBoxOrder);
        buttonFaceReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FaceReportFragment();
                fragment.setArguments(getArguments());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment, MainActivity.FASE_REPORT).addToBackStack("tag").commit();
            }
        });
        Button buttonOrder = (Button) root.findViewById(R.id.buttonOrder);
        buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new OrderFragment();
                fragment.setArguments(getArguments());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment, MainActivity.ORDER).addToBackStack("tag").commit();
            }
        });
        Button buttonVisyaky = (Button) root.findViewById(R.id.buttonVisyaky);
        buttonVisyaky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new VisyakyFragment();
                fragment.setArguments(getArguments());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment, MainActivity.VISYAKY_FRAGMENT).addToBackStack("tag").commit();
            }
        });
        Button buttonReturn = (Button) root.findViewById(R.id.buttonReturn);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ReturnedFragment();
                fragment.setArguments(getArguments());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment, MainActivity.RETURNED_FRAGMENT).addToBackStack("tag").commit();
            }
        });
        Button buttonExchange = (Button) root.findViewById(R.id.buttonExchange);
        buttonExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ExchengedFragment();
                fragment.setArguments(getArguments());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment, MainActivity.EXCHANGED_FRAGMENT).addToBackStack("tag").commit();
            }
        });
        serverApi.rearyReports(userId, shopId, responseListener, errorListener);
        return root;
    }
}
