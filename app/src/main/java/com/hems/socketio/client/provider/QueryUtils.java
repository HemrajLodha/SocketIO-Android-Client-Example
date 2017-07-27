package com.hems.socketio.client.provider;

import android.content.Context;
import android.net.Uri;

/**
 * Created by planet on 6/13/2017.
 */

public final class QueryUtils {

    private static final String TAG = QueryUtils.class.getSimpleName();

    public static boolean deleteChat(Context context, String chatId) {
        Uri CONTENT_URI = Uri.parse(DatabaseContract.TableChat.CONTENT_URI + "/" + chatId);
        return (context.getContentResolver().delete(CONTENT_URI, null, null) == 1);
    }

}
