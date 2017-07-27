package com.hems.socketio.client;

import android.accounts.Account;
import android.app.Application;
import android.content.Context;

import com.hems.socketio.client.sync.SyncUtil;
import com.hems.socketio.client.utils.FileUtils;


public class MyApplication extends Application {

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FileUtils.createApplicationFolder();
    }
}
