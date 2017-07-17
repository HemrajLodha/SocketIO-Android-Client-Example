package com.hems.socketio.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hems.socketio.client.adapter.ChatRecyclerAdapter;
import com.hems.socketio.client.interfaces.OnItemClickListener;
import com.hems.socketio.client.model.Chat;
import com.hems.socketio.client.model.Message;
import com.hems.socketio.client.service.SocketIOService;
import com.hems.socketio.client.utils.SessionManager;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements OnItemClickListener, View.OnClickListener {
    public static final String EXTRA_DATA = "extra_data";
    private RecyclerView recyclerView;
    private ChatRecyclerAdapter adapter;
    private ArrayList<Message> chatList;
    private EditText etMessage;
    private Button btnSend;
    private Chat mChat;
    private SessionManager sessionManager;

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
        btnSend = (Button) findViewById(R.id.send);
        btnSend.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatList = new ArrayList<>();
        adapter = new ChatRecyclerAdapter(this, chatList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send) {

            if (TextUtils.isEmpty(etMessage.getText().toString())) {
                return;
            }

            Message chat = new Message.Builder()
                    .receiverId(mChat.getId())
                    .senderId(sessionManager.getUserId())
                    .senderName(sessionManager.getName())
                    .receiverName(mChat.getName())
                    .message(etMessage.getText().toString())
                    .type(mChat.getType())
                    .time(System.currentTimeMillis())
                    .build();

            Intent service = new Intent(this, SocketIOService.class);
            service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_MESSAGE);
            service.putExtra(SocketIOService.EXTRA_DATA, chat);
            startService(service);

            addItemToList(chat);
            etMessage.setText("");
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SocketIOService.KEY_BROADCAST_MESSAGE)) {
                String senderId = intent.getStringExtra("sender_id");
                String receiverId = intent.getStringExtra("receiver_id");
                String message = intent.getStringExtra("message");
                Message chat = new Message.Builder()
                        .receiverId(receiverId)
                        .senderId(senderId)
                        .senderName(mChat.getName())
                        .receiverName(sessionManager.getName())
                        .message(message)
                        .type(mChat.getType())
                        .time(System.currentTimeMillis())
                        .build();
                addItemToList(chat);
            }
        }
    };
}
