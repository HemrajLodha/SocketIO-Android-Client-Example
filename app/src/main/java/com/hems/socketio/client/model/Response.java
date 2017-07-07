package com.hems.socketio.client.model;

/**
 * Created by planet on 7/5/2017.
 */

public class Response<T> {
    private int status;
    private String message;
    protected T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
