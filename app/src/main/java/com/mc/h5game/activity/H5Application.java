package com.mc.h5game.activity;

import com.mc.h5game.x5.InitializeService;
import com.proxy.util.LogUtil;
import com.tencent.smtt.sdk.QbSdk;

import android.app.Application;

public class H5Application extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		InitializeService.start(this);
		QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
			@Override
			public void onViewInitFinished(boolean arg0) {
				// TODO Auto-generated method stub
				//x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
				LogUtil.log("x5內核初始化完成的回调");
			}

			@Override
			public void onCoreInitFinished() {

			}
		};

		//x5内核初始化接口
		QbSdk.initX5Environment(getApplicationContext(),  cb);

	}

}
