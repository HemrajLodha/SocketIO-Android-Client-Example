package com.hems.socketio.client.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.hems.socketio.client.R;
import com.hems.socketio.client.base.BaseRecyclerAdapter;
import com.hems.socketio.client.enums.MessageType;
import com.hems.socketio.client.interfaces.OnItemClickListener;
import com.hems.socketio.client.model.Message;
import com.hems.socketio.client.utils.ImageUtil;
import com.hems.socketio.client.utils.SessionManager;

import java.util.ArrayList;

/**
 * Created by intel on 04-Mar-17.
 */

public class ChatRecyclerAdapter extends BaseRecyclerAdapter<ChatRecyclerAdapter.ViewHolder, Message> {
    private static final int TYPE_ITEM_TEXT_ME = 0, TYPE_ITEM_TEXT_YOU = 1, TYPE_ITEM_IMAGE_ME = 2, TYPE_ITEM_IMAGE_YOU = 3;
    private SessionManager sessionManager;

    public ChatRecyclerAdapter(Context context, ArrayList<Message> items, OnItemClickListener onClickListener) {
        super(context, items, onClickListener);
        sessionManager = SessionManager.newInstance(context);
    }

    private class ImageViewHolder extends ViewHolder {

        ImageView imgMessage;

        ImageViewHolder(View view, OnItemClickListener onClickListener) {
            super(view, onClickListener);
            imgMessage = (ImageView) view.findViewById(R.id.image);
        }

        @Override
        public void bindData(Message data) {
            super.bindData(data);
            //Bitmap bitmap = ImageUtil.decodeImage(data.getMessage());
            //imgMessage.setImageBitmap(bitmap);
            if (data.getImageUri() != null) {
                Glide.with(getContext()).load(data.getImageUri()).into(imgMessage);
            } else if (!TextUtils.isEmpty(data.getImageUrl())) {
                Glide.with(getContext()).load(data.getImageUrl()).into(imgMessage);
            }
        }
    }

    private class TextViewHolder extends ViewHolder {

        TextView tvMessage;

        TextViewHolder(View view, OnItemClickListener onClickListener) {
            super(view, onClickListener);
            tvMessage = (TextView) view.findViewById(R.id.et_message);
        }

        @Override
        public void bindData(Message data) {
            super.bindData(data);
            tvMessage.setText(data.getChatMessage());
        }
    }


    class ViewHolder extends BaseRecyclerAdapter<BaseRecyclerAdapter.ViewHolder, Message>.ViewHolder {
        TextView tvName;

        ViewHolder(View view, OnItemClickListener onClickListener) {
            super(view, onClickListener);
            tvName = (TextView) view.findViewById(R.id.name);
        }

        @Override
        public void bindData(Message data) {
            tvName.setText(data.getSenderName());
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, getLayoutPosition());
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getMessageType() == MessageType.TEXT) {
            return sessionManager.getUserId().equals(getItem(position).getSenderId()) ? TYPE_ITEM_TEXT_ME : TYPE_ITEM_TEXT_YOU;
        } else {
            return sessionManager.getUserId().equals(getItem(position).getSenderId()) ? TYPE_ITEM_IMAGE_ME : TYPE_ITEM_IMAGE_YOU;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_ITEM_TEXT_ME:
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_me, parent, false);
                break;
            case TYPE_ITEM_TEXT_YOU:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_you, parent, false);
                break;
            case TYPE_ITEM_IMAGE_ME:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_video_me, parent, false);
                break;
            case TYPE_ITEM_IMAGE_YOU:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_video_you, parent, false);
                break;
        }
        return onCreateViewHolder(view, viewType);
    }

    @Override
    protected ViewHolder onCreateViewHolder(View view, int viewType) {
        switch (viewType) {
            default:
            case TYPE_ITEM_TEXT_ME:
            case TYPE_ITEM_TEXT_YOU:
                return new TextViewHolder(view, onItemClickListener);
            case TYPE_ITEM_IMAGE_ME:
            case TYPE_ITEM_IMAGE_YOU:
                return new ImageViewHolder(view, onItemClickListener);
        }
    }

}
