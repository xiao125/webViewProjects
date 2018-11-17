package com.proxy;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import com.proxy.activity.Invitation;
import com.proxy.activity.StartWebView;
import com.proxy.bean.FuncButton;
import com.proxy.bean.GameInfo;
import com.proxy.bean.KnPayInfo;
import com.proxy.listener.BaseListener;
import com.proxy.listener.ExitListener;
import com.proxy.listener.InitListener;
import com.proxy.listener.InvitationListener;
import com.proxy.listener.LoginListener;
import com.proxy.listener.LogoutListener;
import com.proxy.listener.PayListener;
import com.proxy.listener.PushActivationListener;
import com.proxy.listener.PushDataListener;
import com.proxy.listener.RoleReportListener;
import com.proxy.listener.WeixinListener;
import com.proxy.sdk.SdkCenter;
import com.proxy.service.HttpService;
import com.proxy.task.CommonAsyncTask;
import com.proxy.util.DeviceUtil;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;
import com.proxy.util.WxTools;

/**
 * 酷牛sdk
 * @author Administrator
 *
 */
public class OpenSDK {

	private static OpenSDK instance = null;
	private SdkCenter sdkCenter = SdkCenter.getInstance();
	private Listener knListener = Listener.getInstance();
	private static Activity   mActivity = null ;
	public static OpenSDK getInstance() {
		if (instance == null)
			instance = new OpenSDK();
		return instance;
	}

	/**
	 * proxy初始化
	 * @param activity 游戏的主Activity
	 * @param gameInfo	游戏信息
	 */
	public void init(final Activity activity ,GameInfo gameInfo , InitListener initlistenr){
		
		mActivity = activity ;
		sdkLogInit(mActivity);
		Util.writeErrorLog("init");
		Util.writeInfoLog("SDK接入测试结果如下:");
		if(null==initlistenr){
			Util.writeInfoLog("\tSDK初始化方法init中初始化监听不能为空");
		}
		LogUtil.setLogEnable(true);
		knListener.setInitListener(initlistenr);
		Data.getInstance().setGameActivity(activity);
		Data.getInstance().setGameInfo(gameInfo);
		this.onCreate(activity);
	}
	
	//	开始创建SDK检测工具日志
	private void sdkLogInit( Activity activity ){
		Util.sdklog( activity , Util.LOGFILEPATH ,  Util.LOGFILE , Util.RESULT_INFO , Util.RESULT_INFO1 , Util.RESULT_INFO2 ) ;
	}
	
	/**
	 * 游戏进入游戏首页的时候调用
	 * @param data
	 */
	public void onEnterGame(Map<String, Object> data) {

		sdkCenter.onEnterGame(data);
		Util.writeErrorLog("onEnterGame");
		Util.writeErrorLog("onEnterGame");
		Util.writeInfoLog("SDK接入测试结果如下:");
		Object userId = data.get(Constants.USER_ID);
		if(null==userId||""==userId){
			Util.writeInfoLog("\t在调用onEnterGame方法中键名uid对应的值不能为空");
		}
		Object serverId  = data.get(Constants.SERVER_ID);
		if(null == serverId  || "" == serverId ){
			Util.writeInfoLog("\t在调用onEnterGame方法中键名sid对应的值不能为空");
		}
		Object userLv  = data.get(Constants.USER_LEVEL);
		if(null == userLv  || "" == userLv ){
			Util.writeInfoLog("\t在调用onEnterGame方法中键名userLv对应的值不能为空");
		}
		Object serverName   = data.get(Constants.SERVER_NAME);
		if(null == serverName   || "" == serverName  ){
			Util.writeInfoLog("\t在调用onEnterGame方法中键名serverName对应的值不能为空");
		}
		Object roleName   = data.get(Constants.ROLE_NAME);
		if(null == roleName   || "" == roleName  ){
			Util.writeInfoLog("\t在调用onEnterGame方法中键名roleName对应的值不能为空");
		}
		Object vipLevel   = data.get(Constants.VIP_LEVEL);
		if(null == vipLevel   || "" == vipLevel  ){
			Util.writeInfoLog("\t在调用onEnterGame方法中键名vipLevel对应的值不能为空");
		}
		Object role_id   = data.get(Constants.ROLE_ID);
		if(null == role_id   || "" == role_id  ){
			Util.writeInfoLog("\t在调用onEnterGame方法中键名role_id对应的值不能为空");
		}
		Object diamondLeft   = data.get(Constants.BALANCE);
		if(null == diamondLeft   || "" == diamondLeft  ){
			Util.writeInfoLog("\t在调用onEnterGame方法中键名diamondLeft对应的值不能为空");
		}
	}

