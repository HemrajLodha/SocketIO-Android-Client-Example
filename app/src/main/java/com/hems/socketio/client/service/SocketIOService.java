package com.hems.socketio.client.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.hems.socketio.client.api.MessageService;
import com.hems.socketio.client.api.RetrofitCall;
import com.hems.socketio.client.api.RetrofitCallback;
import com.hems.socketio.client.enums.MessageType;
import com.hems.socketio.client.model.Chat;
import com.hems.socketio.client.model.Message;
import com.hems.socketio.client.provider.QueryUtils;
import com.hems.socketio.client.utils.FileUtils;
import com.hems.socketio.client.utils.MessageUtils;
import com.hems.socketio.client.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static com.hems.socketio.client.api.Service.CHAT_SERVICE_URL;
import static com.hems.socketio.client.api.Service.CHAT_IMAGE_URL;
import static com.hems.socketio.client.api.Service.SUCCESS;
import static com.hems.socketio.client.api.Service.FAILED;


/**
 * Created by planet on 6/8/2017.
 */

public class SocketIOService extends Service implements SocketEventListener.Listener, HeartBeat.HeartBeatListener {
    public static final String KEY_BROADCAST_MESSAGE = "b_message";
    public static final int EVENT_TYPE_JOIN = 1, EVENT_TYPE_MESSAGE = 2, EVENT_TYPE_TYPING = 3;
    private static final String EVENT_MESSAGE = "message";
    private static final String EVENT_JOIN = "join";
    private static final String EVENT_RECEIVED = "received";
    private static final String EVENT_TYPING = "typing";
    public static final String EXTRA_DATA = "extra_data_message";
    public static final String EXTRA_USER_NAME = "extra_user_name";
    public static final String EXTRA_EVENT_TYPE = "extra_event_type";
    private static final String TAG = SocketIOService.class.getSimpleName();
    private Socket mSocket;
    private Boolean isConnected = true;
    private boolean mTyping;
    private Queue<Message> chatQueue;


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
            Message chat = intent.getParcelableExtra(EXTRA_DATA);
            int eventType = intent.getIntExtra(EXTRA_EVENT_TYPE, EVENT_TYPE_JOIN);

            switch (eventType) {
                case EVENT_TYPE_JOIN:
                    mUserId = intent.getStringExtra(EXTRA_USER_NAME);
                    if (!mSocket.connected()) {
                        mSocket.connect();
                        Log.i(TAG, "connecting socket...");
                    } else {
                        joinChat();
                    }
                    break;
                case EVENT_TYPE_MESSAGE:
                    if (isSocketConnected()) {
                        if (chat.getMessageType() == MessageType.PICTURE) {
                            sendPictureImage(chat, eventType);
                        } else {
                            sendMessage(chat, eventType);
                        }
                        QueryUtils.saveMessage(this, chat);
                    } else {
                        chatQueue.add(chat);
                    }
                    break;
                case EVENT_TYPE_TYPING:
                    if (isSocketConnected()) {
                        sendMessage(chat, eventType);
                    }
                    break;
            }
        }
        return START_STICKY;
    }

    private boolean isSocketConnected() {
        if (null == mSocket) {
            return false;
        }
        if (!mSocket.connected()) {
            mSocket.connect();
            Log.i(TAG, "reconnecting socket...");
            return false;
        }

        return true;
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

    private void sendMessage(Message message, int event) {
        JSONObject chat = new JSONObject();
        try {
            chat.put("sender_id", message.getSenderId());
            chat.put("sender_name", message.getSenderName());
            chat.put("receiver_id", message.getReceiverId());
            if (!TextUtils.isEmpty(message.getChatMessage())) {
                chat.put("message", message.getChatMessage());
            }
            if (!TextUtils.isEmpty(message.getImageUrl())) {
                chat.put("image_url", CHAT_IMAGE_URL + message.getImageUrl());
            }
            chat.put("message_type", message.getMessageType().getValue());
            chat.put("event", event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w(TAG, "message sent " + chat.toString());
        mSocket.emit(EVENT_MESSAGE, chat);
    }

    private void resendQueueMessages() {
        Message chat = chatQueue.poll();
        if (chat != null) {
            sendMessage(chat, EVENT_TYPE_MESSAGE);
            resendQueueMessages();
        }
    }

    public void sendPictureImage(Message data, int eventType) {

        MessageService request = (MessageService) RetrofitCall.createRequest(MessageService.class);

        MultipartBody.Part fileBody = null;
        if (data.getImageUri() != null) {
            String path = FileUtils.getPath(this, data.getImageUri());
            Uri destUri = FileUtils.saveFile(path);
            fileBody = RetrofitCall.prepareFilePart(this, "image", data.getImageUri());
        }

        RequestBody senderId = RetrofitCall.prepareStringPart(data.getSenderId());
        RequestBody senderName = RetrofitCall.prepareStringPart(data.getSenderName());
        RequestBody receiverId = RetrofitCall.prepareStringPart(data.getReceiverId());
        RequestBody message = RetrofitCall.prepareStringPart(data.getChatMessage());
        RequestBody messageType = RetrofitCall.prepareStringPart(String.valueOf(data.getMessageType().getValue()));
        RequestBody event = RetrofitCall.prepareStringPart(String.valueOf(eventType));

        request.sendPictureImage(senderId,
                senderName,
                receiverId,
                message,
                messageType,
                event,
                fileBody).enqueue(new RetrofitCallback<Message>() {
            @Override
            public void onResponse(Message response) {
                if (response.getStatus() == SUCCESS) {
                    sendMessage(response.getData().get(0), EVENT_TYPE_MESSAGE);
                } else {
                    Toast.makeText(SocketIOService.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(SocketIOService.this, "Failed to send picture message", Toast.LENGTH_SHORT).show();
            }
        });
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
                    int messageEvent = data.getInt("event");
                    MessageType messageType = MessageType.getMessageType(data.getInt("message_type"));
                    String senderId = data.getString("sender_id");
                    String senderName = data.getString("sender_name");
                    String message = data.has("message") ? data.getString("message") : "";
                    String imageUrl = data.has("image_url") ? data.getString("image_url") : "";
                    String receiverId = data.getString("receiver_id");

                    if (messageEvent == EVENT_TYPE_MESSAGE) {
                        Message chat = new Message.Builder()
                                .receiverId(receiverId)
                                .senderId(senderId)
                                .senderName(senderName)
                                .message(message)
                                .imageUrl(imageUrl)
                                .messageType(messageType)
                                .time(System.currentTimeMillis())
                                .build();
                        QueryUtils.saveMessage(this, chat);
                        MessageUtils.playNotificationRingtone(getApplicationContext()); // play notification sound
                    }
                    intent.putExtra("receiver_id", receiverId);
                    intent.putExtra("sender_id", senderId);
                    intent.putExtra("sender_name", senderName);
                    intent.putExtra("event", messageEvent);
                    intent.putExtra("message_type", messageType.getValue());
                    sendBroadcast(intent);
                } catch (JSONException e) {
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
