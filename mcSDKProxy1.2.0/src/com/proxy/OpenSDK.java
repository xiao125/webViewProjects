package com.proxy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
import com.proxy.call.Delegate;
import com.proxy.callback.SdkCallbackListener;
import com.proxy.listener.InvitationListener;
import com.proxy.sdk.SdkCenter;
import com.proxy.service.HttpService;
import com.proxy.tools.HttpRequestUtil;
import com.proxy.util.DeviceUtil;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;
import com.proxy.util.WxTools;

/**
 * 萌创sdk
 * @author Administrator
 *
 */
public class OpenSDK {

	private static OpenSDK instance = null;
	private SdkCenter sdkCenter = SdkCenter.getInstance();
	private static Activity   mActivity = null ;
	private boolean isInited = false; //初始化标识
	public static OpenSDK getInstance() {
		if (instance == null)
			instance = new OpenSDK();
		return instance;
	}


	public SdkCallbackListener callbackListener = new SdkCallbackListener<String>() {
		@Override
		public void callback(int code, String response) {

		}
	};

	/**
	 * 1. 提供外部接入： proxy初始化接口
	 * @param activity 游戏的主Activity
	 * @param gameInfo	游戏信息
	 */
	public void init(final Activity activity ,GameInfo gameInfo , final SdkCallbackListener<String> callback){
		mActivity = activity ;
		sdkLogInit(mActivity);
		Util.writeErrorLog("init");
		Util.writeInfoLog("SDK接入测试结果如下:");
		Data.getInstance().setGameActivity(activity); //设置全局Actvity
		Data.getInstance().setGameInfo(gameInfo);
		LogUtil.setLogEnable(true);
		Delegate.listener = callback;
		this.onCreate(activity);
		isInited = true;

	/*	callback.callback(ResultCode.INIT_SUCCESS,"初始化成功");*/
	}
	
	//	开始创建SDK检测工具日志
	private void sdkLogInit( Activity activity ){
		Util.sdklog( activity , Util.LOGFILEPATH ,  Util.LOGFILE , Util.RESULT_INFO , Util.RESULT_INFO1 , Util.RESULT_INFO2 ) ;
	}


	/**
	 * 2. 提供外部接入：登录接口
	 * @param activity	游戏的Activity
	 */
	public void login(Activity activity){

		LogUtil.log("中间件初始化标识isInited========:"+isInited);
		if(isInited){
			Map<String,Object> paras = new HashMap<>();
			paras.put("login",1);
			Util.writeErrorLog("login");
			sdkCenter.login(activity , paras);
			//Util.writeErrorLog("login");
			//Util.writeInfoLog("SDK接入测试结果如下:");

		}else {
			Delegate.listener.callback(ResultCode.INIT_FAIL,"初始化失败");
		}
	}

	/**
	 * 4.  提供外部接入 ：支付接口（注意：使用支付接口前必须保证调用了 【3.游戏数据上报接口】）
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
	 * 3. 提供外部接入 ：游戏进入服务器后上报数据调用接口
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

	//打开web
	public void openWeb(Activity act , String url ,String wxUrl){
		Intent intent = new Intent(act,StartWebView.class);
		intent.putExtra("url",  url );
		intent.putExtra("wxUrl",  wxUrl );
		act.startActivity( intent );
	}


	private void onCreate(final Activity activity) {
		// 这里必须是我们的游戏log初始化完毕之后才能够执行
		sdkCenter.onCreate(activity);
		activationGame(mActivity); //激活设备接口 （为了获取 SdkChannel类中设置的getChannelVersion参数，放在此处调用才能拿到设置的参数值）
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

		LogUtil.log("获取sdk渠道版本号："+sdkCenter.getChannelVersion());
		return sdkCenter.getChannelVersion();
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

		HttpService.doPushData(activity, new HttpRequestUtil.DataCallBack() {
			@Override
			public void requestSuccess(String result) throws Exception {
				LogUtil.log("=========doPushData Success========"+result);
			}
			@Override
			public void requestFailure(String request, IOException e) {
				LogUtil.log("=========doPushData Failure========"+request);

			}
			@Override
			public void requestNoConnect(String msg, String data) {
				LogUtil.log("=========doPushData Failure========"+msg);

			}
		});

		//sdkCenter.pushData(activity, data);
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
		data.put("game_id",mGameInfo.getGameId());
		data.put("ip",DeviceUtil.getMacAddress());
		data.put("app_key",mGameInfo.getAppKey());
		data.put("imei",DeviceUtil.getDeviceId());
		data.put("platform",mGameInfo.getPlatform());
		data.put("ad_channel",mGameInfo.getAdChannel());
		data.put("phone_type",DeviceUtil.getPhoneType());
		data.put("channel",mGameInfo.getChannel());
		LogUtil.log("开始调用激活接口"+data.toString());
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

}
