package com.hems.socketio.client.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by planet on 6/8/2017.
 */

public class Chat implements Parcelable {
    public static final String TYPE_USER_MESSAGE = "user-message";
    public static final String TYPE_CHAT = "chat";
    public static final int SUCCESS = 1, FAILED = 0, SENDING = 2;
    private String mReceiverId;
    private String mSenderId;
    private String mSenderName;
    private String mReceiverName;
    private String mMessage;
    private int mStatus;
    private String mType;
    private long mTime;

    private Chat() {
    }


    protected Chat(Parcel in) {
        mReceiverId = in.readString();
        mSenderId = in.readString();
        mSenderName = in.readString();
        mReceiverName = in.readString();
        mMessage = in.readString();
        mStatus = in.readInt();
        mType = in.readString();
        mTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mReceiverId);
        dest.writeString(mSenderId);
        dest.writeString(mSenderName);
        dest.writeString(mReceiverName);
        dest.writeString(mMessage);
        dest.writeInt(mStatus);
        dest.writeString(mType);
        dest.writeLong(mTime);
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

    public String getMessage() {
        return mMessage;
    }

    public String getReceiverId() {
        return mReceiverId;
    }

    public String getSenderId() {
        return mSenderId;
    }

    public String getSenderName() {
        return mSenderName;
    }

    public String getReceiverName() {
        return mReceiverName;
    }

    public long getTime() {
        return mTime;
    }

    public String getType() {
        return mType;
    }

    public static class Builder {
        private String mReceiverId;
        private String mSenderId;
        private String mSenderName;
        private String mReceiverName;
        private String mMessage;
        private int mStatus;
        private String mType;
        private long mTime;

        public Builder receiverId(String mReceiverId) {
            this.mReceiverId = mReceiverId;
            return this;
        }

        public Builder senderId(String mSenderId) {
            this.mSenderId = mSenderId;
            return this;
        }

        public Builder senderName(String mSenderName) {
            this.mSenderName = mSenderName;
            return this;
        }

        public Builder receiverName(String mReceiverName) {
            this.mReceiverName = mReceiverName;
            return this;
        }

        public Builder message(String mMessage) {
            this.mMessage = mMessage;
            return this;
        }

        public Builder type(String mType) {
            this.mType = mType;
            return this;
        }

        public Builder time(long mTime) {
            this.mTime = mTime;
            return this;
        }

        public Chat build() {
            Chat message = new Chat();
            message.mType = mType;
            message.mStatus = SENDING;
            message.mReceiverId = mReceiverId;
            message.mSenderId = mSenderId;
            message.mSenderName = mSenderName;
            message.mReceiverName = mReceiverName;
            message.mMessage = mMessage;
            message.mTime = mTime;
            return message;
        }
    }
}
