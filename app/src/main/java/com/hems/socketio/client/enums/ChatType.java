package com.hems.socketio.client.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Created by planet on 7/14/2017.
 */

public enum ChatType {
    DEFAULT(-1),
    @SerializedName("0")
    PERSONAL(0),
    @SerializedName("1")
    GROUP(1),
    @SerializedName("2")
    BROADCAST(2);

    private int value;

    ChatType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ChatType getChatType(int value) {
        for (ChatType type : ChatType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return DEFAULT;
    }
}