	/**
	 * 设置登陆回调
	 * @param listener
	 */
	public void setLogoinListener(LoginListener listener) {
		Util.writeErrorLog("setLogoinListener");
		if(null==listener){
			Util.writeInfoLog("\tSDK登录方法中监听不能为空");
		}
		knListener.setLoginListener(listener);
	}
	
	/**
	 * 设置注销回调
	 * @param listener
	 */
	public void setLogoutListener(LogoutListener listener) {
		Util.writeErrorLog("setLogoutListener");
		knListener.setLogoutListener(listener);
	}
	
	/**
	 * 设置初始化回调
	 * @param listener
	 */
	public void setInitListener(InitListener listener) {
		Util.writeErrorLog("setInitListener");
		knListener.setInitListener(listener);
	}
	
	/**
	 * 设置支付回调
	 * @param listener
	 */
	public void setPayListener(PayListener listener) {
		Util.writeErrorLog("setPayListener");
		if(null == listener){
			Util.writeInfoLog("\tSDK支付方法中监听不能为空");
		}
		knListener.setPayListener(listener);
	}
	
	/**
	 * 设置 退出回调
	 * @param listener
	 */
	public void setExitListener(ExitListener listener) {
		Util.writeErrorLog("setExitListener");
		knListener.setExitListener(listener);
	}

	/**
	 * 上报数据回调
	 * @param listener
	 */
	public void setRoleReportListener(RoleReportListener listener){
		Util.writeErrorLog("setRoleReportListener");
		knListener.setRoleReportListener(listener);
	}

	private void onCreate(final Activity activity) {
		// 这里必须是我们的游戏log初始化完毕之后才能够执行
		sdkCenter.onCreate(activity);
	}

	public void onResume() {
		Util.writeErrorLog("onResume");
		sdkCenter.onResume();
	}

	public void onPause() {
		Util.writeErrorLog("onPause");
		sdkCenter.onPause();
	}
	
	public void onStop() {
		Util.writeErrorLog("onStop");
		sdkCenter.onStop();
	}
	
	public void onRestart(){
		Util.writeErrorLog("onRestart");
		sdkCenter.onRestart();
	}

	public void onDestroy() {
		Util.writeErrorLog("onDestroy");
		sdkCenter.onDestroy();
	}
	
	public void onStart(){
		Util.writeErrorLog("onStart");
		sdkCenter.onStart();
	}
	
	public void onNewIntent(Intent intent) {
		sdkCenter.onNewIntent(intent);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		sdkCenter.onActivityResult(requestCode, resultCode, data);
	}
	
	public void onConfigurationChanged(Configuration newConfig){
		sdkCenter.onConfigurationChanged(newConfig);
	}
	
	public void onSaveInstanceState(Bundle outState) {
		sdkCenter.onSaveInstanceState(outState);
	}
	
	/**
	 * 返回 proxy 版本号
	 * @return  proxy 版本号
	 */
	public String getProxyVersion(){
		return "1.0.0";
	}
	
	/**
	 * @return sdk渠道的版本号
	 */
	public String getChannelVersion(){
		return sdkCenter.getChannelVersion();
	}
	
