package com.hems.socketio.client.provider;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.hems.socketio.client.model.Chat;
import com.hems.socketio.client.model.Contact;
import com.hems.socketio.client.model.Message;

import java.util.ArrayList;

/**
 * Created by planet on 6/13/2017.
 */

public final class QueryUtils {

    private static final String TAG = QueryUtils.class.getSimpleName();

    public static boolean deleteChat(Context context, String chatId) {
        Uri CONTENT_URI = Uri.parse(DatabaseContract.TableChat.CONTENT_URI + "/" + chatId);
        return (context.getContentResolver().delete(CONTENT_URI, null, null) == 1);
    }

    public static void addChat(Context context, Chat chat) {
        Cursor cursorExists = context.getContentResolver().
                query(Uri.parse(DatabaseContract.TableChat.CONTENT_URI + "/" + chat.getId()),
                        DatabaseContract.TableChat.PROJECTION_ID, null, null, null);

        boolean isExists = false;
        if (cursorExists != null) {
            if (cursorExists.moveToFirst()) {
                isExists = true;
            }
            cursorExists.close();
        }
        if (!isExists) {
            String usersJson = new Gson().toJson(chat.getUsers());
            String adminIdsJson = new Gson().toJson(chat.getAdmin_ids());

            ContentValues cv = new ContentValues();
            cv.put(DatabaseContract.TableChat.COLUMN_ID, chat.getId());
            cv.put(DatabaseContract.TableChat.COLUMN_NAME, chat.getName());
            cv.put(DatabaseContract.TableChat.COLUMN_USERS, usersJson);
            cv.put(DatabaseContract.TableChat.COLUMN_ADMIN_IDS, adminIdsJson);
            cv.put(DatabaseContract.TableChat.COLUMN_LAST_MESSAGE_ID, chat.getLastMessage());
            cv.put(DatabaseContract.TableChat.COLUMN_TYPE, chat.getType().getValue());
            cv.put(DatabaseContract.TableChat.COLUMN_UPDATE_DATE, chat.getUpdateDate());
            context.getContentResolver().insert(DatabaseContract.TableChat.CONTENT_URI, cv);
            Log.i(TAG, "new chat inserted");
            context.getContentResolver().notifyChange(DatabaseContract.TableChat.CONTENT_URI_CHAT_WITH_LAST_MESSAGE, null);
        }
    }

    public static void addContact(Context context, ArrayList<Contact> contacts) {
        for (Contact contact : contacts) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseContract.TableContact.COLUMN_ID, contact.getId());
            cv.put(DatabaseContract.TableContact.COLUMN_NAME, contact.getName());
            cv.put(DatabaseContract.TableContact.COLUMN_AGE, contact.getMeta().getAge());
            cv.put(DatabaseContract.TableContact.COLUMN_EMAIL, contact.getMeta().getEmail());
            cv.put(DatabaseContract.TableContact.COLUMN_CONTACT, contact.getMeta().getContact());
            cv.put(DatabaseContract.TableContact.COLUMN_UPDATE_DATE, contact.getUpdate_date());

            Cursor cursorExists = context.getContentResolver().
                    query(Uri.parse(DatabaseContract.TableContact.CONTENT_URI + "/" + contact.getId()),
                            DatabaseContract.TableContact.PROJECTION_ID, null, null, null);

            boolean isExists = false;
            if (cursorExists != null) {
                if (cursorExists.moveToFirst()) {
                    isExists = true;
                }
                cursorExists.close();
            }
            if (isExists) {
                Log.i(TAG, "contact updated");
                context.getContentResolver().update(Uri.parse(DatabaseContract.TableContact.CONTENT_URI + "/" + contact.getId()), cv, null, null);
            } else {
                Log.i(TAG, "new contact inserted");
                context.getContentResolver().insert(DatabaseContract.TableContact.CONTENT_URI, cv);
            }
        }
    }

    /***
     * sync some message first time
     * @param context
     * @param chatId
     * @param messages
     */
    public static void saveLastMessages(Context context, String chatId, ArrayList<Message> messages) {
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();
        for (Message message : messages) {
            batch.add(ContentProviderOperation.newInsert(DatabaseContract.TableMessage.CONTENT_URI)
                    .withValue(DatabaseContract.TableMessage.COLUMN_ID, message.getId())
                    .withValue(DatabaseContract.TableMessage.COLUMN_SENDER_ID, message.getSenderId())
                    .withValue(DatabaseContract.TableMessage.COLUMN_SENDER_NAME, message.getSenderName())
                    .withValue(DatabaseContract.TableMessage.COLUMN_CHAT_ID, chatId)
                    .withValue(DatabaseContract.TableMessage.COLUMN_MESSAGE, message.getChatMessage())
                    .withValue(DatabaseContract.TableMessage.COLUMN_IMAGE_URL, message.getImageUrl())
                    .withValue(DatabaseContract.TableMessage.COLUMN_TYPE, message.getMessageType().getValue())
                    .withValue(DatabaseContract.TableMessage.COLUMN_CREATE_DATE, message.getTime())
                    .build());
        }
        try {
            context.getContentResolver().applyBatch(DatabaseContract.AUTHORITY, batch);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    /***
     * save message
     * @param context
     * @param message
     */
    public static void saveMessage(Context context, Message message) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.TableMessage.COLUMN_ID, message.getId());
        cv.put(DatabaseContract.TableMessage.COLUMN_SENDER_ID, message.getSenderId());
        cv.put(DatabaseContract.TableMessage.COLUMN_SENDER_NAME, message.getSenderName());
        cv.put(DatabaseContract.TableMessage.COLUMN_CHAT_ID, message.getReceiverId());
        cv.put(DatabaseContract.TableMessage.COLUMN_MESSAGE, message.getChatMessage());
        cv.put(DatabaseContract.TableMessage.COLUMN_IMAGE_URL, message.getImageUrl());
        cv.put(DatabaseContract.TableMessage.COLUMN_TYPE, message.getMessageType().getValue());
        cv.put(DatabaseContract.TableMessage.COLUMN_CREATE_DATE, message.getTime());
        context.getContentResolver().insert(DatabaseContract.TableMessage.CONTENT_URI, cv);
        // update last message id in chat table
        saveMessageIdInChat(context, message.getReceiverId(), message.getId(), message.getTime());
        // notify chat message uri
        context.getContentResolver().notifyChange(DatabaseContract.TableMessage.CONTENT_URI_CHAT_MESSAGES, null);
        context.getContentResolver().notifyChange(DatabaseContract.TableChat.CONTENT_URI_CHAT_WITH_LAST_MESSAGE, null);
    }

    /***
     * update last message id in chat table
     * @param context
     * @param chatId
     * @param messageId
     */
    public static void saveMessageIdInChat(Context context, String chatId, String messageId, long updateTime) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.TableChat.COLUMN_LAST_MESSAGE_ID, messageId);
        cv.put(DatabaseContract.TableChat.COLUMN_UPDATE_DATE, updateTime);
        context.getContentResolver().update(Uri.parse(DatabaseContract.TableChat.CONTENT_URI + "/" + chatId),
                cv, null, null);
    }
}
