package com.hems.socketio.client.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hems.socketio.client.enums.ChatType;
import com.hems.socketio.client.enums.MessageType;

import java.io.Serializable;

/**
 * Created by planet on 6/8/2017.
 */

public class Message extends Response<Message> implements Parcelable {
    public static final String TYPE_USER_MESSAGE = "user-message";
    public static final String TYPE_CHAT = "chat";
    public static final int SUCCESS = 1, FAILED = 0, SENDING = 2;
    @SerializedName("receiver_id")
    private String mReceiverId;
    @SerializedName("sender_id")
    private String mSenderId;
    @SerializedName("sender_name")
    private String mSenderName;
    @SerializedName("receiver_name")
    private String mReceiverName;
    @SerializedName("t_message")
    private String mMessage;
    private Uri imageUri;
    @SerializedName("image_url")
    private String imageUrl;
    private int mStatus;
    private ChatType mType;
    @SerializedName("message_type")
    private MessageType messageType;
    private long mTime;

    private Message() {
    }


    protected Message(Parcel in) {
        mReceiverId = in.readString();
        mSenderId = in.readString();
        mSenderName = in.readString();
        mReceiverName = in.readString();
        mMessage = in.readString();
        imageUri = in.readParcelable(Uri.class.getClassLoader());
        imageUrl = in.readString();
        mStatus = in.readInt();
        mType = ChatType.getChatType(in.readInt());
        messageType = MessageType.getMessageType(in.readInt());
        mTime = in.readLong();
        data = in.readParcelable(Message.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mReceiverId);
        dest.writeString(mSenderId);
        dest.writeString(mSenderName);
        dest.writeString(mReceiverName);
        dest.writeString(mMessage);
        dest.writeParcelable(imageUri, flags);
        dest.writeString(imageUrl);
        dest.writeInt(mStatus);
        dest.writeInt(mType.getValue());
        dest.writeInt(messageType.getValue());
        dest.writeLong(mTime);
        dest.writeParcelable(data, flags);
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

    public String getChatMessage() {
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

    public MessageType getMessageType() {
        return messageType;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public static class Builder {
        private String mReceiverId;
        private String mSenderId;
        private String mSenderName;
        private String mReceiverName;
        private String mMessage;
        private Uri imageUri;
        private String imageUrl;
        private int mStatus;
        private ChatType mType;
        private MessageType messageType;
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

        public Builder imageUri(Uri imageUri) {
            this.imageUri = imageUri;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder type(ChatType mType) {
            this.mType = mType;
            return this;
        }

        public Builder messageType(MessageType messageType) {
            this.messageType = messageType;
            return this;
        }

        public Builder time(long mTime) {
            this.mTime = mTime;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.mType = mType;
            message.messageType = messageType;
            message.mStatus = SENDING;
            message.mReceiverId = mReceiverId;
            message.mSenderId = mSenderId;
            message.mSenderName = mSenderName;
            message.mReceiverName = mReceiverName;
            message.mMessage = mMessage;
            message.imageUri = imageUri;
            message.imageUrl = imageUrl;
            message.mTime = mTime;
            return message;
        }
    }
}
