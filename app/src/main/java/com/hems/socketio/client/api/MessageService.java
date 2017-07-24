package com.hems.socketio.client.api;

import com.hems.socketio.client.model.Chat;
import com.hems.socketio.client.model.Message;
import com.hems.socketio.client.model.Response;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by planet on 7/5/2017.
 */

public interface MessageService {

    @POST("/api/message")
    @Multipart
    Call<Message> sendPictureImage(@Part("sender_id") RequestBody senderId,
                                   @Part("sender_name") RequestBody senderName,
                                   @Part("receiver_id") RequestBody receiverId,
                                   @Part("message") RequestBody message,
                                   @Part("message_type") RequestBody messageType,
                                   @Part("event") RequestBody event,
                                   @Part MultipartBody.Part file);
}
