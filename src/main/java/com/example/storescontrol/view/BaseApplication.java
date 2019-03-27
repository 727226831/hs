package com.example.storescontrol.view;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.tencent.bugly.Bugly;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "8a29361815", false);

    }

}
