package com.game.sdk;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import com.game.sdk.activity.AutoLoginActivity;
import com.game.sdk.activity.AutomaticLoginActivity;
import com.game.sdk.activity.FastLoginActivity;
import com.game.sdk.activity.ForgotPasswordActivity;
import com.game.sdk.activity.SelecteLoginActivity;
import com.game.sdk.bean.Data;
import com.game.sdk.bean.GameInfo;
import com.game.sdk.bean.PayInfo;
import com.game.sdk.bean.UserInfo;
import com.game.sdk.call.Delegate;
import com.game.sdk.callback.SdkCallbackListener;
import com.game.sdk.config.ConstData;
import com.game.sdk.config.SDKStatusCode;
import com.game.sdk.floatmenu.SusViewMager;
import com.game.sdk.listener.PayListener;
import com.game.sdk.service.HttpService;
import com.game.sdk.task.SDK;
import com.game.sdk.tools.HttpRequestUtil;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.TodayTimeUtils;
import com.game.sdk.util.Util;
import com.game.sdkproxy.R;
import java.io.IOException;

public class GameSDK {

	public volatile static GameSDK instance = null;
	private int mOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE; //横竖屏
	private boolean isInited = false; //初始化标识
	private Activity activity = null;
	private SusViewMager mSusViewMager;
	public boolean isAuot=false;
	public Data data = Data.getInstance();
	public UserInfo userInfo = Data.getInstance().getUserInfo();
	public GameInfo gameInfo = Data.getInstance().getGameInfo();
	public UserInfo getUserInfo() {
		return userInfo;
	}

	public GameInfo getGameInfo() {
		return gameInfo;
	}

	public void setGameInfo(GameInfo gameInfo) {
		this.gameInfo = gameInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		KnLog.log("读取sdk数据===Uid="+userInfo.getUid()+"  server_id="+userInfo.getServerId()+"  open_id="+userInfo.getOpenId());
		this.userInfo = userInfo;
	}


	public static GameSDK getInstance() {
		if (instance == null){
			synchronized (GameSDK.class){
				if (instance == null){
					instance = new GameSDK();
				}
			}
		}
		return instance;
	}


	/** 1.
	 * 游戏初始化接口:
	 *这里没有商业接口,固定是初始化成功,实际开发需要根据后台去判断成功/失败
	 * 只有当初始化的时候才可以进行后续操作
	 */
	public void initSDK(Activity activity, GameInfo gameInfo,final SdkCallbackListener<String> callback) {
		this.activity = activity;
		Data.getInstance().setGameActivity(activity); //设置全局Actvity
		setGameInfo(gameInfo);
		KnLog.log("=============appKey========="+gameInfo.getAppKey()+" GameId="+gameInfo.getGameId());
		SDK.changeConfig(gameInfo.getAdChannelTxt());
		MetaData();
		callback.callback(SDKStatusCode.SUCCESS, "初始化成功");
		this.isInited = true;
		KnLog.setLogEnable(true);
	}

