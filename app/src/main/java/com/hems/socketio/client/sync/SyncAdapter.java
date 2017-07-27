package com.hems.socketio.client.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.hems.socketio.client.api.ChatService;
import com.hems.socketio.client.api.RetrofitCall;
import com.hems.socketio.client.api.RetrofitCallback;
import com.hems.socketio.client.api.Service;
import com.hems.socketio.client.model.Chat;
import com.hems.socketio.client.model.Contact;
import com.hems.socketio.client.provider.DatabaseContract;
import com.hems.socketio.client.utils.DateUtils;
import com.hems.socketio.client.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapter.class.getSimpleName();
    private static final int MAX_PAGE_SIZE = 50;
    private int chatPageNo = 1;
    private boolean isSyncing = false;
    // Global variables
    // Define a variable to contain a content resolver instance
    private ContentResolver mContentResolver;
    private SessionManager sessionManager;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        sessionManager = SessionManager.newInstance(context);
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        if (isSyncing) {
            Log.w(TAG, "syncing...");
            return;
        }

        Log.w(TAG, "onPerformSync");
        // reset page no
        chatPageNo = 1;
        isSyncing = true;
        syncChat(syncResult);
        sessionManager.saveLastSyncDate(System.currentTimeMillis());
        if (!sessionManager.isFirstSyncDone()) {
            sessionManager.saveFirstSyncDone(true);
        }
        isSyncing = false;
        Log.w(TAG, "syncing done...");
    }

    /**
     * Performs synchronization of our pretend news feed source.
     *
     * @param syncResult Write our stats to this
     */
    private void syncChat(final SyncResult syncResult) {
        Log.i(TAG, "Fetching server entries...");
        final ChatService request = (ChatService) RetrofitCall.createRequest(ChatService.class);
        Call<Chat> call = request.getChatList(sessionManager.getUserId(), chatPageNo, MAX_PAGE_SIZE, sessionManager.getLastSyncDate(),
                sessionManager.isFirstSyncDone());
        try {
            Chat response = call.execute().body();
            if (response != null) {
                if (response.getStatus() == Service.SUCCESS) {
                    chatPageNo++;
                    if (response.getData().size() != 0) {
                        try {
                            syncChatInDatabase(response.getData(), syncResult);
                            syncChat(syncResult);
                        } catch (RemoteException | OperationApplicationException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.i(TAG, "no data to sync");
                    }
                } else {
                    Log.i(TAG, response.getMessage());
                }
            } else {
                Log.i(TAG, "Failed to sync data");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void syncChatInDatabase(ArrayList<Chat> chatList, SyncResult syncResult) throws RemoteException, OperationApplicationException {
        // Create list for batching ContentProvider transactions
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        for (Chat item : chatList) {
            syncResult.stats.numEntries++;
            Cursor cursorExist = mContentResolver.query(DatabaseContract.TableChat.CONTENT_URI,
                    DatabaseContract.TableChat.PROJECTION_ID,
                    DatabaseContract.TableChat.COLUMN_ID + "=?",
                    new String[]{item.getId()}, null, null);
            boolean isExist = false;
            if (cursorExist != null) {
                if (cursorExist.moveToFirst()) {
                    isExist = true;
                    if (item.isDeleted()) {
                        syncResult.stats.numDeletes++;
                        batch.add(ContentProviderOperation.newDelete(DatabaseContract.TableChat.CONTENT_URI)
                                .withSelection(DatabaseContract.TableChat.COLUMN_ID + "=?", new String[]{item.getId()})
                                .build());
                    } else {
                        syncResult.stats.numUpdates++;
                        batch.add(ContentProviderOperation.newUpdate(DatabaseContract.TableChat.CONTENT_URI)
                                .withSelection(DatabaseContract.TableChat.COLUMN_ID + "=?", new String[]{item.getId()})
                                .withValue(DatabaseContract.TableChat.COLUMN_NAME, item.getName())
                                .withValue(DatabaseContract.TableChat.COLUMN_USERS, TextUtils.join(",", item.getUsers()))
                                .withValue(DatabaseContract.TableChat.COLUMN_ADMIN_IDS, TextUtils.join(",", item.getAdmin_ids()))
                                .withValue(DatabaseContract.TableChat.COLUMN_LAST_MESSAGE_ID, item.getLastMessage())
                                .build());
                    }
                }
                cursorExist.close();
            }
            if (!isExist) {
                /*List<String> contacts = new ArrayList<>();
                for(Contact contact : item.getUsers()){
                    contacts.add(contact.getName());
                }*/
                syncResult.stats.numInserts++;
                batch.add(ContentProviderOperation.newInsert(DatabaseContract.TableChat.CONTENT_URI)
                        .withValue(DatabaseContract.TableChat.COLUMN_ID, item.getId())
                        .withValue(DatabaseContract.TableChat.COLUMN_NAME, item.getName())
                        .withValue(DatabaseContract.TableChat.COLUMN_USERS, TextUtils.join(",", item.getUsers()))
                        .withValue(DatabaseContract.TableChat.COLUMN_ADMIN_IDS, TextUtils.join(",", item.getAdmin_ids()))
                        .withValue(DatabaseContract.TableChat.COLUMN_LAST_MESSAGE_ID, item.getLastMessage())
                        .withValue(DatabaseContract.TableChat.COLUMN_TYPE, item.getType().getValue())
                        .withValue(DatabaseContract.TableChat.COLUMN_UPDATE_DATE, item.getUpdateDate())
                        .build());
            }
            // networkEntries.put(item.getId(), item);
        }


        /*// Compare the hash table of network entries to all the local entries
        Log.i(TAG, "Fetching local entries...");
        Cursor c = mContentResolver.query(DatabaseContract.TableChat.CONTENT_URI, null, null, null, null, null);
        assert c != null;
        c.moveToFirst();

        String id;
        String name;
        Chat found;
        for (int i = 0; i < c.getCount(); i++) {
            syncResult.stats.numEntries++;

            // Create local article entry
            id = c.getString(c.getColumnIndex(DatabaseContract.TableChat.COLUMN_NAME));
            name = c.getString(c.getColumnIndex(DatabaseContract.TableChat.COLUMN_NAME));

            // Try to retrieve the local entry from network entries
            found = networkEntries.get(id);
            if (found != null) {
                // The entry exists, remove from hash table to prevent re-inserting it
                networkEntries.remove(id);

                // Check to see if it needs to be updated
                if (!title.equals(found.getTitle())
                        || !content.equals(found.getContent())
                        || !link.equals(found.getLink())) {
                    // Batch an update for the existing record
                    Log.i(TAG, "Scheduling update: " + title);
                    batch.add(ContentProviderOperation.newUpdate(ArticleContract.Articles.CONTENT_URI)
                            .withSelection(ArticleContract.Articles.COL_ID + "='" + id + "'", null)
                            .withValue(ArticleContract.Articles.COL_TITLE, found.getTitle())
                            .withValue(ArticleContract.Articles.COL_CONTENT, found.getContent())
                            .withValue(ArticleContract.Articles.COL_LINK, found.getLink())
                            .build());
                    syncResult.stats.numUpdates++;
                }
            } else {
                // Entry doesn't exist, remove it from the local database
                Log.i(TAG, "Scheduling delete: " + title);
                batch.add(ContentProviderOperation.newDelete(ArticleContract.Articles.CONTENT_URI)
                        .withSelection(ArticleContract.Articles.COL_ID + "='" + id + "'", null)
                        .build());
                syncResult.stats.numDeletes++;
            }
            c.moveToNext();
        }
        c.close();

        // Add all the new entries
        for (Article article : networkEntries.values()) {
            Log.i(TAG, "Scheduling insert: " + article.getTitle());
            batch.add(ContentProviderOperation.newInsert(ArticleContract.Articles.CONTENT_URI)
                    .withValue(ArticleContract.Articles.COL_ID, article.getId())
                    .withValue(ArticleContract.Articles.COL_TITLE, article.getTitle())
                    .withValue(ArticleContract.Articles.COL_CONTENT, article.getContent())
                    .withValue(ArticleContract.Articles.COL_LINK, article.getLink())
                    .build());
            syncResult.stats.numInserts++;
        }
*/
        // Synchronize by performing batch update
        Log.i(TAG, "Merge solution ready, applying batch update...");
        mContentResolver.applyBatch(DatabaseContract.AUTHORITY, batch);
        mContentResolver.notifyChange(DatabaseContract.TableChat.CONTENT_URI, // URI where data was modified
                null, // No local observer
                false); // IMPORTANT: Do not sync to network
    }

    public static void performSync() {
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(SyncUtil.mAccount, DatabaseContract.AUTHORITY, b);
    }
}