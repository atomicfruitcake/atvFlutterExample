package com.example.app;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static MyApplication mCurrentInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mCurrentInstance = this;
    }

    public static MyApplication instance() {
        return mCurrentInstance;
    }

    public static Context context() {
        return mCurrentInstance.getApplicationContext();
    }

}
