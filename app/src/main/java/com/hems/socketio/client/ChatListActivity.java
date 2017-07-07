package com.hems.socketio.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.hems.socketio.client.adapter.ContactRecyclerAdapter;
import com.hems.socketio.client.api.RetrofitCall;
import com.hems.socketio.client.api.RetrofitCallback;
import com.hems.socketio.client.api.Service;
import com.hems.socketio.client.api.UserService;
import com.hems.socketio.client.interfaces.OnItemClickListener;
import com.hems.socketio.client.model.Contact;
import com.hems.socketio.client.service.SocketIOService;
import com.hems.socketio.client.utils.SessionManager;

import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity implements OnItemClickListener {
    private RecyclerView recyclerView;
    private ContactRecyclerAdapter adapter;
    private ArrayList<Contact> list;
    private SessionManager sessionManager;

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
        adapter = new ContactRecyclerAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
        getContacts();
    }

    @Override
    public void onItemClick(View view, int position) {
        Contact contact = adapter.getItem(position);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_DATA, contact);
        startActivity(intent);
    }

    private void getContacts() {
        UserService request = (UserService) RetrofitCall.createRequest(UserService.class);
        request.getContactList(sessionManager.getUserName()).enqueue(new RetrofitCallback<Contact>() {
            @Override
            public void onResponse(Contact response) {
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
