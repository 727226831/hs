package com.example.storescontrol.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.storescontrol.R;
import com.example.storescontrol.Url.Request;
import com.example.storescontrol.Url.Untils;
import com.example.storescontrol.adapter.PurchaseAdapter;
import com.example.storescontrol.bean.PurchaselistBean;
import com.example.storescontrol.bean.TROutBywhcodeBean;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaselistActivity extends BaseActivity {
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchaselist);
        Untils.initTitle(getIntent().getStringExtra("menuname"),this);
        recyclerView=findViewById(R.id.rv_list);
        getData();
    }
    private void getData() {

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("methodname","GetPuArrInfo");
            jsonObject.put("acccode",acccode);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        String obj=jsonObject.toString();
        Log.i("json object",obj);

        Call<ResponseBody> data = Request.getRequestbody(obj);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if(response.code()==200) {
                        PurchaselistBean purchaselistBean=new Gson().fromJson(response.body().string(),PurchaselistBean.class);
                        PurchaseAdapter purchaseAdapter=new PurchaseAdapter(purchaselistBean.getData(),PurchaselistActivity.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(PurchaselistActivity.this));
                        recyclerView.addItemDecoration(new DividerItemDecoration(PurchaselistActivity.this,DividerItemDecoration.VERTICAL));
                        recyclerView.setAdapter(purchaseAdapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            } });
    }

}
