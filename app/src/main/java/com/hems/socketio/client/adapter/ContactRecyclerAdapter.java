package com.hems.socketio.client.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import com.hems.socketio.client.R;
import com.hems.socketio.client.base.BaseRecyclerAdapter;
import com.hems.socketio.client.interfaces.OnItemClickListener;
import com.hems.socketio.client.model.Contact;

import java.util.ArrayList;

/**
 * Created by intel on 04-Mar-17.
 */

public class ContactRecyclerAdapter extends BaseRecyclerAdapter<ContactRecyclerAdapter.ViewHolder, Contact> {

    public ContactRecyclerAdapter(Context context, ArrayList<Contact> items, OnItemClickListener onClickListener) {
        super(context, R.layout.contact_item, items, onClickListener);
    }

    class ViewHolder extends BaseRecyclerAdapter<BaseRecyclerAdapter.ViewHolder, Contact>.ViewHolder
            implements CompoundButton.OnCheckedChangeListener {
        TextView tvName, tvEmail;
        CheckBox cbSelected;

        public ViewHolder(View view, OnItemClickListener onClickListener) {
            super(view, onClickListener);
            tvName = (TextView) view.findViewById(R.id.name);
            tvEmail = (TextView) view.findViewById(R.id.email);
            cbSelected = (CheckBox) view.findViewById(R.id.checkbox);
            cbSelected.setOnCheckedChangeListener(this);
        }

        @Override
        public void bindData(Contact data) {
            tvName.setText(data.getName());
            tvEmail.setText(data.getMeta().getEmail());
            cbSelected.setOnCheckedChangeListener(null);
            cbSelected.setChecked(data.isSelected());
            cbSelected.setOnCheckedChangeListener(this);

            if (data.isSelected()) {
                itemView.setBackgroundColor(Color.parseColor("#DDDDDD"));
            } else {
                itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }

        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, getLayoutPosition());
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (onItemClickListener != null) {
                ((OnItemClickListener) onItemClickListener).onCheckedChange(buttonView, isChecked, getLayoutPosition());

            }
        }
    }


    @Override
    protected ViewHolder onCreateViewHolder(View view, int viewType) {
        return new ViewHolder(view, (OnItemClickListener) onItemClickListener);
    }

    public interface OnItemClickListener extends com.hems.socketio.client.interfaces.OnItemClickListener {
        void onCheckedChange(CompoundButton buttonView, boolean isChecked, int position);
    }

}
