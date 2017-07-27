package com.hems.socketio.client.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by pws-A on 3/29/2017.
 */

public interface DatabaseContract {

    int DATABASE_VERSION = 1;
    String DATABASE_NAME = "socketptt.db";
    String AUTHORITY = "com.hems.socketio.client.provider";

    interface TableChat extends BaseColumns {

        String TABLE_NAME = "chat";
        String COLUMN_ID = "chat_id";
        String COLUMN_NAME = "chat_name";
        String COLUMN_USERS = "users";
        String COLUMN_ADMIN_IDS = "admin_ids";
        String COLUMN_LAST_MESSAGE_ID = "last_message_id";
        String COLUMN_TYPE = "type";
        String COLUMN_CREATE_DATE = "created_at";
        String COLUMN_UPDATE_DATE = "updated_at";

        String[] PROJECTION =
                {
                        _ID,
                        COLUMN_ID,
                        COLUMN_NAME,
                        COLUMN_USERS,
                        COLUMN_ADMIN_IDS,
                        COLUMN_LAST_MESSAGE_ID,
                        COLUMN_TYPE,
                        COLUMN_CREATE_DATE,
                        COLUMN_UPDATE_DATE
                };

        String[] PROJECTION_ID =
                {
                        _ID
                };

        String BASE_PATH = "chats";
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
                + "/" + BASE_PATH);
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + BASE_PATH;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + TABLE_NAME;

        String CREATE_TABLE_QUERY = "create table " + TABLE_NAME
                + " ( " + _ID + " INTEGER PRIMARY KEY, "
                + COLUMN_ID + " VARCHAR, "
                + COLUMN_NAME + " VARCHAR, "
                + COLUMN_USERS + " VARCHAR, "
                + COLUMN_ADMIN_IDS + " VARCHAR, "
                + COLUMN_LAST_MESSAGE_ID + " VARCHAR, "
                + COLUMN_TYPE + " INTEGER, "
                + COLUMN_CREATE_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + COLUMN_UPDATE_DATE + " INTEGER)";

        String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        int INDEX_COLUMN_ID = 0;
        int INDEX_COLUMN_CHAT_ID = 1;
        int INDEX_COLUMN_NAME = 2;
        int INDEX_COLUMN_USERS = 3;
        int INDEX_COLUMN_ADMIN_IDS = 4;
        int INDEX_COLUMN_LAST_MESSAGE_ID = 5;
        int INDEX_COLUMN_TYPE = 6;
        int INDEX_COLUMN_CREATE_DATE = 7;
        int INDEX_COLUMN_UPDATE_DATE = 8;
    }

}