	/**
	 * 2. 登录: 初始化成功才可以登录
	 * @param activity
	 * @param callback
	 * @param logoutListener
	 */
	public void login(Activity activity, final SdkCallbackListener<String> callback,SusViewMager.OnLogoutListener logoutListener) {

		if(isInited){
			String[] usernames = DBHelper.getInstance().findAllUserName();
			Intent intent = null;
			//	数据库中获取用户数据量
			if (usernames.length == 0) {
				intent = new Intent(activity.getApplicationContext(), FastLoginActivity.class);
				intent.putExtra("selectLogin", "selectLogin");
				activity.startActivity(intent);
				isAuot=true;
			}else {
				Object isLogout = TodayTimeUtils.getLogout(activity);
				if (isLogout.equals("true")) {
					intent = new Intent(activity, AutoLoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("logout", "logout");
					activity.startActivity(intent);
					TodayTimeUtils.setLogout(activity, "false");
				} else {
					KnLog.log("=========自动登录1-======="+isAuot);
					if (!isAuot) {
						intent = new Intent(activity, AutomaticLoginActivity.class);
						activity.startActivity(intent);
						isAuot = false;
					}
				}
			}
			KnLog.log("=========自动登录完后2-======="+isAuot);
			//开启悬浮窗
			mSusViewMager = SusViewMager.getInstance();
			if (mSusViewMager !=null){
				mSusViewMager.setOnLogoutListener(logoutListener);
			}
			mSusViewMager.showWithCallback(activity);
			Delegate.listener = callback;
		}else {
			callback.callback(SDKStatusCode.FAILURE, ConstData.INIT_FAILURE);
			return;
		}


	}

	/**
	 * 3.游戏内切换按钮，可以接入此接口
	 * @param logoutListener
	 */
	public void mcLogout(SusViewMager.OnLogoutListener logoutListener){
		mSusViewMager = SusViewMager.getInstance();
		if (mSusViewMager !=null){
			mSusViewMager.setOnLogoutListener(logoutListener);
		}
		logoutListener.onExitFinish();
		TodayTimeUtils.setLogout(activity,"true");
		//注销
		HttpService.doCancel(activity, "2", new HttpRequestUtil.DataCallBack() {
			@Override
			public void requestSuccess(String result) throws Exception {

			}

			@Override
			public void requestFailure(String request, IOException e) {
			}
			@Override
			public void requestNoConnect(String msg, String data) {
			}
		});
	}


	/**
	 * 4. sdk退出接口 :  游戏退出时，调用此接口
	 */
	public void  mcQuit(){
		//注销
		KnLog.log("sdk退出");
		HttpService.doCancel(activity, "1", new HttpRequestUtil.DataCallBack() {
			@Override
			public void requestSuccess(String result) throws Exception {
			}
			@Override
			public void requestFailure(String request, IOException e) {
			}
			@Override
			public void requestNoConnect(String msg, String data) {
			}
		});
	}





	/** 5.支付 （已废弃）
	 * @param activity
	 * @param appid
	 *            爱贝appid
	 * @param appkey
	 *            爱贝appkey
	 */
	public void initIappaySDK(Activity activity,String appid, String appkey , String publicKey) {
		//PAY_API.getInstance().init(activity, getmOrientation(), appid , appkey  , publicKey);
	}


	//关闭
	public void hideFloat(){

		if (mSusViewMager!=null){
			mSusViewMager.hideFloat();
		}
	}

	//移除所有悬浮窗
	public void destoryFloat(){
		if (mSusViewMager!=null){
			mSusViewMager.destroyFloat();
		}
	}


	/**
	 * 跳转到登陆页面
	 * @param
	 * @param
	 */
	public void login( Activity activity ){
		KnLog.log(" login ccc");
		if (!isInited()) {
			Util.ShowTips(activity, activity.getResources().getString(R.string.mc_tips_16) );
			return;
		}
		String[] usernames = DBHelper.getInstance().findAllUserName();
		Intent intent = null;
		//	数据库中获取用户数据量
		if (usernames.length == 0 ) {
			intent = new Intent(activity.getApplicationContext(), SelecteLoginActivity.class);
			intent.putExtra("selectLogin", "selectLogin");
		} else {
			String  lastUserName = usernames[0];
			KnLog.log("lastUserName:"+lastUserName);
			intent = new Intent(activity.getApplicationContext(), AutoLoginActivity.class);
			KnLog.log("lastUserName="+lastUserName);
			intent.putExtra("userName",lastUserName);
		}
		if( null == intent ){
			return ;
		}
		activity.startActivity(intent);
		activity.finish();

	}


	// 跳转到快速注册页面
	public void KsRegister(Activity activity, boolean hasResult) {
		if (hasResult)
		{
			Intent intent = new Intent(activity.getApplicationContext(),FastLoginActivity.class);
			activity.startActivityForResult(intent, SDK.REQUESTCODE_REG);
			activity.finish();
		}
	}

	// 跳转到修改密码
	public void Update_password(Activity activity, boolean hasResult) {
		if (hasResult)
		{
			Intent intent = new Intent(activity.getApplicationContext(),ForgotPasswordActivity.class);
			activity.startActivityForResult(intent, SDK.UPDATE_PASSWORD);
			activity.finish();
		}
	}

	/**
	 * 
	 * @param activity
	 * @param
	 * @param
	 */
	public void pay(final Activity activity, final PayInfo payInfo,
			final PayListener payListener) {
		/*if (userInfo == null || !userInfo.isLogin()) {
			Util.ShowTips(activity,  activity.getResources().getString(R.string.mc_tips_17) );
			return;
		}*/
		//setmPayListener(payListener);
		//PAY_API.getInstance().pay(activity, payInfo, payListener);
	}

	//打开web， 参数： web支付总界面url  与 单独 微信支付url
	public void openWeb(Activity act , String url ,String wxUrl ){
		/*Intent intent = new Intent(act,StartWebView.class);
		intent.putExtra("url",  url );
		intent.putExtra("wxUrl",  wxUrl );
		act.startActivity( intent );*/
	}


	//	读取activity中manifest.xml中某个键值对是否支持横竖屏切换, 即读取<meta-data>配置元素中的数据
	private void MetaData(){
		//	读取activity中manifest.xml中某个键值对是否支持横竖屏切换
		ApplicationInfo ai;
		String adChannel = null ;
		try {
			ai = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			if(null==bundle){
				KnLog.e("bundle is null");
			}else{
				if(bundle.containsKey("ScreenSendor")){
					//mScreenSensor = true ;
				}else{
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}


	public boolean isInited() {
		return isInited;
	}


}
