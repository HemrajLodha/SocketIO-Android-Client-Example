package com.hems.socketio.client.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by planet on 7/6/2017.
 */

public abstract class RetrofitCallback<T> implements Callback<T> {

    public abstract void onResponse(T response);

    public abstract void onFailure(Throwable t);

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.code() == 200 && response.body() != null) {
            onResponse(response.body());
        } else {
            onFailure(new Throwable(response.message()));
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFailure(t);
    }
}
