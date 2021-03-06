package com.example.storescontrol.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.storescontrol.R;
import com.example.storescontrol.Url.Request;
import com.example.storescontrol.Url.iUrl;
import com.example.storescontrol.bean.ArrivalHeadBean;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PutListActivity extends BaseActivity {
    RecyclerView recyclerView;
    private  FunctionAdapter functionAdapter;
    Button buttonsubmit;
    private ImageView imageViewreturn;
    TextView textViewtitle,textViewtotal;
    List<ArrivalHeadBean> arrivalHeadBeans=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_list);
        recyclerView=findViewById(R.id.rv_list);
        textViewtitle=findViewById(R.id.tv_title);
        buttonsubmit=findViewById(R.id.b_submit);
        textViewtotal=findViewById(R.id.tv_total);
        textViewtitle.setText("入库列表");

        imageViewreturn=findViewById(R.id.iv_return);
        imageViewreturn.setVisibility(View.VISIBLE);
        imageViewreturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("sp", Context.MODE_PRIVATE);
        String data="";
        if(getIntent().getStringExtra("menuname").equals("采购入库")) {
             data= sharedPreferences.getString("CreatePuStoreInlist","");

        }else  if(getIntent().getStringExtra("menuname").equals("库存盘点")) {
             data= sharedPreferences.getString("CreateCheckdetailslist","");
            Log.i("data-->",data);
        }else  if(getIntent().getStringExtra("menuname").equals("生产入库")){
             data= sharedPreferences.getString("CreateProductStoreInlist","");

        }else  if(getIntent().getStringExtra("menuname").equals("货位调整")){
            data= sharedPreferences.getString("UpdatePositionTRlist","");
        }else  if(getIntent().getStringExtra("menuname").equals("采购到货")){
            data= sharedPreferences.getString("CreatePuArrivalInlist","");
        }

        if (!data.equals("")){

            try {
                Gson gson = new Gson();
                JsonArray arry = new JsonParser().parse(data).getAsJsonArray();
                for (JsonElement jsonElement : arry) {
                    arrivalHeadBeans.add(gson.fromJson(jsonElement, ArrivalHeadBean.class));
                }
                Log.i("arrivalHeadBeans",arrivalHeadBeans.size()+"");
                textViewtotal.setText("总计:"+arrivalHeadBeans.size()+"条");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }




        functionAdapter=new FunctionAdapter(arrivalHeadBeans);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(functionAdapter);
        buttonsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putData();

            }
        });

    }

    private void putData() {
        dialog.show();
        JSONObject jsonObject=new JSONObject();
        try {
            if(getIntent().getStringExtra("menuname").equals("采购入库")) {
                jsonObject.put("methodname","CreatePuStoreIn");
            }else  if(getIntent().getStringExtra("menuname").equals("库存盘点")) {
                jsonObject.put("methodname","CreateCheckdetails");
            }else  if(getIntent().getStringExtra("menuname").equals("生产入库")){
                jsonObject.put("methodname", "CreateProductStoreIn");
            }else  if(getIntent().getStringExtra("menuname").equals("货位调整")){
                jsonObject.put("methodname", "UpdatePositionTR");



            }else  if(getIntent().getStringExtra("menuname").equals("采购到货")){
                jsonObject.put("methodname", "CreatePuArrivalIn");
                jsonObject.put("cdefine10",getIntent().getStringExtra("cdefine10"));

            }
            jsonObject.put("usercode",usercode);
            jsonObject.put("acccode",acccode);
            Date curDate =  new Date(System.currentTimeMillis());
            SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyy-MM-dd");
            jsonObject.put("ddate",formatter.format(curDate));
            jsonObject.put("datatetails",new JSONArray(new Gson().toJson(arrivalHeadBeans)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String obj=jsonObject.toString();
        Log.i("json object",obj);
        retrofit2.Call<ResponseBody> data = Request.getRequestbody(obj);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    dialog.dismiss();
                    if(response.code()==200) {
                       JSONObject object=new JSONObject(response.body().string());
                       if(object.getString("Resultcode").equals("200")){

                           arrivalHeadBeans.clear();
                           functionAdapter.notifyDataSetChanged();
                           SharedPreferences sharedPreferences = getSharedPreferences("sp", Context.MODE_PRIVATE);
                           if(getIntent().getStringExtra("menuname").equals("采购入库")) {
                               sharedPreferences.edit().putString("CreatePuStoreInlist","").commit();
                               sharedPreferences.edit().putString("CreatePuStoreInscan","").commit();
                           }else  if(getIntent().getStringExtra("menuname").equals("库存盘点")) {

                               sharedPreferences.edit().putString("CreateCheckdetailslist","").commit();
                               sharedPreferences.edit().putString("CreateCheckdetailsscan","").commit();
                           }else  if(getIntent().getStringExtra("menuname").equals("生产入库")){
                               sharedPreferences.edit().putString("CreateProductStoreInlist","").commit();
                               sharedPreferences.edit().putString("CreateProductStoreInscan","").commit();
                           }else  if(getIntent().getStringExtra("menuname").equals("货位调整")){
                               sharedPreferences.edit().putString("UpdatePositionTRlist","").commit();
                               sharedPreferences.edit().putString("UpdatePositionTRscan","").commit();
                           }else  if(getIntent().getStringExtra("menuname").equals("采购到货")){

                               sharedPreferences.edit().putString("CreatePuArrivalInlist","").commit();
                               sharedPreferences.edit().putString("CreatePuArrivalInscan","").commit();
                           }



                       }
                       Toast.makeText(PutListActivity.this,object.getString("ResultMessage"),Toast.LENGTH_LONG).show();
                       textViewtotal.setText("");
                       finish();




                    }else if(response.code()==500){
                        Toast.makeText(PutListActivity.this,"数据错误，请检查",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            } });
    }

    class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.VH>{

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v=getLayoutInflater().inflate(R.layout.item_input,viewGroup,false);
            return new VH(v);


        }

        private List<ArrivalHeadBean> mDatas;
        public FunctionAdapter(List<ArrivalHeadBean> data) {
            this.mDatas = data;
        }

        @Override
        public void onBindViewHolder(@NonNull  VH vh,final int i) {
            vh.textViewnumber.setText(i+1+"");
            vh.textViewccode.setText(arrivalHeadBeans.get(i).getCcode());
            vh.textViewline.setText(arrivalHeadBeans.get(i).getIrowno());
            vh.textViewcposcode.setText("货位："+arrivalHeadBeans.get(i).getCposition());
            vh.textViewmaterial.setText("料号："+arrivalHeadBeans.get(i).getcInvCode());
            vh.textViewbatch.setText("批号："+arrivalHeadBeans.get(i).getCbatch());
            vh.textViewcount.setText("数量："+arrivalHeadBeans.get(i).getIquantity());

            if(arrivalHeadBeans.get(i).getFile()!=null){
                vh.imageViewpic.setTag(i+"");
                Picasso.get().load(new File(arrivalHeadBeans.get(i).getFile())).into(vh.imageViewpic);
            }
            vh.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(PutListActivity.this);
                    builder.setTitle("提示").setMessage("删除此条数据").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences sharedPreferences = getSharedPreferences("sp", Context.MODE_PRIVATE);
                            //delete sharedPreferences->scan item
                            String stringscandata="";
                            if(getIntent().getStringExtra("menuname").equals("采购入库")) {
                               stringscandata=sharedPreferences.getString("CreatePuStoreInscan","");
                            }else  if(getIntent().getStringExtra("menuname").equals("库存盘点")) {
                              stringscandata=sharedPreferences.getString("CreateCheckdetailsscan","");
                            }else  if(getIntent().getStringExtra("menuname").equals("生产入库")){
                               stringscandata=sharedPreferences.getString("CreateProductStoreInscan","");
                            }else  if(getIntent().getStringExtra("menuname").equals("货位调整")){
                                stringscandata=sharedPreferences.getString("UpdatePositionTRscan","");

                            }else  if(getIntent().getStringExtra("menuname").equals("采购到货")){
                                stringscandata=sharedPreferences.getString("CreatePuArrivalInscan","");
                            }

                            List<String> listcode = new ArrayList<>(Arrays.asList(stringscandata));
                            for (int j = 0; j <listcode.size() ; j++) {
                                if(listcode.get(j).contains(arrivalHeadBeans.get(i).getIrowno())){
                                    listcode.remove(j);
                                }
                            }
                            arrivalHeadBeans.remove(i);
                            functionAdapter.notifyDataSetChanged();
                            textViewtotal.setText("总计:"+arrivalHeadBeans.size()+"条");
                            //delete sharedPreferences->putlist item
                            String strings= new Gson().toJson(arrivalHeadBeans);
                             Log.i("list-->",strings);
                            if(getIntent().getStringExtra("menuname").equals("采购入库")) {

                                sharedPreferences.edit().putString("CreatePuStoreInlist",strings).commit();
                                sharedPreferences.edit().putString("CreatePuStoreInscan",listcode.toString()).commit();
                            }else  if(getIntent().getStringExtra("menuname").equals("库存盘点")) {

                                sharedPreferences.edit().putString("CreateCheckdetailslist",strings).commit();
                                sharedPreferences.edit().putString("CreateCheckdetailsscan",listcode.toString()).commit();
                            }else  if(getIntent().getStringExtra("menuname").equals("生产入库")){

                                sharedPreferences.edit().putString("CreateProductStoreInscanlist",strings).commit();
                                sharedPreferences.edit().putString("CreateProductStoreInscanscan",listcode.toString()).commit();
                            }else  if(getIntent().getStringExtra("menuname").equals("货位调整")){

                                sharedPreferences.edit().putString("UpdatePositionTRlist",strings).commit();
                                sharedPreferences.edit().putString("UpdatePositionTRscan",listcode.toString()).commit();

                            }else  if(getIntent().getStringExtra("menuname").equals("采购到货")){

                                sharedPreferences.edit().putString("CreatePuArrivalInlist",strings).commit();
                                sharedPreferences.edit().putString("CreatePuArrivalInscan",listcode.toString()).commit();
                            }



                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                }
            });


        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }
        class  VH extends RecyclerView.ViewHolder{
            TextView textViewnumber,textViewccode,textViewline,textViewcposcode,textViewmaterial,textViewbatch,textViewcount;
            LinearLayout linearLayout;
            ImageView imageViewpic;
            public VH(@NonNull View itemView) {
                super(itemView);
                linearLayout=itemView.findViewById(R.id.l_input);
                textViewnumber=itemView.findViewById(R.id.tv_number);
                textViewccode=itemView.findViewById(R.id.tv_ccode);
                textViewline=itemView.findViewById(R.id.tv_line);
                textViewcposcode=itemView.findViewById(R.id.tv_cposcode);
                textViewmaterial=itemView.findViewById(R.id.tv_material);
                textViewbatch=itemView.findViewById(R.id.tv_batch);
                textViewcount=itemView.findViewById(R.id.tv_count);
                imageViewpic=itemView.findViewById(R.id.iv_pic);


            }
        }
    }


}
