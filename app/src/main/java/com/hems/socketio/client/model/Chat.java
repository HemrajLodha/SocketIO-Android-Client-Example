package com.hems.socketio.client.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hems.socketio.client.enums.ChatType;

import java.util.ArrayList;

/**
 * Created by planet on 7/14/2017.
 */

public class Chat extends Response<ArrayList<Chat>> implements Parcelable {
    private String id;
    private ChatType type;
    private String name;
    private String[] users;
    @SerializedName("last_message")
    private String lastMessage;
    @SerializedName("update_date")
    private String updateDate;

    protected Chat(Parcel in) {
        id = in.readString();
        type = ChatType.getChatType(in.readInt());
        name = in.readString();
        users = in.createStringArray();
        lastMessage = in.readString();
        updateDate = in.readString();
        data = in.createTypedArrayList(Chat.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(type.getValue());
        dest.writeString(name);
        dest.writeStringArray(users);
        dest.writeString(lastMessage);
        dest.writeString(updateDate);
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getUsers() {
        return users;
    }

    public void setUsers(String[] users) {
        this.users = users;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
