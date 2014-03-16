package ru.obsession.merchandising.report;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.main.MainActivity;

public class EditFaceReportFragment extends Fragment {
    public static final String RESULT = "result";
    Goods goods;
    EditText editCount;
    EditText editCost;
    EditText editRecidue;
    EditText editPlace;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.edit_face_fragment, container, false);
        setHasOptionsMenu(true);
        goods = (Goods) getArguments().getSerializable(FaceReportFragment.GOODS);
        TextView textName = (TextView) root.findViewById(R.id.textView);
        textName.setText(goods.nameCompany);
        editCost = (EditText) root.findViewById(R.id.editCost);
        editCost.setText(goods.cost);
        editCount = (EditText) root.findViewById(R.id.editCount);
        editCount.setText(goods.faces);
        editRecidue = (EditText) root.findViewById(R.id.editRecidue);
        editRecidue.setText(goods.recidue);
        editPlace = (EditText) root.findViewById(R.id.editPlase);
        editPlace.setText(goods.place);
        setTargetFragment(getFragmentManager().findFragmentByTag(MainActivity.FASE_REPORT), 0);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.done, menu);
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
        boolean allRight = true;
        if (getTargetFragment() == null) {
            return;
        }
        String count = editCount.getText().toString();
        if (count.equals("")) {
            editCount.setError(getString(R.string.input_data));
            allRight = false;
        }
        String cost = editCost.getText().toString();
        cost = cost.replace(",", ".");
        try {
            double v = Double.valueOf(cost);
            cost = String.valueOf(v);
        } catch (Exception e) {
            editCost.setError(getString(R.string.not_correct_tata));
            allRight = false;
        }
        String recidue = editRecidue.getText().toString();
        if (recidue.equals("")) {
            editRecidue.setError(getString(R.string.input_data));
            allRight = false;
        }
        String plase = editPlace.getText().toString();
        if (plase.equals("")) {
            editPlace.setError(getString(R.string.input_data));
            allRight = false;
        }
        if (allRight) {
            goods.faces = count;
            goods.cost = cost;
            goods.place = plase;
            goods.recidue = recidue;
            getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
            getFragmentManager().popBackStack();
        }
    }
}
