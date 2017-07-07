package com.hems.socketio.client.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by planet on 7/5/2017.
 */

public class User extends Response<User>  implements Parcelable {
    private String id,name,username,email,contact;
    private int age;
    private User meta;

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        username = in.readString();
        email = in.readString();
        contact = in.readString();
        age = in.readInt();
        meta = in.readParcelable(User.class.getClassLoader());
        data = in.readParcelable(User.class.getClassLoader());
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
        dest.writeParcelable(data, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
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

    public User getMeta() {
        return meta;
    }

    public void setMeta(User meta) {
        this.meta = meta;
    }
}
