package com.hems.socketio.client;

import android.accounts.Account;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.v4.media.session.MediaSessionCompat;

import com.hems.socketio.client.sync.SyncUtil;
import com.hems.socketio.client.utils.FileUtils;


public class MyApplication extends Application {

    private static Activity mActivity;

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FileUtils.createApplicationFolder();
    }

    /**
     * set chat activity instance on activity resume
     * @param activity
     */
    public static void setRunningActivity(Activity activity) {
        mActivity = activity;
    }

    /***
     * get chat activity instance if running
     * @return
     */
    public static Activity getRunningActivity() {
        return mActivity;
    }
}
