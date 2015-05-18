package com.kimkha.triethocduongpho.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kimkha.triethocduongpho.ui.PageFragment;

/**
 * @author kimkha
 * @version 1.3
 * @since 5/14/15
 */
public class OpenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = getIntent().getDataString();
        startPageActivity(url);

        finish();
    }

    private void startPageActivity(String url) {
        startActivity(new Intent(this, MainActivity.class));

        Intent pageIntent = new Intent(this, PageActivity.class);
        pageIntent.putExtra(PageFragment.ARG_ITEM_URL, url);
        startActivity(pageIntent);
    }
}
