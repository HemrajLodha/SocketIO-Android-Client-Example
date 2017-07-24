package com.hems.socketio.client;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hems.socketio.client.adapter.ChatRecyclerAdapter;
import com.hems.socketio.client.api.ChatService;
import com.hems.socketio.client.api.MessageService;
import com.hems.socketio.client.api.RetrofitCall;
import com.hems.socketio.client.api.RetrofitCallback;
import com.hems.socketio.client.api.Service;
import com.hems.socketio.client.enums.MessageType;
import com.hems.socketio.client.interfaces.OnItemClickListener;
import com.hems.socketio.client.model.Chat;
import com.hems.socketio.client.model.Message;
import com.hems.socketio.client.service.SocketIOService;
import com.hems.socketio.client.utils.FileUtils;
import com.hems.socketio.client.utils.ImageUtil;
import com.hems.socketio.client.utils.PermissionUtils;
import com.hems.socketio.client.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ChatActivity extends AppCompatActivity
        implements OnItemClickListener, View.OnClickListener {
    private static final int CHOOSE_FILE_REQUEST_CODE = 1001;
    private static final int TYPING_TIME_OUT = 2000;
    private static final String TAG = ChatActivity.class.getSimpleName();
    public static final String EXTRA_DATA = "extra_data";
    private RecyclerView recyclerView;
    private ChatRecyclerAdapter adapter;
    private ArrayList<Message> chatList;
    private EditText etMessage;
    private Button buttonUploadImage;
    private Button btnSend;
    private TextView tvTyping;
    private Chat mChat;
    private SessionManager sessionManager;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() == null) {
            Toast.makeText(this, "Message can't start", Toast.LENGTH_SHORT).show();
            finish();
        }

        sessionManager = SessionManager.newInstance(this);

        Intent service = new Intent(this, SocketIOService.class);
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_JOIN);
        service.putExtra(SocketIOService.EXTRA_USER_NAME, sessionManager.getUserId());
        startService(service);

        mChat = getIntent().getParcelableExtra(EXTRA_DATA);

        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mChat.getName());
        setSupportActionBar(toolbar);
        etMessage = (EditText) findViewById(R.id.et_message);
        tvTyping = (TextView) findViewById(R.id.typing);
        btnSend = (Button) findViewById(R.id.send);
        buttonUploadImage = (Button) findViewById(R.id.upload_image);
        buttonUploadImage.setOnClickListener(this);
        btnSend.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatList = new ArrayList<>();
        adapter = new ChatRecyclerAdapter(this, chatList, this);
        recyclerView.setAdapter(adapter);

        mHandler = new Handler();

        etMessage.addTextChangedListener(watcher);
    }


    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count > 0) {
                sendMessage(SocketIOService.EVENT_TYPE_TYPING, MessageType.TEXT);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send) {

            if (TextUtils.isEmpty(etMessage.getText().toString())) {
                return;
            }

            sendMessage(SocketIOService.EVENT_TYPE_MESSAGE, MessageType.TEXT, etMessage.getText().toString());

            etMessage.setText("");
        } else if (v.getId() == R.id.upload_image) {
            if (PermissionUtils.checkForPermission(this, PermissionUtils.PERMISSION_READ_STORAGE, PermissionUtils.PERMISSION_READ_STORAGE_REQ)) {
                // Use the GET_CONTENT intent from the utility class
                Intent target = FileUtils.createGetContentIntent(true);
                // Create the chooser Intent
                Intent intent = Intent.createChooser(
                        target, getString(R.string.chooser_title));
                try {
                    startActivityForResult(intent, CHOOSE_FILE_REQUEST_CODE);
                } catch (ActivityNotFoundException e) {
                    // The reason for the existence of aFileChooser
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_FILE_REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        sendMessage(SocketIOService.EVENT_TYPE_MESSAGE, MessageType.PICTURE, data.getData());
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendMessage(int eventType, MessageType messageType) {
        sendMessage(eventType, messageType, "", null);
    }

    private void sendMessage(int eventType, MessageType messageType, String message) {
        sendMessage(eventType, messageType, message, null);
    }

    private void sendMessage(int eventType, MessageType messageType, Uri imageUri) {
        sendMessage(eventType, messageType, "", imageUri);
    }

    private void sendMessage(int eventType, MessageType messageType, String message, Uri imageUri) {

        Message chat = new Message.Builder()
                .receiverId(mChat.getId())
                .senderId(sessionManager.getUserId())
                .senderName(sessionManager.getName())
                .receiverName(mChat.getName())
                .message(message)
                .imageUri(imageUri)
                .type(mChat.getType())
                .messageType(messageType)
                .time(System.currentTimeMillis())
                .build();

        Intent service = new Intent(this, SocketIOService.class);
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, eventType);
        service.putExtra(SocketIOService.EXTRA_DATA, chat);
        startService(service);

        if (eventType == SocketIOService.EVENT_TYPE_MESSAGE) {
            addItemToList(chat);
        }
    }

    private void addItemToList(Message chat) {
        chatList.add(chat);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(chatList.size() - 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(SocketIOService.KEY_BROADCAST_MESSAGE);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    Runnable typingRunnable = new Runnable() {
        @Override
        public void run() {
            tvTyping.setText("");
        }
    };


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SocketIOService.KEY_BROADCAST_MESSAGE)) {
                String senderId = intent.getStringExtra("sender_id");
                String senderName = intent.getStringExtra("sender_name");
                String receiverId = intent.getStringExtra("receiver_id");
                String message = intent.getStringExtra("message");
                String imageUrl = intent.getStringExtra("image_url");
                MessageType messageType = MessageType.getMessageType(intent.getIntExtra("message_type", MessageType.TEXT.getValue()));
                int event = intent.getIntExtra("event", -1);
                if (receiverId != null && receiverId.equals(mChat.getId())) {
                    switch (event) {
                        case SocketIOService.EVENT_TYPE_TYPING:
                            mHandler.removeCallbacks(typingRunnable);
                            tvTyping.setText(getString(R.string.typing_message, senderName));
                            mHandler.postDelayed(typingRunnable, TYPING_TIME_OUT);
                            break;
                        case SocketIOService.EVENT_TYPE_MESSAGE:
                            Message chat = new Message.Builder()
                                    .receiverId(receiverId)
                                    .senderId(senderId)
                                    .senderName(senderName)
                                    .receiverName(sessionManager.getName())
                                    .message(message)
                                    .imageUrl(imageUrl)
                                    .type(mChat.getType())
                                    .messageType(messageType)
                                    .time(System.currentTimeMillis())
                                    .build();
                            addItemToList(chat);
                            break;
                    }
                } else {
                    Log.i(TAG, "this message for another chat");
                }
            }
        }
    };


}
