package com.kimkha.triethocduongpho.app;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kimkha.triethocduongpho.R;
import com.kimkha.triethocduongpho.ui.PageFragment;

public class PageActivity extends BaseActivity {

    private String mTitle;
    private String mImgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            Long id = getIntent().getLongExtra(PageFragment.ARG_ITEM_ID, 0);
            arguments.putLong(PageFragment.ARG_ITEM_ID, id);
            mTitle = getIntent().getStringExtra(PageFragment.ARG_ITEM_TITLE);
            arguments.putString(PageFragment.ARG_ITEM_TITLE, mTitle);
            mImgUrl = getIntent().getStringExtra(PageFragment.ARG_ITEM_IMG);
            arguments.putString(PageFragment.ARG_ITEM_IMG, mImgUrl);
            PageFragment pageFragment = new PageFragment();
            pageFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, pageFragment)
                    .commit();

        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_page;
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

}
