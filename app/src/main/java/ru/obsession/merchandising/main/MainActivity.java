package ru.obsession.merchandising.main;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
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

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.login.AutorizationFragment;
import ru.obsession.merchandising.login.GenericAccountService;
import ru.obsession.merchandising.login.SyncUtils;
import ru.obsession.merchandising.shops.ShopsFragment;

public class MainActivity extends ActionBarActivity {

    public static final String REPORT_FRAGMENT = "report_fragment";
    public static final String FASE_REPORT = "fase_report";
    public static final String ORDER = "order";
    public static final String RETURNED_FRAGMENT = "returned_fragment";
    public static final String VISYAKY_FRAGMENT = "visyaky_fragment";
    public static final String EXCHANGED_FRAGMENT = "exchanged_fragment";
    public boolean needPop = true;
    public static String USER_ID = "user_id";
    public Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                setSupportProgressBarIndeterminateVisibility(false);
                if (needPop) {
                    getSupportFragmentManager().popBackStack();
                }
                Toast.makeText(MainActivity.this, R.string.sexes, Toast.LENGTH_LONG).show();
            }catch (Exception e){
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
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        Fragment fragment = new ShopsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    public boolean existAccount() {
        AccountManager accountManager = (AccountManager) getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(GenericAccountService.ACCOUNT_TYPE);
        return accounts != null && accounts.length != 0;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return super.onCreateOptionsMenu(menu);
    }
 public void logOut(){
     SyncUtils.DeleteSyncAccount(this);
     FragmentManager fragmentManager = getSupportFragmentManager();
     for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
         fragmentManager.popBackStack();
     }
     SharedPreferences preferences = getPreferences(MODE_PRIVATE);
     SharedPreferences.Editor ed = preferences.edit();
     ed.putInt(USER_ID, -1);
     Fragment fragment = new AutorizationFragment();
     fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
 }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
