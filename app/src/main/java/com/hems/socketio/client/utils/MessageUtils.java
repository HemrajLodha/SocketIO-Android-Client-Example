package com.hems.socketio.client.utils;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.hems.socketio.client.R;


/**
 * Created by intel on 9/9/2016.
 */
public class MessageUtils
{
    private static Snackbar actionSnackBar;

    public static void snackBarWithAction(Context context, View root, String errorMsg, int color)
    {
        actionSnackBar = Snackbar.make(root, errorMsg, Snackbar.LENGTH_INDEFINITE);
        View snackbarView = actionSnackBar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context,color));
        actionSnackBar.setActionTextColor(ContextCompat.getColor(context,android. R.color.white));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context,android.R.color.white));
        actionSnackBar.setAction(R.string.ok
                , new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                actionSnackBar.dismiss();

            }
        });
        actionSnackBar.show();
    }

    public static void snackBarWithoutAction(Context context,View root,String errorMsg,int color)
    {
        actionSnackBar = Snackbar.make(root, errorMsg, Snackbar.LENGTH_SHORT);
        View snackbarView = actionSnackBar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context,color));
        actionSnackBar.setActionTextColor(ContextCompat.getColor(context,android.R.color.white));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context,android.R.color.white));
        actionSnackBar.show();
    }

    public static void hideSnackBar()
    {
        if(actionSnackBar != null)
            actionSnackBar.dismiss();
    }

    public static void playNotificationRingtone(Context context){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
