package com.example.iepirkumu_saraksts;

import android.content.Context;
import android.content.SharedPreferences;

/** Remembers the logged-in user so the app can skip the login screen. */
public final class Session {
    private static final String PREFS = "session";
    private static final String KEY_USER_ID = "userId";

    private Session() {}

    private static SharedPreferences prefs(Context c) {
        return c.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static void login(Context c, int userId) {
        prefs(c).edit().putInt(KEY_USER_ID, userId).apply();
    }

    public static void logout(Context c) {
        prefs(c).edit().remove(KEY_USER_ID).apply();
    }

    /** @return the stored user id, or -1 when nobody is logged in */
    public static int userId(Context c) {
        return prefs(c).getInt(KEY_USER_ID, -1);
    }
}
