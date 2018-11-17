package com.proxy;
import android.app.Application;

/**
 * 自定义Application
 */

public class McProxyApplication extends Application{
    private static McProxyApplication homeApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        homeApplication=this;
    }

    public static McProxyApplication getInstance(){
        return homeApplication;
    }

}
