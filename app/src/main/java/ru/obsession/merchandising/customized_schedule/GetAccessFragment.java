package ru.obsession.merchandising.customized_schedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
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
import ru.obsession.merchandising.shops.ShopsListFragment;

public class GetAccessFragment extends Fragment {
    private Response.Listener<String> resGetAccess = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                if (s.equals("1")) {
                    Toast.makeText(getActivity(), R.string.requests_sexes, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
            }
        }
    };
    private Response.Listener<String> testAccess = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                if (s.equals("1")) {
                    Fragment fragment = new ShopsListFragment();
                    FragmentManager manager = getFragmentManager();
                    manager.popBackStack();
                    manager.beginTransaction().replace(R.id.container, fragment).addToBackStack("tag").commit();
                } else {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
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
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.get_access_fragment, container, false);
        final EditText editPass = (EditText) root.findViewById(R.id.editText);
        Button getAccess = (Button) root.findViewById(R.id.buttonGetCode);
        Button sendCode = (Button) root.findViewById(R.id.buttonSendCode);
        if (savedInstanceState == null) {
            SharedPreferences preferences =
                    getActivity().getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);
            userId = preferences.getInt(MainActivity.USER_ID, -1);
            if (userId == -1) {
                ((MainActivity) getActivity()).logOut();
                return root;
            }
        } else {
            retainInstance(savedInstanceState);
        }
        getAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerApi serverApi = ServerApi.getInstance(getActivity());
                serverApi.getAcsess(userId, resGetAccess, errorListener);
            }
        });
        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editPass.getText().toString();
                if (password.equals("")) {
                    editPass.setError(getString(R.string.input_data));
                } else {
                    ServerApi serverApi = ServerApi.getInstance(getActivity());
                    serverApi.testAcsess(userId, password, testAccess, errorListener);

                }
            }
        });
        return root;
    }

    private void retainInstance(Bundle savedInstanceState) {
    }
}
