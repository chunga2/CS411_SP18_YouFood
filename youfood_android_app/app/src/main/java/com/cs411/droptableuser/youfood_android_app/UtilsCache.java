package com.cs411.droptableuser.youfood_android_app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class UtilsCache {
    private static final String PREFS_NAME = "YouFoodAppPrefs";
    private static final String NAME_KEY = "Name";
    private static final String EMAIL_KEY = "Email";
    private static final String IS_OWNER_KEY = "IsOwner";
    private static final String HAS_LOGGED_IN_KEY = "HasLoggedIn";
    private static final String PASSWORD_KEY = "Password";

    static Context context;
    static SharedPreferences prefs;

    static void initialize(Context aContext) {
        context = aContext;
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    static void storeName(String name) {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(NAME_KEY, name);
        prefsEditor.apply();
    }

    static void storeEmail(String email) {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(EMAIL_KEY, email);
        prefsEditor.apply();
    }

    static void storeIsOwner(boolean isOwner) {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(IS_OWNER_KEY, isOwner);
        prefsEditor.apply();
    }

    static void storeHasLoggedIn(boolean hasLoggedIn) {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(HAS_LOGGED_IN_KEY, hasLoggedIn);
        prefsEditor.apply();
    }

    static void storePassword(String password) {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(PASSWORD_KEY, password);
        prefsEditor.apply();
    }

    static String getName() {
        return prefs.getString(NAME_KEY, "");
    }

    static String getEmail() {
        return prefs.getString(EMAIL_KEY, "");
    }

    static boolean getIsOwner() {
        return prefs.getBoolean(IS_OWNER_KEY, false);
    }

    static boolean getHasLoggedIn() {
        return prefs.getBoolean(HAS_LOGGED_IN_KEY, false);
    }

    static String getPassword() {
        return prefs.getString(PASSWORD_KEY, "");
    }
}
