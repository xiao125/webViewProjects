package com.mc.weblib.aidl.mainpro;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mc.weblib.aidl.RemoteWebBinderPool;

/**
 * 主进程 MainProHandleRemoteService.
 */

public class MainProHandleRemoteService extends Service {

    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        int pid = android.os.Process.myPid();
        Log.d("", String.format("MainProHandleRemoteService: %s", "当前进程ID为："+pid+"----"+"客户端与服务端连接成功，服务端返回BinderPool.BinderPoolImpl 对象"));
        Binder mBinderPool = new RemoteWebBinderPool.BinderPoolImpl(context);
        return mBinderPool;
    }
}
