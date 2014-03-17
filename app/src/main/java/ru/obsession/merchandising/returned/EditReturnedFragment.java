package ru.obsession.merchandising.returned;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.main.MainActivity;

public class EditReturnedFragment extends Fragment {
    public static final String RESULT = "result";
    Goods goods;
    EditText editCount;
    EditText editResone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.edit_returned_fragment, container, false);
        setHasOptionsMenu(true);
        goods = (Goods) getArguments().getSerializable(ReturnedFragment.GOODS);
        TextView textName = (TextView) root.findViewById(R.id.textView);
        textName.setText(goods.nameCompany);
        editResone = (EditText) root.findViewById(R.id.editResone);
        editResone.setText(goods.reasone);
        editCount = (EditText) root.findViewById(R.id.editCount);
        editCount.setText(goods.count);;
        setTargetFragment(getFragmentManager().findFragmentByTag(MainActivity.RETURNED_FRAGMENT), 0);
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
        String resone = editResone.getText().toString();
        if (resone.equals("")) {
            editResone.setError(getString(R.string.input_data));
            allRight = false;
        }
        if (allRight) {
            goods.count = count;
            goods.reasone = resone;
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
            getFragmentManager().popBackStack();
        }
    }
}
