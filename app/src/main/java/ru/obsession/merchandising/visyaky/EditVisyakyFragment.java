package ru.obsession.merchandising.visyaky;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.main.MainActivity;

public class EditVisyakyFragment extends Fragment {
    public static final String RESULT = "result";
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private Goods goods;
    private EditText editCount;
    private EditText editDate;
    private EditText editResponce;
    private EditText editResone;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.edit_visyaky_fragment, container, false);
        setHasOptionsMenu(true);
        goods = (Goods) getArguments().getSerializable(VisyakyFragment.GOODS);
        TextView textName = (TextView) root.findViewById(R.id.textView);
        textName.setText(goods.nameCompany);
        editDate = (EditText) root.findViewById(R.id.editDateInput);
        editDate.setText(goods.date);
        editCount = (EditText) root.findViewById(R.id.editCount);
        editCount.setText(goods.count);
        editResone = (EditText) root.findViewById(R.id.editResone);
        editResone.setText(goods.resone);
        editResponce = (EditText) root.findViewById(R.id.editResponse);
        editResponce.setText(goods.count);
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runDialog(editDate);
            }
        });
        setTargetFragment(getFragmentManager().findFragmentByTag(MainActivity.VISYAKY_FRAGMENT), 0);
        return root;
    }

    private void runDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        String dateStr = editText.getText().toString();
        if (dateStr.equals("")){
            SimpleDateFormat date = new SimpleDateFormat(DATE_FORMAT);
            dateStr = date.format(new Date());
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date date;
        try {
            date = simpleDateFormat.parse(dateStr);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month++;
                String monthString = String.valueOf(month);
                if (month < 10) {
                    monthString = "0" + monthString;
                }
                String dayString = String.valueOf(day);
                if (day < 10) {
                    dayString = "0" + dayString;
                }
                editText.setText(dayString + "." + monthString + "." + String.valueOf(year));
            }
        }, year, month, day);
        datePickerDialog.show();
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
        String date = editDate.getText().toString();
        if (date.equals("")) {
            editDate.setError(getString(R.string.input_data));
            allRight = false;
        }
        String resone = editResone.getText().toString();
        if (resone.equals("")) {
            editResone.setError(getString(R.string.input_data));
            allRight = false;
        }
        String responce = editResponce.getText().toString();
        if (responce.equals("")) {
            editResponce.setError(getString(R.string.input_data));
            allRight = false;
        }
        if (allRight) {
            goods.count = count;
            goods.date = date;
            goods.resone = resone;
            goods.responsible = responce;
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
            getFragmentManager().popBackStack();
        }
    }
}
