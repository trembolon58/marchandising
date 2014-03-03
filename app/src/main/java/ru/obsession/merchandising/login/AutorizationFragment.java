package ru.obsession.merchandising.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.main.NavigationDrawerFragment;

public class AutorizationFragment extends Fragment {

    private EditText editName;
    private EditText editPassword;
    private String name;
    private String password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.autorization_fragment, container, false);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        editName = (EditText) root.findViewById(R.id.editLogin);
        editPassword = (EditText) root.findViewById(R.id.editPassword);
        Button buttonLogin = (Button) root.findViewById(R.id.buttonLogin);
        NavigationDrawerFragment fragment =
                (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        fragment.disableDrawer();
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    private void login() {
        if (checkValues()) {
            startProfile();
        }
    }

    private void startProfile() {
        SyncUtils.CreateSyncAccount(getActivity(), name, password);
        NavigationDrawerFragment fragment =
                (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        fragment.login();
    }

    private boolean checkValues() {
        boolean allRight = true;
        name = editName.getText().toString();
        password = editPassword.getText().toString();
        if (name.equals("")){
            allRight = false;
            editName.setError(getString(R.string.input_data));
        }
        if (password.equals("")){
            allRight = false;
            editPassword.setError(getString(R.string.input_data));
        }
        return allRight;
    }

    public ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }
}
