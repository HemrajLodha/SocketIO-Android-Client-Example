package com.hems.socketio.client;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hems.socketio.client.adapter.ChatListRecyclerAdapter;
import com.hems.socketio.client.adapter.ContactRecyclerAdapter;
import com.hems.socketio.client.api.ChatService;
import com.hems.socketio.client.api.RetrofitCall;
import com.hems.socketio.client.api.RetrofitCallback;
import com.hems.socketio.client.api.Service;
import com.hems.socketio.client.api.UserService;
import com.hems.socketio.client.enums.ChatType;
import com.hems.socketio.client.interfaces.OnItemClickListener;
import com.hems.socketio.client.model.Chat;
import com.hems.socketio.client.model.Contact;
import com.hems.socketio.client.model.Response;
import com.hems.socketio.client.service.SocketIOService;
import com.hems.socketio.client.utils.SessionManager;

import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity implements ChatListRecyclerAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ChatListRecyclerAdapter adapter;
    private ArrayList<Chat> list;
    private SessionManager sessionManager;
    private Chat mChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = SessionManager.newInstance(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        Intent service = new Intent(this, SocketIOService.class);
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_JOIN);
        service.putExtra(SocketIOService.EXTRA_USER_NAME, sessionManager.getUserId());
        startService(service);

        setContentView(R.layout.activity_chat_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new ChatListRecyclerAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatListActivity.this, ContactListActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.menu_title_chat_options);
        menu.add(Menu.NONE, 101, 1, R.string.menu_title_delete_chat);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 101) {
            if (mChat.getType() == ChatType.PERSONAL) {
                Toast.makeText(this, "Personal chat can not be deleted!", Toast.LENGTH_SHORT).show();
            } else if (mChat != null) {
                deleteGroupChat(mChat.getId());
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getChatList();
    }

    private void getChatList() {
        list.clear();
        getChats();
    }

    @Override
    public void onItemClick(View view, int position) {
        Chat chat = adapter.getItem(position);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_DATA, chat);
        startActivity(intent);
    }

    @Override
    public void onLongClick(View v, int position) {
        mChat = adapter.getItem(position);
        registerForContextMenu(v);
    }

    private void deleteGroupChat(String chatId) {
        ChatService request = (ChatService) RetrofitCall.createRequest(ChatService.class);
        request.deleteChat(sessionManager.getUserId(), chatId).enqueue(new RetrofitCallback<Chat>() {
            @Override
            public void onResponse(Chat response) {
                if (response.getStatus() == Service.SUCCESS) {
                    Toast.makeText(ChatListActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    getChatList();
                } else {
                    Toast.makeText(ChatListActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
                mChat = null;
            }

            @Override
            public void onFailure(Throwable t) {
                mChat = null;
            }
        });
    }

    private void getChats() {
        ChatService request = (ChatService) RetrofitCall.createRequest(ChatService.class);
        request.getChatList(sessionManager.getUserId()).enqueue(new RetrofitCallback<Chat>() {
            @Override
            public void onResponse(Chat response) {
                if (response.getStatus() == Service.SUCCESS) {
                    list.addAll(response.getData());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ChatListActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
