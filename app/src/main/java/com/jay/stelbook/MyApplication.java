package com.jay.stelbook;

import android.app.Application;

import cn.bmob.v3.Bmob;

/**
 * Created by Jay on 2016/7/9.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Bmob服务
        Bmob.initialize(getApplicationContext(),"1a35d7a79cfd31f748389205550e350f");
    }
}
