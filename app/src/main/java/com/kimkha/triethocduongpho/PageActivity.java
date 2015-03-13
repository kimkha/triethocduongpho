package com.kimkha.triethocduongpho;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
            String imgUrl = getIntent().getStringExtra(PageFragment.ARG_ITEM_IMG);
            arguments.putString(PageFragment.ARG_ITEM_IMG, imgUrl);
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
