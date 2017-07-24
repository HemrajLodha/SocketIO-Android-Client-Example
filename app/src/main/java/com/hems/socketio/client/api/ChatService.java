package com.hems.socketio.client.api;

import com.hems.socketio.client.model.Chat;
import com.hems.socketio.client.model.Contact;
import com.hems.socketio.client.model.Response;
import com.hems.socketio.client.model.User;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by planet on 7/5/2017.
 */

public interface ChatService {

    @POST("/api/chat")
    @FormUrlEncoded
    Call<Response<String>> createChat(@Field("user_id") String userId,@Field("type") Integer type, @Field("users") ArrayList<String> users);

    // get all chat list for user
    @GET("/api/chat")
    Call<Chat> getChatList(@Query("id") String userId);

    @FormUrlEncoded
    @HTTP(path = "/api/chat",method = "DELETE", hasBody = true)
    Call<Chat> deleteChat(@Field("user_id") String userId, @Field("chat_id") String chatId);
}
