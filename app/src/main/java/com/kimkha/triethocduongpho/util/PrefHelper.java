package com.kimkha.triethocduongpho.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author kimkha
 * @version 2.0
 * @since 5/22/15
 */
@SuppressWarnings("unused")
public class PrefHelper {
    public static boolean getBoolean(Activity activity, String key) {
        SharedPreferences pref = activity.getPreferences(Context.MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }

    public static void setBoolean(Activity activity, String key, boolean value) {
        SharedPreferences.Editor pref = activity.getPreferences(Context.MODE_PRIVATE).edit();
        pref.putBoolean(key, value);
        pref.apply();
    }

    public static long getLong(Activity activity, String key) {
        SharedPreferences pref = activity.getPreferences(Context.MODE_PRIVATE);
        return pref.getLong(key, 0);
    }

    public static void setLong(Activity activity, String key, long value) {
        SharedPreferences.Editor pref = activity.getPreferences(Context.MODE_PRIVATE).edit();
        pref.putLong(key, value);
        pref.apply();
    }

    public static int getInt(Activity activity, String key) {
        SharedPreferences pref = activity.getPreferences(Context.MODE_PRIVATE);
        return pref.getInt(key, 0);
    }

    public static void setInt(Activity activity, String key, int value) {
        SharedPreferences.Editor pref = activity.getPreferences(Context.MODE_PRIVATE).edit();
        pref.putInt(key, value);
        pref.apply();
    }
}
