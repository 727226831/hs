package com.example.storescontrol.Url;


import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class Request {
    public  static String BASEURL="http://123456.vaiwan.com";
   //public  static String BASEURL="http://192.168.200.20:8881";
   // public  static String BASEURL="http://192.168.1.85:8881";

    public  static String URL;
    public static Call<ResponseBody> getRequestbody(String obj) {

         OkHttpClient client = new OkHttpClient.Builder().
                connectTimeout(60, TimeUnit.SECONDS).
                readTimeout(60, TimeUnit.SECONDS).
                writeTimeout(60, TimeUnit.SECONDS).build();
        Retrofit retrofit=new Retrofit.Builder().client(client).baseUrl(URL).build();

        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),obj);
        iUrl login = retrofit.create(iUrl.class);
        retrofit2.Call<ResponseBody> data = login.getMessage(body);
        return  data;
    }
}
