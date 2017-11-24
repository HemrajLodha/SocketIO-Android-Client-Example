package com.hems.socketio.client.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.hems.socketio.client.enums.ChatType;
import com.hems.socketio.client.provider.DatabaseContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by planet on 7/14/2017.
 */

public class Chat extends DataResponse<ArrayList<Chat>> implements Parcelable {
    private String id;
    private ChatType type;
    private String name;
    private boolean deleted;
    private ArrayList<Contact> users;
    private ArrayList<String> admin_ids;
    private String last_message_id;
    @SerializedName("last_message")
    private String lastMessage;
    @SerializedName("update_date")
    private String imageUrl;
    private long updateDate;

    public Chat(Cursor cursor) {
        id = cursor.getString(0);
        name = cursor.getString(1);
        type = ChatType.getChatType(cursor.getInt(2));
        users = new Gson().fromJson(cursor.getString(3), new TypeToken<List<Contact>>(){}.getType());
        admin_ids = new Gson().fromJson(cursor.getString(4),new TypeToken<List<String>>(){}.getType());
        last_message_id = cursor.getString(5);
        updateDate = cursor.getLong(6);
        lastMessage = cursor.getString(7);
        imageUrl = cursor.getString(8);
    }

    protected Chat(Parcel in) {
        id = in.readString();
        type = ChatType.getChatType(in.readInt());
        name = in.readString();
        deleted = in.readByte() != 0;
        users = in.createTypedArrayList(Contact.CREATOR);
        admin_ids = in.createStringArrayList();
        last_message_id = in.readString();
        lastMessage = in.readString();
        imageUrl = in.readString();
        updateDate = in.readLong();
        data = in.createTypedArrayList(Chat.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(type.getValue());
        dest.writeString(name);
        dest.writeByte((byte) (deleted ? 1 : 0));
        dest.writeTypedList(users);
        dest.writeStringList(admin_ids);
        dest.writeString(last_message_id);
        dest.writeString(lastMessage);
        dest.writeString(imageUrl);
        dest.writeLong(updateDate);
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

    public ArrayList<Contact> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<Contact> users) {
        this.users = users;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public ArrayList<String> getAdmin_ids() {
        return admin_ids;
    }

    public void setAdmin_ids(ArrayList<String> admin_ids) {
        this.admin_ids = admin_ids;
    }

    public String getLast_message_id() {
        return last_message_id;
    }

    public void setLast_message_id(String last_message_id) {
        this.last_message_id = last_message_id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
