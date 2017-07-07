package com.hems.socketio.client.service;

import android.os.Handler;

/**
 * Created by planet on 6/24/2017.
 */

public class HeartBeat implements Runnable {
    private static final int HEART_BEAT_RATE = 5000;
    private static final String TAG = HeartBeat.class.getSimpleName();
    private boolean isBeating;
    private Handler handler;
    private HeartBeatListener listener;

    public HeartBeat(HeartBeatListener listener) {
        handler = new Handler();
        this.listener = listener;
    }

    public void start() {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(this, HEART_BEAT_RATE);
        isBeating = true;
    }

    public void stop() {
        isBeating = false;
    }

    @Override
    public void run() {
       // Log.w(TAG, "" + isBeating);
        if (isBeating) {
            if (listener != null) {
                listener.onHeartBeat();
            }
            handler.postDelayed(this, HEART_BEAT_RATE);
        }
    }


    public interface HeartBeatListener {
        void onHeartBeat();
    }
}
