package com.lihao.rxjavademo;

import android.app.Application;

public class MApplication extends Application {

    private static MApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static MApplication getApp() {
        return app;
    }
}
