package com.kimkha.triethocduongpho.app;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.kimkha.triethocduongpho.MyApplication;
import com.kimkha.triethocduongpho.R;
import com.kimkha.triethocduongpho.util.FontSizeEnum;
import com.kimkha.triethocduongpho.util.PrefHelper;

import java.util.Arrays;

/**
 * @author kimkha
 * @version 0.2
 * @since 4/27/15
 */
public abstract class BaseActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        buildDialog();

        if (MyApplication.tracker != null) {
            MyApplication.tracker.setScreenName("BASE");
        }
    }

    protected abstract int getLayoutResource();

    protected void setActionBarIcon(int iconRes) {
        toolbar.setNavigationIcon(iconRes);
    }
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toolbar != null) {
            toolbar.getBackground().setAlpha(255);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.action_font:
                if (dialogBuilder == null) {
                    buildDialog();
                }
                dialogBuilder.show().setCanceledOnTouchOutside(true);
                break;
            case R.id.action_rate:
                Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    uri = Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName());
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public FontSizeEnum getFontSize() {
        int current = PrefHelper.getInt(this, "font_size");
        return FontSizeEnum.parse(current);
    }

    private void buildDialog() {
        dialogBuilder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle(R.string.font_size_title);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setNegativeButton(R.string.font_size_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        int current = PrefHelper.getInt(this, "font_size");

        dialogBuilder.setSingleChoiceItems(R.array.font_size, current, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PrefHelper.setInt(BaseActivity.this, "font_size", which);
                dialog.dismiss();

                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }

    public void tracking(String screenName, String category, String action, String label, long value) {
        if (MyApplication.tracker == null) {
            return;
        }

        MyApplication.tracker.setScreenName(screenName);

        HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action);

        if (label != null) {
            builder = builder.setLabel(label);
        }
        if (value >= 0) {
            builder = builder.setValue(value);
        }
        MyApplication.tracker.send(builder.build());
    }
}
