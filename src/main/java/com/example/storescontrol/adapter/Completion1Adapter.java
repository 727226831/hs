package com.example.storescontrol.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.storescontrol.R;
import com.example.storescontrol.bean.Completion1Bean;
import com.example.storescontrol.bean.UnqualifiedBean;
import com.example.storescontrol.databinding.ItemCompletion1Binding;
import com.example.storescontrol.databinding.ItemUnqualifiedBinding;
import com.example.storescontrol.view.declaration.ReportprintActivity;

import java.util.List;

public class Completion1Adapter extends  RecyclerView.Adapter<Completion1Adapter.RecyclerViewHolder> {
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemCompletion1Binding binding= DataBindingUtil.
                inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_completion1,viewGroup,false);

        return new RecyclerViewHolder(binding.getRoot());
    }

    private List<Completion1Bean> mDatas;
    public Completion1Adapter(List<Completion1Bean> data) {
        this.mDatas = data;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder vh, final int i) {

        ItemCompletion1Binding  binding=DataBindingUtil.getBinding(vh.itemView);
         Completion1Bean bean=mDatas.get(i);
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
