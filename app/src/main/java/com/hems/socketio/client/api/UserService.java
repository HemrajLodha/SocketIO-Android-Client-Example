package com.hems.socketio.client.api;

import com.hems.socketio.client.model.Contact;
import com.hems.socketio.client.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by planet on 7/5/2017.
 */

public interface UserService {

    @GET("/api/user")
    Call<Contact> getContactList(@Query("id") String id);

    @POST("/api/login")
    @FormUrlEncoded
    Call<User> login(@Field("username") String username, @Field("password") String password);
}
