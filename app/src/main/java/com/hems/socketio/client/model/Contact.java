package com.hems.socketio.client.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.hems.socketio.client.provider.DatabaseContract;

import java.util.ArrayList;

/**
 * Created by planet on 7/5/2017.
 */

public class Contact extends Response<ArrayList<Contact>> implements Parcelable {
    private String id, name, username, email, contact;
    private int age;
    private Contact meta;
    private boolean selected, deleted;
    private long update_date;


    public Contact() {
    }

    public Contact(Cursor cursor) {
        id = cursor.getString(DatabaseContract.TableContact.INDEX_COLUMN_USER_ID);
        name = cursor.getString(DatabaseContract.TableContact.INDEX_COLUMN_NAME);
        getMeta().email = cursor.getString(DatabaseContract.TableContact.INDEX_COLUMN_EMAIL);
        getMeta().age = cursor.getInt(DatabaseContract.TableContact.INDEX_COLUMN_AGE);
        getMeta().contact = cursor.getString(DatabaseContract.TableContact.INDEX_COLUMN_CONTACT);
        update_date = cursor.getLong(DatabaseContract.TableContact.INDEX_COLUMN_UPDATE_DATE);
    }

    protected Contact(Parcel in) {
        id = in.readString();
        name = in.readString();
        username = in.readString();
        email = in.readString();
        contact = in.readString();
        age = in.readInt();
        meta = in.readParcelable(Contact.class.getClassLoader());
        data = in.createTypedArrayList(Contact.CREATOR);
        selected = in.readByte() != 0;
        deleted = in.readByte() != 0;
        update_date = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(contact);
        dest.writeInt(age);
        dest.writeParcelable(meta, flags);
        dest.writeTypedList(data);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeByte((byte) (deleted ? 1 : 0));
        dest.writeLong(update_date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Contact getMeta() {
        if (meta == null) {
            meta = new Contact();
        }
        return meta;
    }

    public void setMeta(Contact meta) {
        this.meta = meta;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public long getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(long update_date) {
        this.update_date = update_date;
    }
}
