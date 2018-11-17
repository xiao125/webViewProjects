package com.game.sdk.service;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.game.sdk.GameSDK;
import com.game.sdk.bean.Data;
import com.game.sdk.task.SDK;
import com.game.sdk.bean.GameInfo;
import com.game.sdk.bean.UserInfo;
import com.game.sdk.listener.BaseListener;
import com.game.sdk.tools.HttpRequestUtil;
import com.game.sdk.util.BuildHelper;
import com.game.sdk.util.DeviceUtil;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.Md5Util;
import com.game.sdk.util.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpService {
	
	private static final String PROXY_VERSION = "1.0.1" ;

	//查询是否绑定账号
	public static void queryBindAccont( Context applicationContext,String user_Name,
										HttpRequestUtil.DataCallBack callBack){
		try {
			HashMap<String,String> update_params = getCommonParams(applicationContext);
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = PROXY_VERSION;
			JSONObject content = new JSONObject();
			content.put("user_name",user_Name);
			update_params.put("content", content.toString());
			update_params.put("proxyVersion", versionCode);
			Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
			HttpRequestUtil.okPostFormRequest( SDK.QUERY_ACCOUNT_BIND, update_params1,callBack);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//验证账号是否存在
	public static void GetUsername( Context applicationContext,String user_Name,
									HttpRequestUtil.DataCallBack callBack){

		try {
			HashMap<String , String> params = getCommonParams(applicationContext);
			JSONObject obj = new JSONObject();
			obj.put("user_name", user_Name);
			String content = obj.toString();
			params.put("content", content);
			params.put("proxyVersion", "1.0.0");
			params.put("sign", Md5Util.getMd5(content +GameSDK.instance.getGameInfo().getRegKey())); //这个接口验签必须是md5
			HttpRequestUtil.okPostFormRequest( SDK.GET_USER_NAME, params,callBack);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



	//游客绑定账号
/*
	public static void visitorBindAccount( Context applicationContext, Handler handler,
			String username, String password ){
		
		String gameId = GameSDK.getInstance().getGameInfo().getGameId() ;
		String channel = GameSDK.getInstance().getGameInfo().getChannel();
		String platform = GameSDK.getInstance().getGameInfo().getPlatform() ;
		String ad_channel = GameSDK.getInstance().getGameInfo().getAdChannel() ;
		String imei = DeviceUtil.getDeviceId();
		String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
//		String proxy_version = KnUtil.getJsonStringByName(appInfo, "versionCode") ;
		String proxy_version = PROXY_VERSION ;
		
		String app_id     = "1011";
		String app_secret = "3d759cba73b253080543f8311b6030bf";
		
		Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg0.compareTo(arg1);
			}
		} );
		
		JSONObject content = new JSONObject();
		try {
			content.put("user_name",username);
			content.put("passwd",password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		update_params.put("content", content.toString());
		
		update_params.put("game_id",gameId);
		update_params.put("channel",channel);
		update_params.put("platform",platform);
		update_params.put("ad_channel",ad_channel);
		update_params.put("msi",imei);
		update_params.put("proxyVersion",proxy_version);
		
		Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
		
		new VisitorAccountBindAsyncTask(applicationContext, handler, SDK.VISITOR_ACCOUNT_BIND)
				.execute(new Map[] { update_params1, null, null });
		
		
	}

	//游客登录
	public static void visitorReg( Context applicationContext, Handler handler ){
		
		String gameId = GameSDK.getInstance().getGameInfo().getGameId() ;
		String channel = GameSDK.getInstance().getGameInfo().getChannel();
		String platform = GameSDK.getInstance().getGameInfo().getPlatform() ;
		String ad_channel = GameSDK.getInstance().getGameInfo().getAdChannel() ;
		String imei = DeviceUtil.getDeviceId();
		String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
//		String proxy_version = KnUtil.getJsonStringByName(appInfo, "versionCode") ;
		String proxy_version = PROXY_VERSION ;
		
		String app_id     = "1011";
		String app_secret = "3d759cba73b253080543f8311b6030bf";
		
		Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg0.compareTo(arg1);
			}
		} );
		
		update_params.put("game_id",gameId);
		update_params.put("channel",channel);
		update_params.put("platform",platform);
		update_params.put("ad_channel",ad_channel);
		update_params.put("msi",imei);
		update_params.put("proxyVersion",proxy_version);
		
		Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
		
		new VisitorAsyncTask(applicationContext, handler, SDK.VISITOR_REG)
				.execute(new Map[] { update_params1, null, null });
		
	}
*/


	//发送验证码
	public static void SecCode( Context applicationContext,String mobile,
								   HttpRequestUtil.DataCallBack callBack){
		try {
			HashMap<String,String> update_params = getCommonParams(applicationContext);
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = PROXY_VERSION ;
			JSONObject content = new JSONObject();
			content.put("mobile",mobile);
			update_params.put("content", content.toString());
			update_params.put("proxyVersion", versionCode);
			Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
			HttpRequestUtil.okPostFormRequest( SDK.GET_RESURITY_CODE_URL, update_params1,callBack);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	//绑定手机请求
	public static void BindMobile( Context applicationContext,String mobile ,String user_Name,String security_code
								   ,HttpRequestUtil.DataCallBack callBack){

		try {
			HashMap<String,String> update_params = getCommonParams(applicationContext);
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = PROXY_VERSION ;
			JSONObject content = new JSONObject();
			content.put("mobile",mobile);
			content.put("user_name",user_Name);
			content.put("rand_code",security_code);
			update_params.put("content", content.toString());
			update_params.put("proxyVersion", versionCode);
			Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
			HttpRequestUtil.okPostFormRequest( SDK.BIND_MOBILE_URL, update_params1,callBack);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



/*
	//游客绑定手机
	public static void visitorbindMobile( Context applicationContext, Handler handler, String mobile , String security_code , String user_Name,String user_Password ){

		try {
			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = PROXY_VERSION ;
			String gameName = gameInfo.getGameId() ;
			String imei = DeviceUtil.getDeviceId();
			String channel = gameInfo.getChannel();
			String ad_channel = gameInfo.getAdChannel();
			String platform = gameInfo.getPlatform();
			Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

				@Override
				public int compare(String arg0, String arg1) {
					// TODO Auto-generated method stub
					return arg0.compareTo(arg1);
				}
			} );
			JSONObject content = new JSONObject();
			content.put("mobile",mobile);
			content.put("user_name",user_Name);
			content.put("passwd",user_Password);
			content.put("rand_code",security_code);
			update_params.put("content", content.toString());
			update_params.put("proxyVersion", versionCode);
			update_params.put("msi",imei);
			update_params.put("game_id",gameName);
			update_params.put("channel",channel);
			update_params.put("platform",platform);
			update_params.put("ad_channel",ad_channel);

			Map<String, String> update_params1 = Util.getSign( update_params , app_secret );

			new VisitorBindMobileAsyncTask(applicationContext, handler, SDK.VISITOR_BIND_MOBILE)
					.execute(new Map[] { update_params1 , null, null });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
*/


	//随机分配用户名接口
	public static void RandUserName( String time,
									 HttpRequestUtil.DataCallBack callBack){
		try {
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = PROXY_VERSION ;
			Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {
				@Override
				public int compare(String arg0, String arg1) {
					// TODO Auto-generated method stub
					return arg0.compareTo(arg1);
				}
			} );
			update_params.put("time",time);
			update_params.put("proxyVersion", versionCode);
			Map<String, String> update_params1 = Util.getSign( update_params , app_secret );

			HttpRequestUtil.okPostFormRequest( SDK.RAND_USER_NAME, update_params1,callBack);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//更加手机验证码修改密码
	public static void ModifyPaswordSubmit( Context applicationContext, String mobile ,
											String security_code ,String new_password,String newSdk,
											HttpRequestUtil.DataCallBack callBack) {
		try {
			HashMap<String, String> update_params = getCommonParams(applicationContext);
			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = PROXY_VERSION;
			JSONObject content = new JSONObject();
			content.put("mobile", mobile);
			content.put("pwd_new", new_password);
			content.put("rand_code", security_code);
			update_params.put("newSdk", newSdk);//区分sdk
			update_params.put("content", content.toString());
			update_params.put("proxyVersion", versionCode);
			Map<String, String> update_params1 = Util.getSign(update_params, app_secret);
			HttpRequestUtil.okPostFormRequest(SDK.UPDATE_PASSWORD_URL, update_params1, callBack);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//账号登录请求
	public static void doLogin(Context applicationContext,String username, String password ,
							   HttpRequestUtil.DataCallBack callBack) {
		try {
			HashMap<String , String> params = getCommonParams(applicationContext);
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			JSONObject content = new JSONObject();
			content.put("user_name", username);
			content.put("passwd", password);
			params.put("content", content.toString());
			String versionCode = PROXY_VERSION ;
			params.put("proxyVersion",versionCode);
			Map<String, String> update_params = Util.getSign(params ,app_secret );
			HttpRequestUtil.okPostFormRequest(SDK.LOGIN_URL, update_params,callBack);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//根据用户名与密码来注册账号
	public static void Register(Context applicationContext, String username, String password,
								  HttpRequestUtil.DataCallBack callBack) {
		try {

			HashMap<String,String> params = getCommonParams(applicationContext);
			JSONObject obj = new JSONObject();
			obj.put("user_name", username);
			obj.put("passwd", password);
			String content = obj.toString();
			params.put("content", content);
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = PROXY_VERSION ;
			params.put("proxyVersion",versionCode);
			Map<String, String> update_params = Util.getSign(params ,app_secret );
			HttpRequestUtil.okPostFormRequest( SDK.REG_URL, update_params,callBack);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//手机号注册账号
	public static void MobileRegister(Context applicationContext, String mobile,String code,
										String password,HttpRequestUtil.DataCallBack callBack) {
		try {
			HashMap<String,String> params = getCommonParams(applicationContext);
			JSONObject obj = new JSONObject();
			obj.put("mobile",mobile);
			obj.put("passwd",password);
			obj.put("rand_code",code);
			String content = obj.toString();
			params.put("content",content);
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = PROXY_VERSION ;
			params.put("proxyVersion",versionCode);
			Map<String, String> update_params = Util.getSign(params ,app_secret );
			HttpRequestUtil.okPostFormRequest( SDK.REG_MOBILE, update_params,callBack);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//注销接口 act_type: 2 注销 1：退出
	public static void doCancel(Activity activity,String act_type,HttpRequestUtil.DataCallBack callBack) {

		try {
			HashMap<String,String> params =  new HashMap<String,String>();
			UserInfo userInfo = GameSDK.getInstance().getUserInfo();
			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();
			String open_id ="";
			String server_id ="";
			String nick_name ="";
			String uid = "";

			if(userInfo!= null){

				open_id = userInfo.getOpenId();
				uid = userInfo.getUid();
				server_id = String.valueOf(userInfo.getServerId());
				nick_name = userInfo.getUsername();
			}

			String platform = gameInfo.getPlatform();
			String game_id =  gameInfo.getGameId();
			String channel = gameInfo.getChannel();
			String ad_channel = gameInfo.getAdChannel();
			String app_secret = gameInfo.getAppKey();
			String versionCode = PROXY_VERSION ;
			String msi = DeviceUtil.getDeviceId();
			params.put("game_id", game_id);//游戏名称
			params.put("open_id",open_id );
			params.put("imei",msi );
			params.put("channel",channel );
			params.put("ad_channel",ad_channel);
			params.put("proxyVersion",versionCode);
			params.put("act_type",act_type);
			params.put("uid",uid);
			params.put("server_id",server_id );
			params.put("nick_name",nick_name );
			params.put("system",Util.getSystemVersion()); //手机系统版本
			params.put("memory",Util.getTotalMemorySize()); //手机内存大小
			params.put("resolution",Util.ImageGalleryAdapter(activity.getApplicationContext())); //当前手机分辨率

			Collection<String> keyset= params.keySet();
			List<String> list = new ArrayList<String>(keyset);
			Collections.sort(list);
			String key = "";
			for(int i=0;i<list.size();i++){
				if(params.get(list.get(i))==null || params.get(list.get(i))=="" || params.get(list.get(i))=="0") {
					continue;
				}
				key += list.get(i)+"="+params.get(list.get(i))+"&";
			}
			key += "app_secret="+app_secret;
			params.put("sign",Md5Util.getMd5(key));
			//KnLog.log("排序字段:"+key);
			HttpRequestUtil.okPostFormRequest(SDK.CANCEL, params,callBack);
		} catch (Exception e) {
			KnLog.log("注销接口异常:"+e);
			e.printStackTrace();
		}
	}

	
	public static HashMap<String, String> getCommonParams(Context context){
		
		HashMap<String, String> params = new HashMap<String, String>();
		 GameSDK.getInstance().getUserInfo();
		UserInfo userInfo = GameSDK.getInstance().getUserInfo();
		GameInfo gameInfo = GameSDK.getInstance().getGameInfo(); //注意此处是获取
		
		String open_id="",game_id="",channel="",ad_channel="",msi="",platform="",appkey= "";
		String uid="",server_id="";
		
		if(userInfo!= null){
			open_id = userInfo.getOpenId();
			uid = userInfo.getUid();
			server_id = String.valueOf(userInfo.getServerId());
		}
		
		if(gameInfo!=null){
			platform = gameInfo.getPlatform();
			game_id =  gameInfo.getGameId();
			channel = gameInfo.getChannel();
			ad_channel = gameInfo.getAdChannel();
			appkey = gameInfo.getAppKey();
		}
		msi = DeviceUtil.getDeviceId();
		String Product = BuildHelper.getProduct(); //手机品牌
		String Mode = BuildHelper.getMode(); //手机型号
		String ip =DeviceUtil.getIPAddress(); //手机ip地址
		String time = Util.getTimes();

		params.put("game_id", game_id);//游戏名称
		params.put("channel", channel); //联运渠道
		params.put("ad_channel", ad_channel); //广告渠道
		params.put("uid", String.valueOf(uid));
		params.put("open_id", open_id);
		params.put("server_id", String.valueOf(server_id));
		params.put("mac", DeviceUtil.getMacAddress());
		params.put("platform", platform);
		//params.put("phoneType", DeviceUtil.getPhoneType()); //手机型号
		params.put("netType", DeviceUtil.getNetWorkType()); //手机网络状态
		params.put("app_key",appkey);
		String appInfo = Util.getAppInfo(  Data.getInstance().getGameActivity());
		params.put("packageName", Util.getJsonStringByName(appInfo, "packageName") ); //客户端包名
		params.put("versionName", Util.getJsonStringByName(appInfo, "versionName") ); //客户端版本
		params.put("versionCode", Util.getJsonStringByName(appInfo, "versionCode") );
		params.put("msi", msi ); //手机IMEI码
		params.put("phone_type",Product+"_"+Mode); //手机型号
		params.put("ip",ip); //手机型号
		params.put("time",time); //当前时间
		params.put("system",Util.getSystemVersion()); //手机系统版本
		params.put("memory",Util.getTotalMemorySize()); //手机内存大小
		params.put("resolution",Util.ImageGalleryAdapter(context.getApplicationContext())); //当前手机分辨率
		return params;
		
	}

}
