package com.kimkha.triethocduongpho;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.kimkha.triethocduongpho.data.Category;

import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, MainFragment.Callbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Menu optionsMenu;
    private MainFragment mFragment;
    private boolean isNetworkAvailable = false;
    private AlertDialog networkDialog;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkNetworkAndShowAlert()) {
            isNetworkAvailable = true;

            mNavigationDrawerFragment = (NavigationDrawerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
            mTitle = getTitle();

            // Set up the drawer.
            mNavigationDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
        } else {
            isNetworkAvailable = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isNetworkAvailable && checkNetworkAndShowAlert()) {
            // Old = not connect, New = connected => reload activity
            isNetworkAvailable = true;
            restartActivity();
        }
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (checkNetworkAndShowAlert()) {
            // update the main content by replacing fragments
            String category = Category.CATEGORY_LIST[position];
            mFragment = MainFragment.newInstance(category);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mFragment)
                    .commit();

            mTitle = category;
            restoreActionBar();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        if (isNetworkAvailable && !mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.mainmenu, menu);
            restoreActionBar();
            showProgressActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isNetworkAvailable) {
            switch (item.getItemId()) {
                case R.id.action_refresh:
                    Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT)
                            .show();
                    mFragment.cleanAndReload();
                    break;
                case R.id.action_settings:
                    Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                            .show();
                    break;
                default:
                    break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Long id, String title, String imgUrl) {
        if (isNetworkAvailable) {
            Intent pageIntent = new Intent(this, PageActivity.class);
            pageIntent.putExtra(PageFragment.ARG_ITEM_ID, id);
            pageIntent.putExtra(PageFragment.ARG_ITEM_TITLE, title);
            pageIntent.putExtra(PageFragment.ARG_ITEM_IMG, imgUrl);
            startActivity(pageIntent);
        }
    }

    @Override
    public void onItemLoading() {
        isLoading = true;
        setRefreshActionButtonState();
    }

    @Override
    public void onItemLoaded() {
        isLoading = false;
        setRefreshActionButtonState();
    }

    public void setRefreshActionButtonState() {
        showProgressActionBar();
        if (isLoading) {
            Toast.makeText(this, R.string.loading, Toast.LENGTH_LONG).show();
        }
    }

    private void showProgressActionBar() {
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu
                    .findItem(R.id.action_refresh);
            if (refreshItem != null) {
                if (isLoading) {
                    MenuItemCompat.setActionView(refreshItem, R.layout.actionbar_indeterminate_progress);
                    //refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                } else {
                    MenuItemCompat.setActionView(refreshItem, null);
                    //refreshItem.setActionView(null);
                }
            }
        }
    }

    private boolean checkNetworkAndShowAlert() {
        if (!isNetworkConnected()) {
            if (networkDialog == null) {
                networkDialog = new AlertDialog.Builder(this).setMessage("Please Check Your Internet Connection and Try Again")
                        .setTitle("Network Error")
                        .setCancelable(false)
                        .setNegativeButton("Retry",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        restartActivity();
                                    }
                                })
                        .setPositiveButton("Connect to WIFI",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    }
                                })
                        .show();
            } else {
                networkDialog.show();
            }
            return false;
        }
        return true;
    }

    private boolean isServerConnected() {
        try{
            URL myUrl = new URL("triethocduongpho-android.appspot.com");
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            return true;
        } catch (Exception e) {
            // Handle your exceptions
            return false;
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return null != ni;
    }
}
