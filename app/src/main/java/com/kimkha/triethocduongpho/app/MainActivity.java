package com.kimkha.triethocduongpho.app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.kimkha.triethocduongpho.R;
import com.kimkha.triethocduongpho.backend.article2Api.model.Article;
import com.kimkha.triethocduongpho.data.Category;
import com.kimkha.triethocduongpho.data.MonthYearAdapter;
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
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
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
    private String mTitle;
    private Menu optionsMenu;
    private MainFragment mFragment;
    private boolean isNetworkAvailable = false;
    private boolean isLoading = false;
    private MyConnection connection = new MyConnection(this);
    private AlertDialog monthDialog;
    private Calendar fromDate;
    private Calendar toDate;
    private MonthYearAdapter monthYearAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (connection.checkNetworkAndShowAlert()) {
            isNetworkAvailable = true;

            tracking(SCREEN_NAME, "Default", "view", null, -1);

            mNavigationDrawerFragment = (NavigationDrawerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
            //mTitle = getTitle();

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

    public void runFragment() {
        mFragment = MainFragment.newInstance(mTitle, fromDate, toDate);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mFragment)
                .commit();
        restoreActionBar();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (connection.checkNetworkAndShowAlert()) {
            // update the main content by replacing fragments
            mTitle = Category.CATEGORY_LIST[position];
            fromDate = null;
            toDate = null;

            runFragment();

            tracking(SCREEN_NAME, mTitle, "select", null, -1);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            String title = mTitle;
            if (fromDate != null) {
                title = String.format("(%02d/%02d) %s", fromDate.get(Calendar.MONTH), fromDate.get(Calendar.YEAR)%100, title);
            }
            actionBar.setTitle(title);
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
                    tracking(SCREEN_NAME, mTitle, "reload", null, -1);
                }
                break;
            case R.id.action_time:
                showMonthDialog();
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(Long id, String title, String url, String imgUrl) {
        if (isNetworkAvailable) {
            tracking(SCREEN_NAME, mTitle, "click", title, id);

            Intent pageIntent = new Intent(this, PageActivity.class);
            pageIntent.putExtra(PageFragment.ARG_ITEM_ID, id);
            pageIntent.putExtra(PageFragment.ARG_ITEM_CATEGORY, mTitle);
            pageIntent.putExtra(PageFragment.ARG_ITEM_TITLE, title);
            pageIntent.putExtra(PageFragment.ARG_ITEM_URL, url);
            pageIntent.putExtra(PageFragment.ARG_ITEM_IMG, imgUrl);
            startActivity(pageIntent);
        }
    }

    public void setRefreshActionButtonState(boolean isLoading) {
        this.isLoading = isLoading;
        showProgressActionBar();
    }

    private void showProgressActionBar() {
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu
                    .findItem(R.id.action_refresh);
            if (refreshItem != null) {
                if (isLoading) {
                    MenuItemCompat.setActionView(refreshItem, R.layout.actionbar_indeterminate_progress);
                } else {
                    MenuItemCompat.setActionView(refreshItem, null);
                }
            }
        }
    }

    private void setMonthYear(Calendar selectedDate) {
        if (selectedDate == null) {
            fromDate = null;
            toDate = null;
        } else {
            fromDate = selectedDate;
            toDate = (Calendar) selectedDate.clone();
            toDate.add(Calendar.MONTH, 1);
        }
        runFragment();
    }

    private void showMonthDialog() {
        if (monthDialog == null) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.month_title);

            monthYearAdapter = new MonthYearAdapter(MainActivity.this);

            builderSingle.setNegativeButton(R.string.month_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builderSingle.setPositiveButton(R.string.month_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            setMonthYear(null);
                        }
                    });

            builderSingle.setSingleChoiceItems(monthYearAdapter, monthYearAdapter.findMatchedPosition(fromDate),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Calendar value = (Calendar) monthYearAdapter.getItem(which);
                            setMonthYear(value);
                        }
                    });
            monthDialog = builderSingle.show();
        } else {
            monthDialog.show();
        }
    }

}