	/**
	 * 登录
	 * @param activity		游戏的Activity
	 */
	public void login(Activity activity){
		Util.writeErrorLog("login");
		LogUtil.e("login()+++");
		sdkCenter.login(activity , null);
		Util.writeErrorLog("login");
		Util.writeInfoLog("SDK接入测试结果如下:");
	}
	
	/**
	 * 支付
	 * @param activity		游戏的Activity
	 * @param knPayInfo	 	支付信息
	 */
	public void pay( Activity activity , KnPayInfo knPayInfo) {
		Util.writeErrorLog("pay");
		Util.writeInfoLog("SDK接入测试结果如下:");
		sdkCenter.pay(activity , knPayInfo);
	}
	
	/**
	 * @return false:第三方sdk没有自己的退出</br>
	 * 		   true :第三方sdk拥有自己的退出
	 */
	public boolean hasThirdPartyExit() {
		LogUtil.e("hasThirdPartyExit()+++"+sdkCenter.hasThirdPartyExit());
		return sdkCenter.hasThirdPartyExit();
	}
		
	/**
	 * 点击返回按钮时，调用的第三方退出接口
	 */
	public void onThirdPartyExit(){
		sdkCenter.onThirdPartyExit();
		LogUtil.e("onThirdPartyExit()+++");
		Util.writeErrorLog("onThirdPartyExit");
		Util.writeInfoLog("SDK接入测试结果如下:");
	}
	
	/**
	 * 
	 * @param enable true 可以打印出调试信息，false则不
	 */
	public void isDebugModel(boolean enable){
		LogUtil.mLogEnable = enable;
	}
	
	/**
	 * @return 返回渠道名称
	 */
	public String getChannelName(){
		return sdkCenter.getChannelName();
	}
	
	public String getAdChannel(){
		return sdkCenter.getAdChannel();
	}
	
	/**
	 * 设置buglyAppid</br>
	 * 如果不需要接入buglysdk则不需要填写
	 * @param appId 申请到的buglysdk的AppID
	 */
	public void setBuglySdkAppId(String appId){
		
	}
	/**
	 * 
	 * @return 返回null则表示没有sdk设置页面
	 */
	public FuncButton[] getSettingItems(){
		return sdkCenter.getSettingItems();
	}
	
	/**
	 * 调用sdk设置页面
	 */
	public void callSettingView() {
		sdkCenter.callSettingView();
	}

	/*
	
	此处添加接口:pushData(
		ip,
		phone_type,
		sign,
		gameId,
		appKey,
		imei,
		platform,
		ad_channel,
	)
	
	sign = md5(gameId+appKey+imei)
	
	完成数据上传成功或者完毕之后游戏方可进行登录(或者说完成初始化成功之后方可进行游戏登录)
	 
	 */
	public void pushData( final  Activity activity ,  final Map<String,Object> data ){
		HttpService.doPushData(activity,data, new BaseListener() {
			@Override
			public void onSuccess(Object result) {
				LogUtil.e("result:"+result.toString());
			}
			
			@Override
			public void onFail(Object result) {
				LogUtil.e("result:"+result.toString());
			}
		} );
		sdkCenter.pushData(activity, data);
	}
	
	public void setPushDataListener(PushDataListener listener){
		knListener.setPushDataListener(listener);
	}
	
	public void setActivationListener( PushActivationListener  listener){
		knListener.setPushActivationListenr(listener);
	}
	
