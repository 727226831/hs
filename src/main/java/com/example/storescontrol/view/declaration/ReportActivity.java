package com.example.storescontrol.view.declaration;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.storescontrol.R;
import com.example.storescontrol.Url.Request;
import com.example.storescontrol.Url.Untils;
import com.example.storescontrol.bean.DetailsBean;
import com.example.storescontrol.bean.MoInfoBean;
import com.example.storescontrol.databinding.ActivityReportBinding;
import com.example.storescontrol.view.BaseActivity;
import com.example.storescontrol.view.DetailListActivity;
import com.example.storescontrol.view.ProductionwarehousingActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 完工报单
 */
public class ReportActivity extends BaseActivity {
    ActivityReportBinding binding;
    String  stringScan;
    List<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_report);
        Untils.initTitle("完工报单",this);

          binding.etPeople.addTextChangedListener(new TextWatcher() {
              @Override
              public void beforeTextChanged(CharSequence s, int start, int count, int after) {

              }

              @Override
              public void onTextChanged(CharSequence s, int start, int before, int count) {

              }

              @Override
              public void afterTextChanged(Editable s) {
                  if(handler.hasMessages(0)){
                      handler.removeMessages(0);
                  }
                  handler.sendEmptyMessageDelayed(0,1000);


              }
          });
          binding.etCode.setOnKeyListener(new View.OnKeyListener() {
              @Override
              public boolean onKey(View v, int keyCode, KeyEvent event) {

                  if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {

                      parseCode(binding.etCode.getText().toString());

                  }

                  return false;
              }
          });
          binding.bSubmit.setOnClickListener(onClickListener);
          binding.bMaterial.setOnClickListener(onClickListener);


    }
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
              switch (v.getId()){
                  case R.id.b_submit:
                      Toast.makeText(ReportActivity.this,"提交成功！",Toast.LENGTH_LONG).show();
                      startActivity(new Intent(ReportActivity.this,ReportprintActivity.class));
                      break;
                  case R.id.b_material:
                      startActivity(new Intent(ReportActivity.this,MaterialActivity.class));
                      break;
              }
        }
    };

    private void parseCode(String code){
        stringScan=code;
        if(code.isEmpty()){
            return;
        }

        String  numbers=code.replace("|",",");
        list = Arrays.asList(numbers.split(","));
        getMoInfo();

    }
    private void getMoInfo() {

        JSONObject jsonObject=new JSONObject();
        try {

            jsonObject.put("methodname","GetMoInfo");
            jsonObject.put("acccode",acccode);
            jsonObject.put("cmocode",list.get(1));
            jsonObject.put("cposname","测试工序");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String obj=jsonObject.toString();
        Log.i("json object",obj);

        Call<ResponseBody> data =Request.getRequestbody(obj);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if(response.code()==200) {
                        JSONArray jsonArray=new JSONArray(response.body().string());
                        MoInfoBean bean=new Gson().fromJson(jsonArray.getJSONObject(0).toString(),MoInfoBean.class);
                         binding.tvCmocode.setText(bean.getCmocode());
                         binding.tvCOpdesc.setText(bean.getCOpdesc());
                         binding.tvImoqty.setText(bean.getImoqty()+"");
                         binding.tvCInvName.setText(bean.getCInvName());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            } });
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            binding.etPeople.setText(binding.etPeople.getText().toString()+",");
            handler.removeMessages(0);
            binding.etPeople.requestFocus();
            binding.etPeople.setSelection(binding.etPeople.getText().length());

        }
    };


}
