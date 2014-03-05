package ru.obsession.merchandising.main;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.report.ReportFragment;
import ru.obsession.merchandising.shops.ShopsFragment;
import ru.obsession.merchandising.login.AutorizationFragment;
import ru.obsession.merchandising.login.GenericAccountService;
import ru.obsession.merchandising.login.SyncUtils;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String REPORT_FRAGMENT = "report_fragment";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    public static String USER_ID = "user_id";
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                drawerLayout);
        if (!existAccount()) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            Fragment fragment = new AutorizationFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment fragment;
        FragmentManager fragmentManager = normaliseStack();
        switch (position) {
            case 0:
                fragment = new ShopsFragment();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                return;
            case 1:
                fragment = new ReportFragment();
                fragmentManager.beginTransaction().replace(R.id.container, fragment, REPORT_FRAGMENT).commit();
                return;
        }

    }

    public FragmentManager normaliseStack() {
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        return fragmentManager;
    }

    public boolean existAccount() {
        AccountManager accountManager = (AccountManager) getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(GenericAccountService.ACCOUNT_TYPE);
        return accounts != null && accounts.length != 0;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mNavigationDrawerFragment.isDrawerOpen();
        hideMenuItems(menu, !drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void hideMenuItems(Menu menu, boolean visible) {
        for (int i = 0; i < menu.size(); i++) {

            menu.getItem(i).setVisible(visible);

        }
    }
}
