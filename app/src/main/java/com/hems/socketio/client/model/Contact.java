package com.hems.socketio.client.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by planet on 7/5/2017.
 */

public class Contact extends Response<ArrayList<Contact>> implements Parcelable {
    private String id, name, username, email, contact;
    private int age;
    private Contact meta;
    private boolean selected;

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
}
