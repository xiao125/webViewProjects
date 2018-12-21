package com.mc.weblib;

import android.app.Application;
import android.content.Context;

import com.mc.weblib.utils.TimeUtils;
import com.proxy.McProxyApplication;
import com.proxy.util.LogUtil;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import java.util.HashMap;

public class H5Application extends Application {


	public boolean isInit = false;

	private static H5Application H5Application;


	public static H5Application getInstance(){
		return H5Application;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		H5Application=this;
		InitializeService.start(this);

		/*// 在调用TBS初始化、创建WebView之前进行如下配置，以开启优化方案 （多线程方案）
		HashMap<String, Object> map = new HashMap<String, Object>();
		// 配置不使用多进程策略，即该方案仅在Android 5.1+系统上生效。
		map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, false);
		QbSdk.initTbsSettings(map);

		initX5();
		preinitX5WebCore();*/

	}

	/*public void initX5() {
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
			isInit = arg0;
		}

		@Override
		public void onCoreInitFinished() {

		}
	};

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		TimeUtils.beginTimeCalculate(TimeUtils.COLD_START);
	}*/
}
