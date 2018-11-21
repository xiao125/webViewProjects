package com.mc.h5game.x5;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.mc.h5game.activity.MainActivity;
import com.mc.h5game.activity.SplashActivity;
import com.proxy.util.LogUtil;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import java.io.File;
import java.util.HashMap;

import ren.yale.android.cachewebviewlib.CacheType;
import ren.yale.android.cachewebviewlib.ResourceInterceptor;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptor;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;
import ren.yale.android.cachewebviewlib.config.CacheExtensionConfig;

/**
 * Service进行第三方库初始化
 */

public class InitializeService extends IntentService {

    private static final String ACTION_INIT_WHEN_APP_CREATE = "H5.service.action.INIT";

    public InitializeService() {
        super("InitializeService");
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, InitializeService.class);
        intent.setAction(ACTION_INIT_WHEN_APP_CREATE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INIT_WHEN_APP_CREATE.equals(action)) {
                performInit();
            }
        }
    }

    private void performInit() {

        //初始化
        WebViewCacheInterceptor.Builder builder =  new WebViewCacheInterceptor.Builder(this);

        builder.setCachePath(new File(this.getCacheDir(),"webCache"))//设置缓存路径，默认getCacheDir，名称CacheWebViewCache
                .setCacheSize(1024*1024*300)  //设置缓存大小，默认100M
                .setConnectTimeoutSecond(20)  //设置http请求链接超时，默认20秒
                .setReadTimeoutSecond(20)     //设置http请求链接读取超时，默认20秒
                .setCacheType(CacheType.FORCE);

        CacheExtensionConfig extension = new CacheExtensionConfig();
        extension.addExtension("json")
                .addExtension("atlas")
                .addExtension("spb")
                .addExtension("ogg")
                .addExtension("mp3");
        builder.setCacheExtensionConfig(extension);
        builder.setAssetsDir("static");
        builder.setDebug(true);

        builder.setResourceInterceptor(new ResourceInterceptor() {
            @Override
            public boolean interceptor(String url) {
                return true;
            }
        });

        WebViewCacheInterceptorInst.getInstance().init(builder);
    }


}
