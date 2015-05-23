package com.kimkha.triethocduongpho.util;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;

import com.kimkha.triethocduongpho.R;
import com.kimkha.triethocduongpho.app.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kimkha
 * @version 2.0
 * @since 5/21/15
 */
public class MyConnection {
    private final MainActivity activity;
    private AlertDialog networkDialog;
    private AlertDialog updateDialog;
    private boolean isForceUpdate;

    public MyConnection(MainActivity mainActivity) {
        activity = mainActivity;
    }

    public boolean checkNetworkAndShowAlert() {
        if (!isNetworkConnected()) {
            createAndShowAlert();
            return false;
        }

        if (mightForceUpdate()) {
            return false;
        }

        new HttpAsyncTask().execute("http://triethocduongpho-android.appspot.com/version.txt?t="+System.currentTimeMillis());
        return true;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return null != ni;
    }

    private void createAndShowAlert() {
        if (networkDialog == null) {
            networkDialog = new AlertDialog.Builder(activity)
                    .setTitle(R.string.network)
                    .setMessage(R.string.network_content)
                    .setCancelable(false)
                    .setNegativeButton(R.string.network_retry,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    activity.restartActivity();
                                }
                            })
                    .setPositiveButton(R.string.network_wifi,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            })
                    .show();
        } else {
            networkDialog.show();
        }
    }

    public boolean mightForceUpdate() {
        if (isForceUpdate) {
            showUpdatePopup();
            return true;
        }
        return false;
    }

    private void showUpdatePopup() {
        if (updateDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                    .setTitle(R.string.update)
                    .setPositiveButton(R.string.update_go,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    goToStore();
                                }
                            });

            if (isForceUpdate) {
                builder.setMessage(R.string.update_content_force)
                        .setCancelable(false);

                // Reset skipping
                PrefHelper.setLong(activity, "skipUpdate", 0);
            } else {
                if (PrefHelper.getLong(activity, "skipUpdate") >= System.currentTimeMillis()) {
                    // Still skipping time
                    return;
                }

                builder.setMessage(R.string.update_content)
                        .setCancelable(true)
                        .setNegativeButton(R.string.update_cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();

                                        long current = System.currentTimeMillis() + 2*24*60*60*1000;
                                        PrefHelper.setLong(activity, "skipUpdate", current);
                                    }
                                });
            }

            updateDialog = builder.show();
        } else {
            updateDialog.show();
        }
    }

    private void goToStore() {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            uri = Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName());
            activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    private void checkVersion(String versionString) {
        if (versionString == null || "".equals(versionString) || !versionString.contains("@")) {
            // Fail to connect
            createAndShowAlert();
            return;
        }

        Boolean update = parseVersion(versionString, getCurrentVersion());
        if (update != null) {
            isForceUpdate = update;
            showUpdatePopup();
        }
    }

    private int getCurrentVersion() {
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // Do nothing
        }
        return 0;
    }

    private Boolean parseVersion(String string, int currentVersion) {
        if (string != null) {
            int foundVersion = -1;
            boolean isForce = false;

            Pattern pattern = Pattern.compile("(\\d+)@(\\w+)");
            Matcher matcher = pattern.matcher(string);
            while (matcher.find()) {
                int code = Integer.parseInt(matcher.group(1));
                if (currentVersion <= code && (foundVersion == -1 || code < foundVersion)) {
                    // Found a version need to update
                    foundVersion = code;
                    isForce = "force".equalsIgnoreCase(matcher.group(2));
                }
            }

            if (foundVersion > 0) {
                // Found an update
                return isForce;
            }
        }
        return null;
    }

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.w("MyConnection", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return readIt(is, len);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            MyConnection.this.checkVersion(result);
        }
    }

}
