package com.hems.socketio.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.hems.socketio.client.R;
import com.hems.socketio.client.base.BaseRecyclerAdapter;
import com.hems.socketio.client.interfaces.OnItemClickListener;
import com.hems.socketio.client.model.Message;
import com.hems.socketio.client.utils.SessionManager;

import java.util.ArrayList;

/**
 * Created by intel on 04-Mar-17.
 */

public class ChatRecyclerAdapter extends BaseRecyclerAdapter<ChatRecyclerAdapter.ViewHolder, Message> {
    private static final int TYPE_ITEM_ME = 0, TYPE_ITEM_YOU = 1;
    private SessionManager sessionManager;

    public ChatRecyclerAdapter(Context context, ArrayList<Message> items, OnItemClickListener onClickListener) {
        super(context, items, onClickListener);
        sessionManager = SessionManager.newInstance(context);
    }

    class ViewHolder extends BaseRecyclerAdapter<BaseRecyclerAdapter.ViewHolder, Message>.ViewHolder {
        TextView tvName, tvMessage;

        public ViewHolder(View view, OnItemClickListener onClickListener) {
            super(view, onClickListener);
            tvName = (TextView) view.findViewById(R.id.name);
            tvMessage = (TextView) view.findViewById(R.id.et_message);
        }

        @Override
        public void bindData(Message data) {
            tvName.setText(data.getSenderName());
            tvMessage.setText(data.getMessage());
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
        return sessionManager.getUserId().equals(getItem(position).getSenderId()) ? TYPE_ITEM_ME : TYPE_ITEM_YOU;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_ITEM_ME) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_me, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_you, parent, false);
        }
        return onCreateViewHolder(view, viewType);
    }

    @Override
    protected ViewHolder onCreateViewHolder(View view, int viewType) {
        return new ViewHolder(view, onItemClickListener);
    }

}
