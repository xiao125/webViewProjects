package com.mc.h5game.activity;

import com.mc.h5game.util.TimeUtils;
import com.mc.h5game.x5.InitializeService;
import com.proxy.util.LogUtil;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import java.util.HashMap;

public class H5Application extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		Intent intent = new Intent(H5Application.this,InitializeService.class);
		startService(intent);

		// 在调用TBS初始化、创建WebView之前进行如下配置，以开启优化方案
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
		QbSdk.initTbsSettings(map);

		initX5();
		preinitX5WebCore();

	}

	private void initX5() {
		QbSdk.setDownloadWithoutWifi(true);
		QbSdk.initX5Environment(getApplicationContext(), cb);
	}

	private void preinitX5WebCore() {

		if(!QbSdk.isTbsCoreInited()) {
			// preinit只需要调用一次，如果已经完成了初始化，那么就直接构造view
			QbSdk.preInit(getApplicationContext(), null);// 设置X5初始化完成的回调接口

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

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		TimeUtils.beginTimeCalculate(TimeUtils.COLD_START);
	}
}
