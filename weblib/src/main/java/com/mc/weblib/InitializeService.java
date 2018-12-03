package com.mc.weblib;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.mc.weblib.utils.TimeUtils;
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
                LogUtil.log("启动InitializeService成功");
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

        // 在调用TBS初始化、创建WebView之前进行如下配置，以开启优化方案 （多线程方案）
        HashMap<String, Object> map = new HashMap<String, Object>();
        // 配置不使用多进程策略，即该方案仅在Android 5.1+系统上生效。
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, false);
        QbSdk.initTbsSettings(map);

        initX5();
        preinitX5WebCore();
    }

    private void initX5() {
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.initX5Environment(this, cb);
    }

    private void preinitX5WebCore() {

        if(!QbSdk.isTbsCoreInited()) {
            // preinit只需要调用一次，如果已经完成了初始化，那么就直接构造view
            QbSdk.preInit(this, null);// 设置X5初始化完成的回调接口

        }
    }

    QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
        @Override
        public void onViewInitFinished(boolean arg0) {
            // TODO Auto-generated method stub
            //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
            LogUtil.log("x5內核初始化完成的回调："+arg0);
        }

        @Override
        public void onCoreInitFinished() {

        }
    };




}
