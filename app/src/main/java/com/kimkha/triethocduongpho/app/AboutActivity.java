package com.kimkha.triethocduongpho.app;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.kimkha.triethocduongpho.R;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String versionName = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // Do nothing
        }

        try {
            HtmlTextView htmlTextView = (HtmlTextView) findViewById(R.id.content);
            String text = readInputStream(getAssets().open("changes.txt"));
            text = text.replace("__VER__", versionName);
            htmlTextView.setHtmlFromString(text, true, "");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_about;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    private String readInputStream(InputStream is) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String s = null;

        while ((s = r.readLine()) != null)
            sb.append(s);

        return sb.toString();
    }
}