	//	激活码礼品
	public void pushActivation( final  Activity activity , final Map<String,Object> data ){
		
		sdkCenter.pushActivation(activity,data);
		Object userId = data.get(Constants.USER_ID);
		if(null==userId||""==userId){
			Util.writeInfoLog("\t在调用pushActivation方法中键名uid对应的值不能为空");
		}
		Object serverId  = data.get(Constants.SERVER_ID);
		if(null == serverId  || "" == serverId ){
			Util.writeInfoLog("\t在调用pushActivation方法中键名sid对应的值不能为空");
		}
		Object userLv  = data.get(Constants.SERVER_ID);
		if(null == userLv  || "" == userLv ){
			Util.writeInfoLog("\t在调用pushActivation方法中键名userLv对应的值不能为空");
		}
		Object serverName   = data.get(Constants.SERVER_ID);
		if(null == serverName   || "" == serverName  ){
			Util.writeInfoLog("\t在调用pushActivation方法中键名serverName对应的值不能为空");
		}
		Object roleName   = data.get(Constants.SERVER_ID);
		if(null == roleName   || "" == roleName  ){
			Util.writeInfoLog("\t在调用pushActivation方法中键名roleName对应的值不能为空");
		}
		Object vipLevel   = data.get(Constants.SERVER_ID);
		if(null == vipLevel   || "" == vipLevel  ){
			Util.writeInfoLog("\t在调用pushActivation方法中键名vipLevel对应的值不能为空");
		}
		Object role_id   = data.get(Constants.SERVER_ID);
		if(null == role_id   || "" == role_id  ){
			Util.writeInfoLog("\t在调用pushActivation方法中键名role_id对应的值不能为空");
		}
		Object diamondLeft   = data.get(Constants.SERVER_ID);
		if(null == diamondLeft   || "" == diamondLeft  ){
			Util.writeInfoLog("\t在调用pushActivation方法中键名diamondLeft对应的值不能为空");
		}
		
	}
	
	public void activation( final  Activity activity ){
		sdkCenter.activation(activity);
	}

		
	public void finish(){
		sdkCenter.finish();
	}
	
	public void onWindowFocusChanged(boolean hasFocus) {
		sdkCenter.onWindowFocusChanged(hasFocus);
	}
	
	public void switchAccount(){
		LogUtil.e("switchAccount+++++");
		sdkCenter.switchAccount();
	}
	
	public void logout(){
		LogUtil.e("游戏登出--");
		sdkCenter.logout();
	}
	
	/**
	 * 游戏退出上报接口
	 */
	public void Quit(){
		sdkCenter.onQuit();
	}
	
	public void activationGame( Activity activity){
		
		//		游戏激活
		GameInfo mGameInfo = Data.getInstance().getGameInfo();
		Map<String, Object>  data = new HashMap<String, Object>();
		LogUtil.e("初始化"+mGameInfo.getGameId()) ;
		data.put("game_id",mGameInfo.getGameId());
		data.put("ip",DeviceUtil.getMacAddress());
		data.put("app_key",mGameInfo.getAppKey());
		data.put("imei",DeviceUtil.getDeviceId());
		data.put("platform",mGameInfo.getPlatform());
		LogUtil.e("初始化"+mGameInfo.getAdChannel()) ;
		data.put("ad_channel",mGameInfo.getAdChannel());
		data.put("phone_type",DeviceUtil.getPhoneType());
		LogUtil.e("初始化"+mGameInfo.getChannel()) ;
		data.put("channel",mGameInfo.getChannel());
		pushData(activity,data);
		
	}
	
	//跳转激活码页面
	public void invitation( final Activity activity , InvitationListener listener ){
		Listener.getInstance().setInvivationListener(listener);
		Intent intent = new Intent(activity.getApplicationContext(), Invitation.class);
		activity.startActivity(intent);	
	}
	//	是否打开Debug模式
	public void DebugOpen( boolean flag ){
		LogUtil.setLogEnable(flag);
	}

	public void isSupportNew( final boolean mode ){
		Util.writeErrorLog("isSupportNew");
		Data.getInstance().setNewMode(mode);
	}
	
	public void doDebug( final boolean mode ){
		Data.getInstance().setDeBugMode(mode);
	}

	//打开web
	public void openWeb(Activity act , String url ,String wxUrl){
		Intent intent = new Intent(act,StartWebView.class);
		intent.putExtra("url",  url );
		intent.putExtra("wxUrl",  wxUrl );
		act.startActivity( intent );
	}
	
