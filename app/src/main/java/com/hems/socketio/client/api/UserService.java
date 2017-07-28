package com.hems.socketio.client.api;

import com.hems.socketio.client.model.Contact;
import com.hems.socketio.client.model.Response;
import com.hems.socketio.client.model.User;

import java.util.ArrayList;

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


    @POST("/api/contact")
    @FormUrlEncoded
    Call<Response<String>> createContact(@Field("user_id") String userId,@Field("users") ArrayList<String> users);

    @GET("/api/user")
    Call<Contact> getUserList(@Query("id") String userId);

    @GET("/api/contact")
    Call<Contact> getContactList(@Query("id") String userId,@Query("pageNo") int pageNo,@Query("limit") int limit,@Query("update_date") long update_date,@Query("deleted") boolean deleted);

    @POST("/api/login")
    @FormUrlEncoded
    Call<User> login(@Field("username") String username, @Field("password") String password);
}
