package com.hems.socketio.client.api;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.hems.socketio.client.utils.FileUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hems.socketio.client.utils.FileUtils.getMimeType;

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

    @NonNull
    public static MultipartBody.Part prepareFilePart(Context context, String partName, Uri fileUri) {
        // create RequestBody instance from file
        File file = FileUtils.getFile(context, fileUri);
        RequestBody requestFile = RequestBody.create(
                MediaType.parse(getMimeType(context, fileUri)),
                file
        );
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    @NonNull
    public static RequestBody prepareStringPart(String params) {
        return RequestBody.create(
                MediaType.parse("text/plain"),
                params
        );
    }
}
