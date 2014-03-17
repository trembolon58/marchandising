package ru.obsession.merchandising.order;

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

        import java.util.Calendar;

        import ru.obsession.merchandising.R;
        import ru.obsession.merchandising.main.MainActivity;

public class EditOrderFragment extends Fragment {
    public static final String RESULT = "result";
    private Calendar calendar;
    private Order order;
    private EditText editCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.edit_order_fragment, container, false);
        TextView textName = (TextView) root.findViewById(R.id.textView);
        calendar = Calendar.getInstance();
        setHasOptionsMenu(true);
        order = (Order) getArguments().getSerializable(OrderFragment.ORDERS);
        textName.setText(order.nameCompany);
        editCount = (EditText) root.findViewById(R.id.editCount);
        editCount.setText(order.count);
        setTargetFragment(getFragmentManager().findFragmentByTag(MainActivity.ORDER), 0);
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
        if (getTargetFragment() == null) {
            return;
        }
        String count = editCount.getText().toString();
        if (count.equals("")) {
            editCount.setError(getString(R.string.input_data));
            return;
        }
        order.count = count;
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
        getFragmentManager().popBackStack();
    }
}

