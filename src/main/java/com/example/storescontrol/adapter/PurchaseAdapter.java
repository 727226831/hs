package com.example.storescontrol.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.storescontrol.R;
import com.example.storescontrol.bean.PurchaselistBean;
import com.example.storescontrol.bean.TROutBywhcodeBean;
import com.example.storescontrol.databinding.ItemProductionListBinding;
import com.example.storescontrol.databinding.ItemPurchaseBinding;
import com.example.storescontrol.view.ProductionwarehousingActivity;

import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.VH> {
    @NonNull
    @Override
    public PurchaseAdapter.VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemPurchaseBinding binding= DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_purchase,viewGroup,false);

        return new  PurchaseAdapter.VH(binding.getRoot());
    }
    private List<PurchaselistBean.Data> mDatas;
    private  Context context;
    public PurchaseAdapter(List<PurchaselistBean.Data> data, Context context) {
        this.mDatas = data;
        this.context=context;
    }



    @Override
    public void onBindViewHolder(@NonNull PurchaseAdapter.VH vh, int i) {
        ItemPurchaseBinding itemPurchaseBinding=DataBindingUtil.getBinding(vh.itemView);

        itemPurchaseBinding.lInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(context, ProductionwarehousingActivity.class);
                intent.putExtra("menuname","采购入库");
               context.startActivity(intent);
            }
        });
        PurchaselistBean.Data data=mDatas.get(i);
        itemPurchaseBinding.setData(data);
        itemPurchaseBinding.executePendingBindings();


    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        public VH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
