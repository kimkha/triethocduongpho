package com.kimkha.triethocduongpho.app;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.kimkha.triethocduongpho.BuildConfig;
import com.kimkha.triethocduongpho.R;
import com.kimkha.triethocduongpho.ui.PageFragment;

public class PageActivity extends BaseActivity {

    private String mTitle;
    private String mImgUrl;
    private int mAlpha = 0;
    private boolean mIsShow = false;

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
        showTransparentToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
            actionBar.setTitle(mIsShow?mTitle:"");
        }
    }

    public void setTitleIsShow(boolean isShow) {
        if (mIsShow == isShow) {
            // No change
            return;
        }
        mIsShow = isShow;
        Log.e("AAA", "mIsShow " + mIsShow);
        restoreActionBar();
    }

    public void makeTransparentToolbar(int alpha) {
        if (mAlpha == alpha) {
            // No change
            return;
        }
        mAlpha = alpha;
        Log.e("AAA", "mAlpha " + mAlpha);
        showTransparentToolbar();
    }

    private void showTransparentToolbar() {
        Toolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.getBackground().setAlpha(mAlpha);
        }
    }

}
