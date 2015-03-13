package com.kimkha.triethocduongpho;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;


/**
 * An activity representing a single Page detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link CategoryListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link CategoryDetailFragment}.
 */
public class CategoryDetailActivity extends ActionBarActivity implements CategoryDetailFragment.Callbacks {

    private Menu optionsMenu;
    private CategoryDetailFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            String category = getString(R.string.category_def);
            Bundle arguments = new Bundle();
            arguments.putString(CategoryDetailFragment.ARG_CATEGORY, category);
//            arguments.putString(CategoryDetailFragment.ARG_CATEGORY,
//                    getIntent().getStringExtra(CategoryDetailFragment.ARG_CATEGORY));
            if (fragment == null) {
                fragment = new CategoryDetailFragment();
            }
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.page_detail_container, fragment)
                    .commit();

            getSupportActionBar().setTitle(category);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        setRefreshActionButtonState(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT)
                        .show();
                setRefreshActionButtonState(true);
                fragment.loadListFromStart();
                break;
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, CategoryListActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu
                    .findItem(R.id.action_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    MenuItemCompat.setActionView(refreshItem, R.layout.actionbar_indeterminate_progress);
                    //refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                } else {
                    MenuItemCompat.setActionView(refreshItem, null);
                    //refreshItem.setActionView(null);
                }
            }
        }
        if (refreshing) {
            Toast.makeText(this, R.string.loading, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(Long id, String title, String imgUrl) {
        Intent pageIntent = new Intent(this, PageActivity.class);
        pageIntent.putExtra(PageFragment.ARG_ITEM_ID, id);
        pageIntent.putExtra(PageFragment.ARG_ITEM_TITLE, title);
        pageIntent.putExtra(PageFragment.ARG_ITEM_IMG, imgUrl);
        startActivity(pageIntent);
    }

    @Override
    public void onItemLoading() {
        setRefreshActionButtonState(true);
    }

    @Override
    public void onItemLoaded() {
        setRefreshActionButtonState(false);
    }
}
