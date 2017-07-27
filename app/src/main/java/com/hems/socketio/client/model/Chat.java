package com.hems.socketio.client.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hems.socketio.client.enums.ChatType;
import com.hems.socketio.client.provider.DatabaseContract;

import java.util.ArrayList;

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
    private long updateDate;

    public Chat(Cursor cursor) {
        id = cursor.getString(DatabaseContract.TableChat.INDEX_COLUMN_CHAT_ID);
        type = ChatType.getChatType(cursor.getInt(DatabaseContract.TableChat.INDEX_COLUMN_TYPE)) ;
        name = cursor.getString(DatabaseContract.TableChat.INDEX_COLUMN_NAME);
        //users =  cursor.getString(DatabaseContract.TableChat.INDEX_COLUMN_USERS);
        //admin_ids =  cursor.getString(DatabaseContract.TableChat.INDEX_COLUMN_ADMIN_IDS);
        last_message_id = cursor.getString(DatabaseContract.TableChat.INDEX_COLUMN_LAST_MESSAGE_ID);
        updateDate = cursor.getLong(DatabaseContract.TableChat.INDEX_COLUMN_UPDATE_DATE);
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
}
