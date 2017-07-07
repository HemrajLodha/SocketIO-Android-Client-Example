package com.hems.socketio.client.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.hems.socketio.client.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pws-A on 3/6/2017.
 */

public abstract class BaseRecyclerAdapter<VH extends RecyclerView.ViewHolder, Data> extends RecyclerView.Adapter<VH> {
    private Context context;
    private ArrayList<Data> datas;
    public OnItemClickListener onItemClickListener;
    protected int listRes;

    protected abstract VH onCreateViewHolder(View view, int viewType);

    public BaseRecyclerAdapter(Context context, int listRes) {
        this(context, listRes, null, null);
    }

    public BaseRecyclerAdapter(Context context, ArrayList<Data> datas) {
        this(context, 0, datas, null);
    }

    public BaseRecyclerAdapter(Context context, int listRes, ArrayList<Data> datas) {
        this(context, listRes, datas, null);
    }

    public BaseRecyclerAdapter(Context context, ArrayList<Data> datas, OnItemClickListener onItemClickListener) {
        this(context, 0, datas, null);
    }


    public BaseRecyclerAdapter(Context context, int listRes, ArrayList<Data> datas, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.datas = datas;
        this.onItemClickListener = onItemClickListener;
        this.listRes = listRes;
    }

    public void setDatas(ArrayList<Data> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void setDatas(List<Data> datas) {
        this.datas = (ArrayList<Data>) datas;
        notifyDataSetChanged();
    }

    public void setDatas(Data[] datas) {
        this.datas = new ArrayList<>(Arrays.asList(datas));
        notifyDataSetChanged();
    }

    public void clearDataAndNotify() {
        this.datas.clear();
        notifyDataSetChanged();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ArrayList<Data> getDatas() {
        return datas;
    }

    public Data getItem(int position) {
        return datas.get(position);
    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (this.listRes != 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(listRes, parent, false);
            return onCreateViewHolder(view, viewType);
        }
        return onCreateViewHolder(null, viewType);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        ((ViewHolder) holder).bindData(getItem(position));
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final OnItemClickListener onItemClickListener;

        public ViewHolder(View view, OnItemClickListener onItemClickListener) {
            super(view);
            this.onItemClickListener = onItemClickListener;
            view.setOnClickListener(this);
        }

        public abstract void bindData(Data data);
    }

    public Context getContext() {
        return context;
    }
}
