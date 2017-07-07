package com.hems.socketio.client.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.hems.socketio.client.LoginActivity;

/**
 * Created by Rakesh on 12/27/2016.
 */
public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "SOCKET_IO_PREF";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_ID = "key_id";
    private static final String KEY_NAME = "key_name";
    private static final String KEY_USER_NAME = "key_user_name";

    public static SessionManager newInstance(Context context) {
        return new SessionManager(context);
    }

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String id, String name, String username) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_USER_NAME, username);
        // commit changes
        editor.commit();
    }

    public String getUserId() {
        return pref.getString(KEY_ID, null);
    }

    public String getName() {
        return pref.getString(KEY_NAME, null);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, null);
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity
                    .class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }
    }


    /**
     * Clear session details
     */
    @SuppressLint("NewApi")
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        _context.startActivity(i);
        //((ActivityManager) _context.getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void saveEmailInfo(String email) {
        editor.putString("emailInfo", email).commit();
    }

    public void savePhoneInfo(String phone) {
        editor.putString("phoneInfo", phone).commit();
    }

    public String getPhoneInfo() {
        return pref.getString("phoneInfo", null);
    }


    public void saveLoginSession(boolean result) {
        editor.putBoolean(IS_LOGIN, result).commit();
    }

}