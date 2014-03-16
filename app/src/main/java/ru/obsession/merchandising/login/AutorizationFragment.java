package ru.obsession.merchandising.login;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.main.MainActivity;
import ru.obsession.merchandising.server.ServerApi;
import ru.obsession.merchandising.shops.ShopsFragment;

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
            ServerApi.getInstance(getActivity()).singUp(name,password, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try{
                        SharedPreferences preferences = getActivity().getPreferences(getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor ed = preferences.edit();
                        ed.putInt(MainActivity.USER_ID,Integer.valueOf(s));
                        ed.commit();
                    startProfile();
                    } catch (Exception e){
                        Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(getActivity(),volleyError.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void startProfile() {
        SyncUtils.CreateSyncAccount(getActivity(), name, password);
        Fragment fragment = new ShopsFragment();
        getFragmentManager().popBackStack();
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
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
}
