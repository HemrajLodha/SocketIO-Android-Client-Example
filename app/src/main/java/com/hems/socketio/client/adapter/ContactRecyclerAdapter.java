package com.hems.socketio.client.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;


import com.hems.socketio.client.R;
import com.hems.socketio.client.base.BaseRecyclerAdapter;
import com.hems.socketio.client.interfaces.OnItemClickListener;
import com.hems.socketio.client.model.Contact;

import java.util.ArrayList;

/**
 * Created by intel on 04-Mar-17.
 */

public class ContactRecyclerAdapter extends BaseRecyclerAdapter<ContactRecyclerAdapter.ViewHolder, Contact>
{

    public ContactRecyclerAdapter(Context context, ArrayList<Contact> items, OnItemClickListener onClickListener) {
        super(context, R.layout.contact_item, items, onClickListener);
    }

    class ViewHolder extends BaseRecyclerAdapter<BaseRecyclerAdapter.ViewHolder, Contact>.ViewHolder {
        TextView tvName, tvEmail;

        public ViewHolder(View view, OnItemClickListener onClickListener) {
            super(view, onClickListener);
            tvName = (TextView) view.findViewById(R.id.name);
            tvEmail = (TextView) view.findViewById(R.id.email);
        }

        @Override
        public void bindData(Contact data) {
            tvName.setText(data.getName());
            tvEmail.setText(data.getMeta().getEmail());
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
