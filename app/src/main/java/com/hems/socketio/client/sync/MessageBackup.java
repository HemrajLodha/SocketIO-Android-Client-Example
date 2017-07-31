package com.hems.socketio.client.sync;

import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import com.hems.socketio.client.api.ChatService;
import com.hems.socketio.client.api.MessageService;
import com.hems.socketio.client.api.RetrofitCall;
import com.hems.socketio.client.api.Service;
import com.hems.socketio.client.model.Chat;
import com.hems.socketio.client.model.Message;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by planet on 7/31/2017.
 */

public class MessageBackup {
    public static final int DATA_LIMIT = 20;
    public static final String TAG = MessageBackup.class.getSimpleName();
    private int pageNo = 1;
    private boolean isSyncing = false;

    public MessageBackup() {
        pageNo = 1;
    }



}
