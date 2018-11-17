package com.proxy.sdk;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.proxy.OpenSDK;
import com.proxy.Constants;
import com.proxy.Data;
import com.proxy.Splash;
import com.proxy.bean.FuncButton;
import com.proxy.bean.KnPayInfo;
import com.proxy.listener.SplashListener;
import com.proxy.sdk.channel.SdkChannel;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;

public class SdkCenter {
	
	private static SdkCenter instance = null;
	
	private SdkProxy sdkProxy = null;
	private Data kndata = Data.getInstance();
	private boolean queried = false;
	private static Activity m_activity = null ;
	public SdkProxy getSdkProxy() {
		return sdkProxy;
	}

	public void setSdkProxy(SdkProxy sdkProxy) {
		this.sdkProxy = sdkProxy;
	}

	public static SdkCenter getInstance(){
		if(instance == null)
			instance = new SdkCenter();
		return instance;
	}
	
	public void onCreate(final Activity activity){
		m_activity = activity;
		setSdkProxy( SdkChannel.getInstance() );
		if(Util.getSplash(activity).equals("1")){
			LogUtil.e("闪屏=="+Util.getSplash(activity));
			LogUtil.e("gameId:"+kndata.getGameInfo().getGameId());
			LogUtil.e("channel:"+Util.getChannel(activity));
			LogUtil.e("直接优先执行SDK初始化++channel:"+Util.getChannle(activity));
			if(Util.getChannle(activity).equals("37wan")||Util.getChannle(activity).equals("")){
				sdkProxy.onCreate(activity);
			}else{
				//静态闪屏，获取打包系统配置闪屏图片
					if(kndata.getGameInfo().getGameId().equals("guhuozainew")){
							Splash.getInstance(activity).splash(true , new SplashListener() {
							@Override
							public void onSuccess(Object result) {
								// TODO Auto-generated method stub
								activity.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										sdkProxy.onCreate(activity);
									}
								});
							}
							@Override
							public void onFail(Object result) {
								// TODO Auto-generated method stub
							}
						});
						
					}else{
						LogUtil.log("splashSDK..............");
						Splash.getInstance(activity).splashSDK( new SplashListener() {
							@Override
							public void onSuccess(Object result) {
								// TODO Auto-generated method stub
								activity.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										sdkProxy.onCreate(activity);
									}
								});
							}
							@Override
							public void onFail(Object result) {
							}
						} );
						//sdkProxy.onCreate(activity);
					}
			}
		}else{
			sdkProxy.onCreate(activity);
		}
	}
	
	public boolean canEnterGame() {
		return sdkProxy.canEnterGame();
	}

	public void onEnterGame(Map<String, Object> data) {
		try {
			String userId1 	   = data.get(Constants.USER_ID)!=null?data.get(Constants.USER_ID).toString():"";
			String accountTpye = data.get(Constants.USER_ACCOUT_TYPE)!=null?data.get(Constants.USER_ACCOUT_TYPE).toString():"";
			String sex    	   = data.get(Constants.USER_SEX)!=null?data.get(Constants.USER_SEX).toString():"";
			String age		   = data.get(Constants.USER_AGE)!=null?data.get(Constants.USER_AGE).toString():"";
			String gameServer  = data.get(Constants.SERVER_NAME)!=null?data.get(Constants.SERVER_NAME).toString():"";
			String level       = data.get(Constants.USER_LEVEL)!=null?data.get(Constants.USER_LEVEL).toString():"";
			String serverId       = data.get(Constants.SERVER_ID)!=null?data.get(Constants.SERVER_ID).toString():"";
			String roleId       = data.get(Constants.ROLE_ID)!=null?data.get(Constants.ROLE_ID).toString():"";
			String sceneId     = data.get(Constants.SCENE_ID)!=null?data.get(Constants.SCENE_ID).toString():"";
			String isNewRole     = data.get(Constants.IS_NEW_ROLE)!=null?data.get(Constants.IS_NEW_ROLE).toString():"";
			LogUtil.e("sceneId = "+sceneId+"   isNewRole = " +isNewRole);
			/*ReYun 数据上报*/
			//进入游戏场景
			if(sceneId == null || sceneId.equals("")){
				
			}else if(sceneId == "1"||sceneId.equals("1")){
				sdkProxy.onEnterGame(data);
				LogUtil.e("进入服务器上报 sceneId = "+sceneId);
			}else if(sceneId == "2"||sceneId.equals("2")){
				LogUtil.e("创建角色上报 sceneId = "+sceneId);
				sdkProxy.onEnterGame(data);
			}else if(sceneId == "4"||sceneId.equals("4")){
				LogUtil.e("创建升级上报 sceneId = "+sceneId);
				sdkProxy.onEnterGame(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showUserCenter() {
		sdkProxy.showUserCenter();
	}
	
	
	public void onGameLevelChanged(int newlevel) {
		sdkProxy.onGameLevelChanged(newlevel);
	}

	public void onResume() {
		sdkProxy.onResume();
	}

	public void onPause() {
		sdkProxy.onPause();
	}

	public void onStop() {
		sdkProxy.onStop();
	}

	public void onDestroy() {
		sdkProxy.onDestroy();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		sdkProxy.onActivityResult(requestCode, resultCode, data);
	}

	public void onRestart() {
		sdkProxy.onRestart();
	}
	
	public void onConfigurationChanged(Configuration newConfig){
		sdkProxy.onConfigurationChanged(newConfig);
	}

	public void login(final Activity activity,final Map<String, Object> params) {

		LogUtil.e("登录接口login ++");
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				sdkProxy.login(activity , params);
			}
		});
	}

	public void pay(final Activity activity ,final KnPayInfo knPayInfo) {
		
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				sdkProxy.pay(activity , knPayInfo);				
			}
		});
	}

	public boolean hasThirdPartyExit() {
		return sdkProxy.hasThirdPartyExit();
	}

	public boolean hasSwitchUserView() {
		return sdkProxy.hasSwitchUserView();
	}

	public void onThirdPartyExit() {
		kndata.getGameActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				sdkProxy.onThirdPartyExit();
			}
		});
	}
	
	public void onQuit() {
		sdkProxy.onQuit();
	}


	public String getChannelVersion() {
		return sdkProxy.getChannelVersion();
	}

	public String getChannelName() {
		return sdkProxy.getChannelName();
	}

	public void onNewIntent(Intent intent) {
		sdkProxy.onNewIntent(intent);
	}

	public void onStart() {
		sdkProxy.onStart();
	}
	
	public FuncButton[] getSettingItems(){
		return sdkProxy.getSettingItems();
	}
	
	public void callSettingView() {
		sdkProxy.callSettingView();
	}

	public void onSaveInstanceState(Bundle outState) {
		sdkProxy.onSaveInstanceState(outState);
	}

	public String getAdChannel() {
		return sdkProxy.getAdChannel();
	}
	
	public void pushData( final Activity activity , final Map<String,Object> data ){
		activity.runOnUiThread( new Runnable() {
			@Override
			public void run() {
				sdkProxy.pushData(activity, data);
			}
		} );
	}

	public void pushActivation( final  Activity activity , final Map<String,Object> data ){
		activity.runOnUiThread( new Runnable() {
				@Override
				public void run() {
					sdkProxy.pushActivation(activity,data);
				}
			} );
		
	}
	
	public void activation(final  Activity activity){
		activity.runOnUiThread( new Runnable() {
			@Override
			public void run() {
				sdkProxy.activation(activity);
			}
		} );
	}
	
	public void finish(){
		sdkProxy.finish();
	}
	
	public void onWindowFocusChanged(boolean hasFocus) {
		sdkProxy.onWindowFocusChanged(hasFocus);
	}
	
	public void switchAccount(){
		sdkProxy.switchAccount();
	}
	
	public void logout(){
		sdkProxy.logout();
	}
	
	public void onBackPressed() {
		// TODO Auto-generated method stub
		sdkProxy.onBackPressed();
	}
	
}
