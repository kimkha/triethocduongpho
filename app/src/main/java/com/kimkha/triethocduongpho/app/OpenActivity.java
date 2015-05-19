package com.kimkha.triethocduongpho.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.kimkha.triethocduongpho.MyApplication;
import com.kimkha.triethocduongpho.ui.PageFragment;

/**
 * @author kimkha
 * @version 1.3
 * @since 5/14/15
 */
public class OpenActivity extends AppCompatActivity {
    private static final String SCREEN_NAME = "BROWSER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = getIntent().getDataString();
        tracking(url);
        startPageActivity(url);

        finish();
    }

    private void startPageActivity(String url) {
        startActivity(new Intent(this, MainActivity.class));

        Intent pageIntent = new Intent(this, PageActivity.class);
        pageIntent.putExtra(PageFragment.ARG_ITEM_URL, url);
        startActivity(pageIntent);
    }

    private void tracking(String url) {
        if (MyApplication.tracker == null) {
            return;
        }

        MyApplication.tracker.setScreenName(SCREEN_NAME);
        MyApplication.tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Open")
                .setAction("fromUrl")
                .setLabel(url)
                .build());
    }
}
