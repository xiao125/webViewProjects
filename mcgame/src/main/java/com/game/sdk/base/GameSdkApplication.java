package com.game.sdk.base;
import android.app.Application;

/**
 * 自定义Application
 */

public class GameSdkApplication extends Application{
    private static GameSdkApplication homeApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        homeApplication=this;
    }

    public static GameSdkApplication getInstance(){
        return homeApplication;
    }

}
