package ru.obsession.merchandising.customized_schedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.main.MainActivity;
import ru.obsession.merchandising.server.ServerApi;

public class GetAccessFragment extends Fragment {
    private Response.Listener<String> resGetAccess = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            try {
                Toast.makeText(getActivity(), volleyError.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.get_access_fragment, container, false);
        Button getAccess = (Button) root.findViewById(R.id.buttonGetCode);
        if (savedInstanceState == null) {
            SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
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
                serverApi.getAcsess(userId,resGetAccess,errorListener);
            }
        });
        return root;
    }

    private void retainInstance(Bundle savedInstanceState) {
    }
}
