package ru.obsession.merchandising.works;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.obsession.merchandising.R;

public class DescriptionFragment extends Fragment {

    public static final String DESCRIPTION = "description";
    public static final String NAME = "name";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.work_description, container, false);
        Bundle bundle = getArguments();
        String desc = bundle.getString(DESCRIPTION);
        String name = bundle.getString(NAME);
        TextView textView = (TextView) root.findViewById(R.id.textDescription);
        textView.setText(desc);
        textView = (TextView) root.findViewById(R.id.textName);
        textView.setText(name);
        return root;
    }
}
