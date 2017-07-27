package com.hems.socketio.client;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hems.socketio.client.adapter.ChatListRecyclerAdapter;
import com.hems.socketio.client.api.ChatService;
import com.hems.socketio.client.api.RetrofitCall;
import com.hems.socketio.client.api.RetrofitCallback;
import com.hems.socketio.client.api.Service;
import com.hems.socketio.client.enums.ChatType;
import com.hems.socketio.client.model.Chat;
import com.hems.socketio.client.provider.DatabaseContract;
import com.hems.socketio.client.provider.QueryUtils;
import com.hems.socketio.client.provider.SQLiteHelper;
import com.hems.socketio.client.service.SocketIOService;
import com.hems.socketio.client.sync.SyncAdapter;
import com.hems.socketio.client.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatListActivity extends AppCompatActivity
        implements ChatListRecyclerAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {
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
        SyncAdapter.performSync();
        getLoaderManager().initLoader(101, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_list_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.menu_title_logout);
            builder.setMessage("Are you sure want to logout?");
            builder.setPositiveButton(R.string.menu_title_logout, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SQLiteHelper.deleteDatabase(getApplicationContext());
                    sessionManager.logoutUser(ChatListActivity.this);
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void deleteGroupChat(final String chatId) {
        ChatService request = (ChatService) RetrofitCall.createRequest(ChatService.class);
        request.deleteChat(sessionManager.getUserId(), chatId).enqueue(new RetrofitCallback<Chat>() {
            @Override
            public void onResponse(Chat response) {
                if (response.getStatus() == Service.SUCCESS) {
                    Toast.makeText(ChatListActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatListActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
                mChat = null;
                QueryUtils.deleteChat(ChatListActivity.this, chatId);
            }

            @Override
            public void onFailure(Throwable t) {
                mChat = null;
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DatabaseContract.TableChat.CONTENT_URI,
                DatabaseContract.TableChat.PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<Chat> chats = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Chat chat = new Chat(cursor);
                    chats.add(chat);
                } while (cursor.moveToNext());
            }
        }
        adapter.setDatas(chats);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
