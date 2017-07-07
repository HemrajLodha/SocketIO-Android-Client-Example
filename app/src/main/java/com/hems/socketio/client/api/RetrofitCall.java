package com.hems.socketio.client.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by planet on 7/5/2017.
 */

public class RetrofitCall {

    public static Object createRequest(Class className) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Service.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(className);
    }
}
