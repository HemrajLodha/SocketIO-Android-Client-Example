package com.hems.socketio.client.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by pws on 2/2/2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private Context context;


    public SQLiteHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.TableChat.CREATE_TABLE_QUERY);
        db.execSQL(DatabaseContract.TableContact.CREATE_TABLE_QUERY);
        db.execSQL(DatabaseContract.TableMessage.CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                Log.i("onUpgrade", oldVersion + "");
            case 2:
                Log.i("onUpgrade", oldVersion + "");
        }
    }

    /**
     * delete database
     *
     * @param context
     */
    public static void deleteDatabase(Context context) {
        context.getContentResolver().delete(DatabaseContract.TableChat.CONTENT_URI, null, null);
        context.getContentResolver().delete(DatabaseContract.TableContact.CONTENT_URI, null, null);
        context.getContentResolver().delete(DatabaseContract.TableMessage.CONTENT_URI, null, null);
    }
}

