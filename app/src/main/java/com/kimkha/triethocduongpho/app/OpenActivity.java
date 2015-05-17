package com.kimkha.triethocduongpho.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * @author kimkha
 * @version 1.3
 * @since 5/14/15
 */
public class OpenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String data = getIntent().getDataString();
        Log.e("AAA", "URL: " + data);

        startActivity(new Intent(this, MainActivity.class));
    }
}
