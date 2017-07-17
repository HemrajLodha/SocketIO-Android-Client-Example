package com.hems.socketio.client.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.hems.socketio.client.enums.ChatType;

/**
 * Created by planet on 6/8/2017.
 */

public class Message implements Parcelable {
    public static final String TYPE_USER_MESSAGE = "user-message";
    public static final String TYPE_CHAT = "chat";
    public static final int SUCCESS = 1, FAILED = 0, SENDING = 2;
    private String mReceiverId;
    private String mSenderId;
    private String mSenderName;
    private String mReceiverName;
    private String mMessage;
    private int mStatus;
    private ChatType mType;
    private long mTime;

    private Message() {
    }


    protected Message(Parcel in) {
        mReceiverId = in.readString();
        mSenderId = in.readString();
        mSenderName = in.readString();
        mReceiverName = in.readString();
        mMessage = in.readString();
        mStatus = in.readInt();
        mType = ChatType.getChatType(in.readInt());
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
        dest.writeInt(mType.getValue());
        dest.writeLong(mTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
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

    public ChatType getType() {
        return mType;
    }

    public static class Builder {
        private String mReceiverId;
        private String mSenderId;
        private String mSenderName;
        private String mReceiverName;
        private String mMessage;
        private int mStatus;
        private ChatType mType;
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

        public Builder type(ChatType mType) {
            this.mType = mType;
            return this;
        }

        public Builder time(long mTime) {
            this.mTime = mTime;
            return this;
        }

        public Message build() {
            Message message = new Message();
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
