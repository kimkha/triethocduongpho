package com.kimkha.triethocduongpho.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.kimkha.triethocduongpho.R;
import com.kimkha.triethocduongpho.backend.article2Api.model.Article;
import com.kimkha.triethocduongpho.data.Category;
import com.kimkha.triethocduongpho.data.MyArticle2Service;
import com.kimkha.triethocduongpho.ui.MainFragment;
import com.kimkha.triethocduongpho.ui.NavigationDrawerFragment;
import com.kimkha.triethocduongpho.ui.PageFragment;
import com.kimkha.triethocduongpho.util.MyConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;


public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String SCREEN_NAME = "MAIN";

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
    private boolean isLoading = false;
    private MyConnection connection = new MyConnection(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (connection.checkNetworkAndShowAlert()) {
            isNetworkAvailable = true;

            tracking(SCREEN_NAME, "Default", "view", null, -1);

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
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isNetworkAvailable && connection.checkNetworkAndShowAlert()) {
            // Old = not connect, New = connected => reload activity
            isNetworkAvailable = true;
            restartActivity();
        }
    }

    public void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (connection.checkNetworkAndShowAlert()) {
            // update the main content by replacing fragments
            String category = Category.CATEGORY_LIST[position];
            mFragment = MainFragment.newInstance(category);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mFragment)
                    .commit();

            mTitle = category;
            restoreActionBar();

            tracking(SCREEN_NAME, mTitle.toString(), "select", null, -1);
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
        if (isNetworkAvailable && !mNavigationDrawerFragment.isDrawerOpen()) {
            this.optionsMenu = menu;
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            showProgressActionBar();
            return super.onCreateOptionsMenu(menu);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (isNetworkAvailable) {
                    mFragment.cleanAndReload();
                    tracking(SCREEN_NAME, mTitle.toString(), "reload", null, -1);
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(Long id, String title, String url, String imgUrl) {
        if (isNetworkAvailable) {
            tracking(SCREEN_NAME, mTitle.toString(), "click", title, id);

            Intent pageIntent = new Intent(this, PageActivity.class);
            pageIntent.putExtra(PageFragment.ARG_ITEM_ID, id);
            pageIntent.putExtra(PageFragment.ARG_ITEM_CATEGORY, mTitle.toString());
            pageIntent.putExtra(PageFragment.ARG_ITEM_TITLE, title);
            pageIntent.putExtra(PageFragment.ARG_ITEM_URL, url);
            pageIntent.putExtra(PageFragment.ARG_ITEM_IMG, imgUrl);
            startActivity(pageIntent);
        }
    }

    public void setRefreshActionButtonState(boolean isLoading) {
        this.isLoading = isLoading;
        showProgressActionBar();
//        if (isLoading) {
//            Toast.makeText(this, R.string.loading, Toast.LENGTH_LONG).show();
//        }
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

}
