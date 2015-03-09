package com.kimkha.triethocduongpho;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import com.kimkha.triethocduongpho.R;
import com.kimkha.triethocduongpho.backend.articleApi.model.Article;
import com.kimkha.triethocduongpho.data.Content;
import com.kimkha.triethocduongpho.data.MyArticleService;

import java.util.List;

public class PageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            Long id = getIntent().getLongExtra(PageFragment.ARG_ITEM_ID, 0);
            arguments.putLong(PageFragment.ARG_ITEM_ID, id);
            String title = getIntent().getStringExtra(PageFragment.ARG_ITEM_TITLE);
            arguments.putString(PageFragment.ARG_ITEM_TITLE, title);
            PageFragment pageFragment = new PageFragment();
            pageFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, pageFragment)
                    .commit();

            getSupportActionBar().setTitle(title);
        }
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
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
