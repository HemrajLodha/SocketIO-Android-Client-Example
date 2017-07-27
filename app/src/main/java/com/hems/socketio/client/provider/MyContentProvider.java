package com.hems.socketio.client.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

public class MyContentProvider extends ContentProvider {
    SQLiteHelper sqLiteHelper;
    // used for the UriMacher
    private static final int CHAT = 1;
    private static final int CHAT_ID = 2;

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.TableChat.BASE_PATH, CHAT);
        sURIMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.TableChat.BASE_PATH + "/*", CHAT_ID);
    }

    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = sqLiteHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case CHAT:
                rowsDeleted = sqlDB.delete(DatabaseContract.TableChat.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case CHAT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(
                            DatabaseContract.TableChat.TABLE_NAME,
                            DatabaseContract.TableChat.COLUMN_ID + "='" + id + "'",
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(
                            DatabaseContract.TableChat.TABLE_NAME,
                            DatabaseContract.TableChat.COLUMN_ID + "='" + id + "'"
                                    + " AND " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = sqLiteHelper.getWritableDatabase();
        Uri returnUri = null;
        switch (uriType) {
            case CHAT:
                long id = sqlDB.insert(DatabaseContract.TableChat.TABLE_NAME, null, values);
                returnUri = Uri.parse(DatabaseContract.TableChat.BASE_PATH + "/" + id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        sqLiteHelper = new SQLiteHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        // check if the caller has requested a column which does not exists
        // Set the table
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case CHAT:
                queryBuilder.setTables(DatabaseContract.TableChat.TABLE_NAME);
                break;
            case CHAT_ID:
                // adding the ID to the original query
                queryBuilder.setTables(DatabaseContract.TableChat.TABLE_NAME);
                queryBuilder.appendWhere(DatabaseContract.TableChat.COLUMN_ID + "='"
                        + uri.getLastPathSegment() + "'");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = sqLiteHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case CHAT:
                rowsUpdated = sqlDB.update(DatabaseContract.TableChat.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CHAT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(DatabaseContract.TableChat.TABLE_NAME,
                            values,
                            DatabaseContract.TableChat.COLUMN_ID + "=?" ,
                            new String[]{id});
                } else {
                    rowsUpdated = sqlDB.update(DatabaseContract.TableChat.TABLE_NAME,
                            values,
                            DatabaseContract.TableChat.COLUMN_ID + "='" + id + "'"
                                    + " AND "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