	public void onBackPressed() {
		// TODO Auto-generated method stub
		sdkCenter.onBackPressed();
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event){
		View m_baseView = WxTools.getIntance().getM_View();
		if(m_baseView!=null){
			((ViewGroup)m_baseView.getParent()).removeView(m_baseView);
			WxTools.getIntance().setM_View(null);
			return true;
		}else{
			return false ;
		}
	}

	
	//显示邀请码
	public void inviteShows(final Activity activity , final InvitationListener listener ){
		
		Listener.getInstance().setInvivationListener(listener);
		String app_secret = "3d759cba73b253080543f8311b6030bf";
		final String PROXY_VERSION = "1.0.1" ;
		String imei = DeviceUtil.getDeviceId();
		String gameId= Data.getInstance().getGameInfo().getGameId();
		//获取时间
		SimpleDateFormat formatter   =   new   SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
		Date curDate =  new Date(System.currentTimeMillis());
		String   time  =   formatter.format(curDate);
		Map<String, String> params = new TreeMap<String, String>( new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg0.compareTo(arg1);
			}
		} );
		params.put("msi", imei);
		params.put("time", String.valueOf(time));
		params.put("proxyVersion",PROXY_VERSION);
		params.put("game_id",gameId);
		Map<String, String> update_params1 = Util.getSign( params , app_secret );	
		new CommonAsyncTask(mActivity,Constants.URL.ISACTIVATIONS,new BaseListener() {
			@Override
			public void onSuccess(Object result) {
				LogUtil.log("是否已经使用了激活码接:"+result.toString());
				Listener.getInstance().getInvivationListener().onSuccess("已经激活了");
			}
			
			@Override
			public void onFail(Object result) {
				// TODO Auto-generated method stub
				LogUtil.log("激活失败:"+result.toString());
				Listener.getInstance().getInvivationListener().onFail("激活失败");
				Intent intent = new Intent(activity, Invitation.class);
				activity.startActivity(intent);
				
			}
		}).execute(new Map[] { update_params1 , null, null });;		

	}
	
	
	
	
	public void inviteShow( final String sid , final Activity activity , final InvitationListener listener    ){
		Listener.getInstance().setInvivationListener(listener);
		String app_id     = "1009";
		String app_secret = "5c7f4ea46f0d1c6c5c30693f24016374";
		Map<String, String> params = new TreeMap<String, String>( new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg0.compareTo(arg1);
			}
		} );
		
		params.put("m", "knsdk");
		params.put("game", Data.getInstance().getGameInfo().getGameId());
		params.put("a", "checkInviteStatus");
		params.put("open_id", Data.getInstance().getUser().getOpenId());
		String ad_channel = Data.getInstance().getGameInfo().getAdChannel();
		params.put("server_id",sid);
		params.put("ad_channel",ad_channel);
		params.put("app_id",app_id);
		Map<String, String> update_params1 = Util.getSign( params , app_secret );
		LogUtil.log(update_params1.toString());
		new CommonAsyncTask(mActivity,Constants.URL.ACTIVATION, new BaseListener() {
			@Override
			public void onSuccess(Object result) {
				// TODO Auto-generated method stub
				LogUtil.log("开始显示邀请码界面UI:"+result.toString());
				try {
					JSONObject  json = new JSONObject(result.toString());
					String      version = json.getString("version");
					JSONObject  verJson = new JSONObject(version);
					String      code = verJson.getString("code");
					if(code.equals("0")){
						LogUtil.log("已经激活了");
						Listener.getInstance().getInvivationListener().onSuccess("已经激活了");
					}else if(code.equals("1")){
						Intent intent = new Intent(activity.getApplicationContext(), Invitation.class);
						intent.putExtra("server_id", sid);
						activity.startActivity(intent);
					}else if(code.equals("2")){
						String      msg  = verJson.getString("msg");
						Util.ShowTips(activity, msg);
						Listener.getInstance().getInvivationListener().onFail("非法请求");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}

			@Override
			public void onFail(Object result) {
				LogUtil.log("不用显示邀请码界面UI");
			}
		}).execute(new Map[] { update_params1 , null, null });;	
	}
}
