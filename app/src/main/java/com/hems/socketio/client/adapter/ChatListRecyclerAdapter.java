package com.hems.socketio.client.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hems.socketio.client.R;
import com.hems.socketio.client.base.BaseRecyclerAdapter;
import com.hems.socketio.client.interfaces.OnItemClickListener;
import com.hems.socketio.client.model.Chat;

import java.util.ArrayList;

/**
 * Created by intel on 04-Mar-17.
 */

public class ChatListRecyclerAdapter extends BaseRecyclerAdapter<ChatListRecyclerAdapter.ViewHolder, Chat>
{
    public ChatListRecyclerAdapter(Context context, ArrayList<Chat> items, OnItemClickListener onClickListener) {
        super(context, R.layout.chat_item, items, onClickListener);
    }

    class ViewHolder extends BaseRecyclerAdapter<BaseRecyclerAdapter.ViewHolder, Chat>.ViewHolder {
        TextView tvName, tvMessage;

        public ViewHolder(View view, OnItemClickListener onClickListener) {
            super(view, onClickListener);
            tvName = (TextView) view.findViewById(R.id.name);
            tvMessage = (TextView) view.findViewById(R.id.message);
        }

        @Override
        public void bindData(Chat data) {
            tvName.setText(data.getName());
            tvMessage.setText(data.getLastMessage());
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, getLayoutPosition());
            }
        }
    }


    @Override
    protected ViewHolder onCreateViewHolder(View view, int viewType) {
        return new ViewHolder(view, onItemClickListener);
    }

}
