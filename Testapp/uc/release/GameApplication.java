package com.proxy.sdk.channel;

import android.app.Application;

/**
 *
 */
public class GameApplication extends Application {
    private static GameApplication proxyApplication;

    public static GameApplication getInstance(){
        return proxyApplication;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        proxyApplication=this;

    }
}
