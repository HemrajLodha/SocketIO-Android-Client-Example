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
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.hems.socketio.client.api.ChatService;
import com.hems.socketio.client.api.RetrofitCall;
import com.hems.socketio.client.api.Service;
import com.hems.socketio.client.api.UserService;
import com.hems.socketio.client.model.Chat;
import com.hems.socketio.client.model.Contact;
import com.hems.socketio.client.provider.DatabaseContract;
import com.hems.socketio.client.utils.SessionManager;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class ChatSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = ChatSyncAdapter.class.getSimpleName();
    private static final int MAX_PAGE_SIZE = 50;
    private int pageNo = 1;
    private boolean isSyncing = false;
    // Global variables
    // Define a variable to contain a content resolver instance
    private ContentResolver mContentResolver;
    private SessionManager sessionManager;

    /**
     * Set up the sync adapter
     */
    public ChatSyncAdapter(Context context, boolean autoInitialize) {
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
    public ChatSyncAdapter(
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
        isSyncing = true;
        // reset page no
        pageNo = 1;
        syncChat(syncResult);
        pageNo = 1;
        syncContacts(syncResult);
        sessionManager.saveLastSyncDate(System.currentTimeMillis());
        if (!sessionManager.isFirstSyncDone()) {
            sessionManager.saveFirstSyncDone(true);
        }
        isSyncing = false;
        Log.w(TAG, "syncing done...");
    }

    /**
     * Performs synchronization of chats
     *
     * @param syncResult Write our stats to this
     */
    private void syncChat(final SyncResult syncResult) {
        Log.i(TAG, "Fetching chats entries...");
        final ChatService request = (ChatService) RetrofitCall.createRequest(ChatService.class);
        Call<Chat> call = request.getChatList(sessionManager.getUserId(), pageNo, MAX_PAGE_SIZE, sessionManager.getLastSyncDate(),
                sessionManager.isFirstSyncDone());
        try {
            Chat response = call.execute().body();
            if (response != null) {
                if (response.getStatus() == Service.SUCCESS) {
                    pageNo++;
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

    /**
     * Performs synchronization of chats
     *
     * @param syncResult Write our stats to this
     */
    private void syncContacts(final SyncResult syncResult) {
        Log.i(TAG, "Fetching contacts entries...");
        final UserService request = (UserService) RetrofitCall.createRequest(UserService.class);
        Call<Contact> call = request.getContactList(sessionManager.getUserId(), pageNo, MAX_PAGE_SIZE,
                sessionManager.getLastSyncDate(), sessionManager.isFirstSyncDone());
        try {
            Contact response = call.execute().body();
            if (response != null) {
                if (response.getStatus() == Service.SUCCESS) {
                    pageNo++;
                    if (response.getData().size() != 0) {
                        try {
                            syncContactsInDatabase(response.getData(), syncResult);
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
                        String usersJson = new Gson().toJson(item.getUsers());
                        String adminIdsJson = new Gson().toJson(item.getAdmin_ids());
                        batch.add(ContentProviderOperation.newUpdate(DatabaseContract.TableChat.CONTENT_URI)
                                .withSelection(DatabaseContract.TableChat.COLUMN_ID + "=?", new String[]{item.getId()})
                                .withValue(DatabaseContract.TableChat.COLUMN_NAME, item.getName())
                                .withValue(DatabaseContract.TableChat.COLUMN_USERS, usersJson)
                                .withValue(DatabaseContract.TableChat.COLUMN_ADMIN_IDS, adminIdsJson)
                                .withValue(DatabaseContract.TableChat.COLUMN_LAST_MESSAGE_ID, item.getLastMessage())
                                .withValue(DatabaseContract.TableChat.COLUMN_UPDATE_DATE, item.getUpdateDate())
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
                String usersJson = new Gson().toJson(item.getUsers());
                String adminIdsJson = new Gson().toJson(item.getAdmin_ids());
                batch.add(ContentProviderOperation.newInsert(DatabaseContract.TableChat.CONTENT_URI)
                        .withValue(DatabaseContract.TableChat.COLUMN_ID, item.getId())
                        .withValue(DatabaseContract.TableChat.COLUMN_NAME, item.getName())
                        .withValue(DatabaseContract.TableChat.COLUMN_USERS, usersJson)
                        .withValue(DatabaseContract.TableChat.COLUMN_ADMIN_IDS, adminIdsJson)
                        .withValue(DatabaseContract.TableChat.COLUMN_LAST_MESSAGE_ID, item.getLastMessage())
                        .withValue(DatabaseContract.TableChat.COLUMN_TYPE, item.getType().getValue())
                        .withValue(DatabaseContract.TableChat.COLUMN_UPDATE_DATE, item.getUpdateDate())
                        .build());
            }
            // networkEntries.put(item.getId(), item);
        }
        // Synchronize by performing batch update
        Log.i(TAG, "Merge solution ready, applying batch update...");
        mContentResolver.applyBatch(DatabaseContract.AUTHORITY, batch);
        mContentResolver.notifyChange(DatabaseContract.TableChat.CONTENT_URI, // URI where data was modified
                null, // No local observer
                false); // IMPORTANT: Do not sync to network
    }

    private void syncContactsInDatabase(ArrayList<Contact> contactList, SyncResult syncResult) throws RemoteException, OperationApplicationException {
        // Create list for batching ContentProvider transactions
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        for (Contact item : contactList) {
            syncResult.stats.numEntries++;
            Cursor cursorExist = mContentResolver.query(DatabaseContract.TableContact.CONTENT_URI,
                    DatabaseContract.TableContact.PROJECTION_ID,
                    DatabaseContract.TableContact.COLUMN_ID + "=?",
                    new String[]{item.getId()}, null, null);
            boolean isExist = false;
            if (cursorExist != null) {
                if (cursorExist.moveToFirst()) {
                    isExist = true;
                    if (item.isDeleted()) {
                        syncResult.stats.numDeletes++;
                        batch.add(ContentProviderOperation.newDelete(DatabaseContract.TableContact.CONTENT_URI)
                                .withSelection(DatabaseContract.TableContact.COLUMN_ID + "=?", new String[]{item.getId()})
                                .build());
                    } else {
                        syncResult.stats.numUpdates++;
                        batch.add(ContentProviderOperation.newUpdate(DatabaseContract.TableContact.CONTENT_URI)
                                .withSelection(DatabaseContract.TableContact.COLUMN_ID + "=?", new String[]{item.getId()})
                                .withValue(DatabaseContract.TableContact.COLUMN_NAME, item.getName())
                                .withValue(DatabaseContract.TableContact.COLUMN_AGE, item.getMeta().getAge())
                                .withValue(DatabaseContract.TableContact.COLUMN_EMAIL, item.getMeta().getEmail())
                                .withValue(DatabaseContract.TableContact.COLUMN_CONTACT, item.getMeta().getContact())
                                .withValue(DatabaseContract.TableContact.COLUMN_UPDATE_DATE, item.getUpdate_date())
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
                batch.add(ContentProviderOperation.newInsert(DatabaseContract.TableContact.CONTENT_URI)
                        .withValue(DatabaseContract.TableContact.COLUMN_ID, item.getId())
                        .withValue(DatabaseContract.TableContact.COLUMN_NAME, item.getName())
                        .withValue(DatabaseContract.TableContact.COLUMN_AGE, item.getMeta().getAge())
                        .withValue(DatabaseContract.TableContact.COLUMN_EMAIL, item.getMeta().getEmail())
                        .withValue(DatabaseContract.TableContact.COLUMN_CONTACT, item.getMeta().getContact())
                        .withValue(DatabaseContract.TableContact.COLUMN_UPDATE_DATE, item.getUpdate_date())
                        .build());
            }
            // networkEntries.put(item.getId(), item);
        }
        // Synchronize by performing batch update
        Log.i(TAG, "Merge solution ready, applying batch update...");
        mContentResolver.applyBatch(DatabaseContract.AUTHORITY, batch);
        mContentResolver.notifyChange(DatabaseContract.TableContact.CONTENT_URI, // URI where data was modified
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