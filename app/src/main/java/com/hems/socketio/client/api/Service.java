package com.hems.socketio.client.api;

/**
 * Created by planet on 7/5/2017.
 */

public interface Service {
    int SUCCESS = 1, FAILED = 0, ERROR = 2;
    String CHAT_IMAGE_URL = "http://192.168.100.23:3000/uploads/"; // uploaded images folder path
    String BASE_URL = "http://192.168.100.23:3000/";  // change to your ip/server address
    String CHAT_SERVICE_URL = BASE_URL; // change to your ip/server address
}
