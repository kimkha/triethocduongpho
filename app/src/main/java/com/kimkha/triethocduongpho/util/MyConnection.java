package com.kimkha.triethocduongpho.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.kimkha.triethocduongpho.app.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author kimkha
 * @version 1.3
 * @since 5/21/15
 */
public class MyConnection {
    private final MainActivity activity;
    private AlertDialog networkDialog;

    public MyConnection(MainActivity mainActivity) {
        activity = mainActivity;
    }

    public boolean checkNetworkAndShowAlert() {
        if (!isNetworkConnected()) {
            createAndShowAlert();
            return false;
        }

        new HttpAsyncTask().execute("http://triethocduongpho-android.appspot.com/version.txt");
        return true;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return null != ni;
    }

    private void createAndShowAlert() {
        if (networkDialog == null) {
            networkDialog = new AlertDialog.Builder(activity).setMessage("Please Check Your Internet Connection and Try Again")
                    .setTitle("Network Error")
                    .setCancelable(false)
                    .setNegativeButton("Retry",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    activity.restartActivity();
                                }
                            })
                    .setPositiveButton("Connect to WIFI",
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

    private void checkVersion(String versionString) {
        if (versionString == null || "".equals(versionString) || versionString.contains("@")) {
            // Fail to connect
            createAndShowAlert();
            return;
        }

        versionString = versionString.trim();
        String[] pair = versionString.split("@");

    }

    private String getCurrentVersion() {
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // Do nothing
        }
        return "";
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
            Log.d("MainActivity", "The response is: " + response);
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
