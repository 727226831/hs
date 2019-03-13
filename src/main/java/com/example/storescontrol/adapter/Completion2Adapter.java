package com.example.storescontrol.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.storescontrol.R;
import com.example.storescontrol.bean.Completion1Bean;
import com.example.storescontrol.bean.MeterialBean;
import com.example.storescontrol.databinding.ItemCompletion1Binding;
import com.example.storescontrol.databinding.ItemCompletion2Binding;

import java.util.List;

public class Completion2Adapter extends  RecyclerView.Adapter<Completion2Adapter.RecyclerViewHolder> {
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemCompletion2Binding binding= DataBindingUtil.
                inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_completion2,null,false);

        return new RecyclerViewHolder(binding.getRoot());
    }

    private List<MeterialBean> mDatas;
    public Completion2Adapter(List<MeterialBean> data) {
        this.mDatas = data;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder vh, final int i) {

        ItemCompletion2Binding binding=DataBindingUtil.getBinding(vh.itemView);
        MeterialBean bean=mDatas.get(i);
        binding.setData(bean);
        binding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
    class  RecyclerViewHolder extends RecyclerView.ViewHolder{

        public RecyclerViewHolder(View view) {
            super(view);
        }



    }
}
