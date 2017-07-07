package com.hems.socketio.client.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;


import com.hems.socketio.client.model.Chat;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Queue;

import io.socket.client.IO;
import io.socket.client.Socket;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static com.hems.socketio.client.api.Service.CHAT_SERVICE_URL;


/**
 * Created by planet on 6/8/2017.
 */

public class SocketIOService extends Service implements SocketEventListener.Listener, HeartBeat.HeartBeatListener {
    public static final String KEY_BROADCAST_MESSAGE = "b_message";
    public static final int EVENT_TYPE_JOIN = 1, EVENT_TYPE_MESSAGE = 2;
    private static final String EVENT_MESSAGE = "message";
    private static final String EVENT_JOIN = "join";
    private static final String EVENT_RECEIVED = "received";
    public static final String EXTRA_DATA = "extra_data_message";
    public static final String EXTRA_USER_NAME = "extra_user_name";
    public static final String EXTRA_EVENT_TYPE = "extra_event_type";
    private static final String TAG = SocketIOService.class.getSimpleName();
    private Socket mSocket;
    private Boolean isConnected = true;
    private boolean mTyping;
    private Queue<Chat> chatQueue;


    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private HeartBeat heartBeat;
    private String mUserId;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.arg1) {
                case 1:
                    Log.w(TAG, "Connected");
                    /*Toast.makeText(SocketIOService.this,
                            R.string.connect, Toast.LENGTH_LONG).show();*/
                    break;
                case 2:
                    Log.w(TAG, "Disconnected");
                    /*Toast.makeText(SocketIOService.this,ss
                            R.string.disconnect, Toast.LENGTH_LONG).show();*/
                    break;
                case 3:
                    Log.w(TAG, "Error in Connection");
                    /*Toast.makeText(SocketIOService.this,
                            R.string.error_connect, Toast.LENGTH_LONG).show();*/
                    break;
            }
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        chatQueue = new LinkedList<>();
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread(TAG + "Args",
                THREAD_PRIORITY_BACKGROUND);
        thread.start();
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        try {
            mSocket = IO.socket(CHAT_SERVICE_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        mSocket.on(Socket.EVENT_CONNECT, new SocketEventListener(Socket.EVENT_CONNECT, this));
        mSocket.on(Socket.EVENT_DISCONNECT, new SocketEventListener(Socket.EVENT_DISCONNECT, this));
        mSocket.on(Socket.EVENT_CONNECT_ERROR, new SocketEventListener(Socket.EVENT_CONNECT_ERROR, this));
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, new SocketEventListener(Socket.EVENT_CONNECT_TIMEOUT, this));
        mSocket.on(EVENT_MESSAGE, new SocketEventListener(EVENT_MESSAGE, this));
        mSocket.on(EVENT_RECEIVED, new SocketEventListener(EVENT_RECEIVED, this));
        /*mSocket.on("user joined", new SocketEventListener("user joined", this));
        mSocket.on("user left", new SocketEventListener("user left", this));
        mSocket.on("typing", new SocketEventListener("typing", this));
        mSocket.on("stop typing", new SocketEventListener("stop typing", this));*/
        mSocket.connect();
        heartBeat = new HeartBeat(this);
        heartBeat.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        if (intent != null) {
            Chat chat = intent.getParcelableExtra(EXTRA_DATA);
            int eventType = intent.getIntExtra(EXTRA_EVENT_TYPE, EVENT_TYPE_JOIN);
            if (eventType == EVENT_TYPE_JOIN) {
                mUserId = intent.getStringExtra(EXTRA_USER_NAME);
                if (!mSocket.connected()) {
                    mSocket.connect();
                    Log.i(TAG, "connecting socket...");
                } else {
                    joinChat();
                }
            } else if (chat != null) {
                if (!mSocket.connected()) {
                    chatQueue.add(chat);
                    mSocket.connect();
                    Log.i(TAG, "reconnecting socket...");
                } else {
                    sendMessage(chat);
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onHeartBeat() {
        if (mSocket != null && !mSocket.connected()) {
            mSocket.connect();
            Log.i(TAG, "connecting socket...");
        }
    }

    private void joinChat() {
        if (TextUtils.isEmpty(mUserId)) {
            // user name is null can not join chat
            return;
        }

        JSONObject data = new JSONObject();
        try {
            data.put("user_id", mUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(EVENT_JOIN, data);
        resendQueueMessages();
    }

    private void sendMessage(Chat message) {
        if (null == mSocket) return;
        JSONObject chat = new JSONObject();
        try {
            chat.put("sender_id", message.getSenderId());
            chat.put("receiver_id", message.getReceiverId());
            chat.put("message", message.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w(TAG, "message sent " + chat.toString());
        mSocket.emit(EVENT_MESSAGE, chat);
    }

    private void resendQueueMessages() {
        Chat chat = chatQueue.poll();
        if (chat != null) {
            sendMessage(chat);
            resendQueueMessages();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        heartBeat.stop();
        mUserId = null;
        // clear chat queue if service stop
        //chatQueue.clear();
        mSocket.off(Socket.EVENT_CONNECT, new SocketEventListener(Socket.EVENT_CONNECT, this));
        mSocket.off(Socket.EVENT_DISCONNECT, new SocketEventListener(Socket.EVENT_DISCONNECT, this));
        mSocket.off(Socket.EVENT_CONNECT_ERROR, new SocketEventListener(Socket.EVENT_CONNECT_ERROR, this));
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, new SocketEventListener(Socket.EVENT_CONNECT_TIMEOUT, this));
        mSocket.off(EVENT_MESSAGE, new SocketEventListener(EVENT_MESSAGE, this));
        mSocket.off(EVENT_RECEIVED, new SocketEventListener(EVENT_RECEIVED, this));
        Log.w(TAG, "onStop Service");
        /*mSocket.off("user joined", new SocketEventListener("user joined", this));
        mSocket.off("user left", new SocketEventListener("user left", this));
        mSocket.off("typing", new SocketEventListener("typing", this));
        mSocket.off("stop typing", new SocketEventListener("stop typing", this));*/
    }


    @Override
    public void onEventCall(String event, Object... args) {
        switch (event) {
            case Socket.EVENT_CONNECT:
                joinChat();
                android.os.Message msg = mServiceHandler.obtainMessage();
                msg.arg1 = 1;
                mServiceHandler.sendMessage(msg);
                isConnected = true;
                break;
            case Socket.EVENT_DISCONNECT:
                Log.w(TAG, "socket disconnected");
                isConnected = false;
                msg = mServiceHandler.obtainMessage();
                msg.arg1 = 2;
                mServiceHandler.sendMessage(msg);
                break;
            case Socket.EVENT_CONNECT_ERROR:
                isConnected = false;
                msg = mServiceHandler.obtainMessage();
                msg.arg1 = 3;
                mServiceHandler.sendMessage(msg);
                // reconnect
                mSocket.connect();
                break;
            case Socket.EVENT_CONNECT_TIMEOUT:
                if (!mTyping) return;

                mTyping = false;
                mSocket.emit("stop typing");
                break;
            case EVENT_MESSAGE:
                JSONObject data = (JSONObject) args[0];
                Log.w(TAG, "message : " + data.toString());
                try {
                    Intent intent = new Intent();
                    intent.setAction(KEY_BROADCAST_MESSAGE);
                    intent.putExtra("message", data.getString("message"));
                    intent.putExtra("receiver_id", data.getString("receiver_id"));
                    intent.putExtra("sender_id", data.getString("sender_id"));
                    sendBroadcast(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case EVENT_RECEIVED:
                data = (JSONObject) args[0];
                Log.w(TAG, "received : " + data.toString());
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
           /* case "user joined":
                data = (JSONObject) args[0];
                int numUsers;
                try {
                    username = data.getString("username");
                    numUsers = data.getInt("numUsers");
                    Log.w(TAG, "onUserJoined : " + username);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    return;
                }
                // TODO add to log
                break;
            case "user left":
                data = (JSONObject) args[0];
                try {
                    username = data.getString("username");
                    numUsers = data.getInt("numUsers");
                    Log.w(TAG, "onUserLeft : " + username);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    return;
                }
                //TODO
                break;
            case "typing":
                data = (JSONObject) args[0];
                try {
                    username = data.getString("username");
                    Log.w(TAG, "onTyping : " + username);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    return;
                }
                // TODO typing
                break;
            case "stop typing":
                data = (JSONObject) args[0];
                try {
                    username = data.getString("username");
                    Log.w(TAG, "onStopTyping : " + username);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    return;
                }
                // TODO typing
                break;*/
        }
    }
}
