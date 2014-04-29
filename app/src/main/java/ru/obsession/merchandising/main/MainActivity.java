package ru.obsession.merchandising.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.help.HelpActivity;
import ru.obsession.merchandising.login.AutorizationFragment;
import ru.obsession.merchandising.login.SyncUtils;
import ru.obsession.merchandising.server.ServerApi;

public class MainActivity extends ActionBarActivity {

    public static final String REPORT_FRAGMENT = "photo_report_fragment";
    public static final String PREFERENSES_NAME = "my_preferenses";
    public static final String ORDER = "order";
    public static final String RETURNED_FRAGMENT = "returned_fragment";
    public static final String VISYAKY_FRAGMENT = "visyaky_fragment";
    public static final String EXCHANGED_FRAGMENT = "exchanged_fragment";
    public static final String DB_VERSION = "db_version";
    public static final String FACE_REPORT = "cc";
    private static final String SERVER_TIME = "server_time";
    public static String USER_ID = "user_id";
    public static int timeServer;
    private int dbVersion;
    public Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                setSupportProgressBarIndeterminateVisibility(false);
                JSONObject jsonObject = new JSONObject(s);
                timeServer = jsonObject.getInt("time");
                SharedPreferences preferences = getSharedPreferences(MainActivity.PREFERENSES_NAME, Context.MODE_PRIVATE);
                preferences.edit().putInt(SERVER_TIME, timeServer).commit();
                int versionNew = jsonObject.getInt("db_ver");
                if (dbVersion != versionNew) {
                    Intent intent = new Intent(MainActivity.this, DownloadScheduleService.class);
                    intent.putExtra(DB_VERSION, versionNew);
                    startService(intent);
                } else {
                    Toast.makeText(MainActivity.this, R.string.actual_version, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    public Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            try {
                setSupportProgressBarIndeterminateVisibility(false);
                Toast.makeText(MainActivity.this, R.string.requests_error, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        SharedPreferences preferences = getSharedPreferences(PREFERENSES_NAME, Context.MODE_PRIVATE);
        int userId = preferences.getInt(MainActivity.USER_ID, -1);
        dbVersion = preferences.getInt(MainActivity.DB_VERSION, -1);
        timeServer = preferences.getInt(MainActivity.SERVER_TIME, -1);
        checkUpdate();
        if (userId == -1) {
            logOut();
        } else {
            timeServer = preferences.getInt(MainActivity.SERVER_TIME, -1);
            Fragment fragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }

    }

    public void checkUpdate() {
        setSupportProgressBarIndeterminateVisibility(true);
        SharedPreferences preferences = getSharedPreferences(PREFERENSES_NAME, Context.MODE_PRIVATE);
        int userId = preferences.getInt(MainActivity.USER_ID, -1);
        dbVersion = preferences.getInt(MainActivity.DB_VERSION, -1);
        ServerApi.getInstance(getApplicationContext()).testNewDb(userId, listener, errorListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void logOut() {
        SyncUtils.DeleteSyncAccount(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        SharedPreferences preferences = getSharedPreferences(PREFERENSES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putInt(USER_ID, -1).commit();
        Fragment fragment = new AutorizationFragment();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logOut();
                return true;
            case R.id.action_help:
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_refresh:
                checkUpdate();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
