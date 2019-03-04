package com.example.storescontrol.view.declaration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.storescontrol.R;
import com.example.storescontrol.Url.Request;
import com.example.storescontrol.Url.Untils;
import com.example.storescontrol.bean.MeterialBean;
import com.example.storescontrol.bean.MoInfoBean;
import com.example.storescontrol.databinding.ActivityMaterialBinding;
import com.example.storescontrol.view.BaseActivity;
import com.example.storescontrol.view.DetailListActivity;
import com.example.storescontrol.view.ScanActivity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaterialActivity extends BaseActivity {
    ActivityMaterialBinding binding;
    FunctionAdapter functionAdapter;
    List<MeterialBean> meterialBeanList=new ArrayList<>();

    MeterialBean bean;
    String cposcode="";
    String  stringScan;
    List<String> list;
    boolean isSuccess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_material);
        Untils.initTitle("投料明细",this);

        binding.etCwhcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {


                    getMoInventory(binding.etCwhcode.getText().toString());
                }

                return false;
            }
        });
        getMeteriallist();
        binding.ivScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  openScan();
            }
        });
        binding.bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i <meterialBeanList.size() ; i++) {

                    if(bean.getCinvcode().equals(meterialBeanList.get(i).getCinvcode())){

                        bean.setCposcode(cposcode);
                        meterialBeanList.set(i,bean);
                        functionAdapter=new FunctionAdapter(meterialBeanList);
                        binding.rvList.setAdapter(functionAdapter);
                        binding.etCwhcode.setText("");

                        SharedPreferences sharedPreferences = getSharedPreferences("sp", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString("Meteriallist",new Gson().toJson(meterialBeanList)).commit();

                    }
                }
                if(isSuccess){
                    Toast.makeText(MaterialActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MaterialActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
                }

            }
        });
        binding.rgCposcode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId==binding.rbCposcode.getId()){

                    cposcode="S010103";
                }else{
                    cposcode="S010104";

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                String code=result.getContents();
                Log.i("scan",code);
                binding.etCwhcode.setText(code);
                getMoInventory(binding.etCwhcode.getText().toString());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void openScan() {

        new IntentIntegrator(MaterialActivity.this)
                .setCaptureActivity(ScanActivity.class)
                .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                .setPrompt("请对准二维码")// 设置提示语
                .setCameraId(0)// 选择摄像头,可使用前置或者后置
                .setBeepEnabled(false)// 是否开启声音,扫完码之后会"哔"的一声
                .setBarcodeImageEnabled(true)// 扫完码之后生成二维码的图片
                .initiateScan();// 初始化扫码

    }

    private void getMeteriallist() {
        JSONObject jsonObject=new JSONObject();
        try {

            jsonObject.put("methodname","GetBeiliao");
            jsonObject.put("acccode",acccode);
            jsonObject.put("cmocode",getIntent().getStringExtra("cmocode"));
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
                        Gson gson = new Gson();
                        JsonArray arry = new JsonParser().parse(response.body().string()).getAsJsonArray();
                        for (JsonElement jsonElement : arry) {
                            meterialBeanList.add(gson.fromJson(jsonElement, MeterialBean.class));
                        }


                        binding.rvList.setLayoutManager(new LinearLayoutManager(MaterialActivity.this));
                        binding.rvList.addItemDecoration(new DividerItemDecoration(MaterialActivity.this,DividerItemDecoration.VERTICAL));
                        functionAdapter=new FunctionAdapter(meterialBeanList);
                        binding.rvList.setAdapter(functionAdapter);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            } });
    }

    private void getMoInventory(String code) {
        list=Untils.parseCode(code,1);
        Log.i("list-->",list.toString());


        if(list.isEmpty()){
            return;
        }

        JSONObject jsonObject=new JSONObject();
        try {

            jsonObject.put("methodname","GetMoInventory");
            jsonObject.put("acccode",acccode);
            jsonObject.put("cmocode",getIntent().getStringExtra("cmocode"));
            jsonObject.put("cinvcode",list.get(1));
            jsonObject.put("cbatch",list.get(2));
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
                        JSONArray jsonArray=new JSONArray(response.body().string());
                        bean=new Gson().fromJson(jsonArray.getJSONObject(0).toString(),MeterialBean.class);
                        bean.setCposcode("S010103");
                        bean.setIqty(list.get(4));
                        bean.setCbatch(list.get(2));
                        binding.tvCinvname.setText(bean.getCinvname());
                        binding.tvCinvcode.setText(bean.getCinvcode());
                        binding.tvCInvStd.setText(bean.getCinvstd());
                        binding.etIqty.setText(list.get(4));
                        binding.tvMoqty.setText(bean.getMoqty());
                        binding.tvCvenbatch.setText(bean.getCvenbatch());
                        binding.tvCbatch.setText(bean.getCbatch());

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
        public FunctionAdapter.VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v=getLayoutInflater().inflate(R.layout.item_meterial,viewGroup,false);
            return new FunctionAdapter.VH(v);
        }

        private List<MeterialBean> mDatas;
        public FunctionAdapter(List<MeterialBean> data) {
            this.mDatas = data;
        }

        @Override
        public void onBindViewHolder(@NonNull FunctionAdapter.VH vh, final int i) {
              vh.textViewcinvname.setText(mDatas.get(i).getCinvname());
              vh.textViewiqty.setText(mDatas.get(i).getIqty());
              vh.textViewcvenbatch.setText(mDatas.get(i).getCvenbatch());
              vh.textViewmoqty.setText(mDatas.get(i).getMoqty());
              vh.textViewcinvcode.setText(mDatas.get(i).getCinvcode());
              vh.textViewcInvStd.setText(mDatas.get(i).getCinvstd());
              vh.textViewcposcode.setText(mDatas.get(i).getCposcode());
              vh.textViewcbatch.setText(mDatas.get(i).getCbatch());


        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }
        class  VH extends RecyclerView.ViewHolder{
          TextView textViewcinvname,textViewiqty,textViewcbatch,textViewcvenbatch,textViewmoqty,
            textViewcinvcode,textViewcInvStd,textViewcposcode;
            public VH(@NonNull View itemView) {
                super(itemView);
                textViewcbatch=itemView.findViewById(R.id.tv_cbatch);
                textViewiqty=itemView.findViewById(R.id.tv_iqty);
                textViewcinvname=itemView.findViewById(R.id.tv_cinvname);
                textViewcvenbatch=itemView.findViewById(R.id.tv_cvenbatch);
                textViewmoqty=itemView.findViewById(R.id.tv_moqty);
                textViewcinvcode=itemView.findViewById(R.id.tv_cinvcode);
                textViewcInvStd=itemView.findViewById(R.id.tv_cInvStd);
                textViewcposcode=itemView.findViewById(R.id.tv_cposcode);

            }
        }
    }
}
