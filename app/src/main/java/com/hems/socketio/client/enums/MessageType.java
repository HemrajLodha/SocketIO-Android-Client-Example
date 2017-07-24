package com.hems.socketio.client.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Created by planet on 7/14/2017.
 */

public enum MessageType {
    DEFAULT(-1),
    @SerializedName("0")
    TEXT(0),
    @SerializedName("1")
    PICTURE(1),
    @SerializedName("2")
    AUDIO(2),
    @SerializedName("3")
    VIDEO(2);

    private int value;

    MessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MessageType getMessageType(int value) {
        for (MessageType type : MessageType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return DEFAULT;
    }
}
