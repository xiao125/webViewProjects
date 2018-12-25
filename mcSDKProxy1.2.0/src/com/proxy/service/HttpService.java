package com.proxy.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import com.proxy.*;
import com.proxy.bean.GameInfo;
import com.proxy.bean.GameUser;
import com.proxy.bean.KnPayInfo;
import com.proxy.bean.User;
import com.proxy.call.Delegate;
import com.proxy.listener.BaseListener;
import com.proxy.task.CommonAsyncTask;
import com.proxy.tools.HttpRequestUtil;
import com.proxy.tools.HttpUrlConstants;
import com.proxy.util.DeviceUtil;
import com.proxy.util.LoadingDialog;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;
import com.proxy.util.Md5Util;

public class HttpService {

	//登录
	public static void doLogin(Activity activity,
							   String content , HttpRequestUtil.DataCallBack callBack) {
		try {
			GameInfo gameInfo = Data.getInstance().getGameInfo();
			HashMap<String , String> params = getCommonParams(activity.getApplicationContext());
			LogUtil.e("params:"+params.toString());
			String game_id = gameInfo.getGameId();
			String platform = gameInfo.getPlatform();
			String channel = gameInfo.getChannel();
			params.put("content", content);
			params.put(
					"sign",
					Md5Util.getMd5(game_id + channel
							+ platform + content.toString()
							+ gameInfo.getAppKey()));
			LogUtil.e("AppKey"+gameInfo.getAppKey());
			LogUtil.e("game_id"+Data.getInstance().getGameInfo().getGameId());
			LogUtil.e("game_id = "+game_id+"  channel="+channel+"  AppKey="+gameInfo.getAppKey());
			HttpRequestUtil.okPostFormRequest(Constants.LOGIN, params,callBack);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//请求支付订单
	public static void applyOrder( Activity activity ,KnPayInfo knPayInfo,HttpRequestUtil.DataCallBack callBack) {
		try {

			User userInfo = Data.getInstance().getUser();
			GameInfo gameInfo = Data.getInstance().getGameInfo();
			GameUser gamuser = Data.getInstance().getGameUser();
			HashMap<String,String> params = getCommonParams(activity.getApplicationContext());
			String open_id = userInfo != null ? userInfo.getOpenId():"";
			String uid = gamuser!=null ? gamuser.getUid():"";
			int server_id = gamuser!=null ?  gamuser.getServerId() : 0;
			String game_id = gameInfo != null ? gameInfo.getGameId() : "";
			String platform = gameInfo != null ? gameInfo.getPlatform():"";
			String channel = gameInfo != null ? gameInfo.getChannel():"";
			//params.put("extra_info", knPayInfo.getExtraInfo());

			if(Util.getGameName(activity).equals("fmsg")){
				params.put("extra_info", knPayInfo.getExtraInfo());
			}else {
				//params.put("extra_info", knPayInfo.getOrderNo());
				if(knPayInfo.getExtraInfo()!=null ||("").equals(knPayInfo.getExtraInfo())){
					LogUtil.e("==支付透传1==");
					params.put("extra_info", knPayInfo.getExtraInfo());
				}else {
					LogUtil.e("==支付透传2==");
					params.put("extra_info", knPayInfo.getOrderNo());
				}

				if (Util.getChannle(activity).equals("nubia")) {
					params.put("productName", knPayInfo.getProductName());
				}if (Util.getChannle(activity).equals("dalv")) {
					params.put("productName",(knPayInfo.getPrice()/10+"元宝"));
					params.put("amount",String.valueOf((knPayInfo.getPrice()/100)));
					params.put("extend","充值元宝");
					params.put("appid","et51ba58d87527a539");
					params.put("gameArea",Data.getInstance().getGameUser().getServerName());
					params.put("gameAreaId",String.valueOf(Data.getInstance().getGameUser().getServerId()));
					params.put("roleId",Data.getInstance().getGameUser().getUid());
					params.put("userRole",Data.getInstance().getGameUser().getUsername());
					params.put("gameLevel",String.valueOf(Data.getInstance().getGameUser().getUserLevel()));

				}if (Util.getChannle(activity).equals("uc")) {
					double price=knPayInfo.getPrice();
					params.put("amount",String.valueOf(Integer.parseInt(new java.text.DecimalFormat("0").format(price))/100));
					params.put("accountId",Data.getInstance().getUser().getExtraInfo1());
					params.put("callbackInfo","自定义信息");
					LogUtil.log("支付请求参数accountId====="+Data.getInstance().getUser().getExtraInfo1()+"amount="+String.valueOf(Integer.parseInt(new java.text.DecimalFormat("0").format(price))/100));
				}
				if (Util.getChannle(activity).equals("vivo")) {
					double price=knPayInfo.getPrice();
					params.put("vivo_title",knPayInfo.getProductName());
					params.put("vivo_desc",knPayInfo.getCoinName());
					params.put("vivo_price",String.valueOf(Integer.parseInt(new java.text.DecimalFormat("0").format(price))));
				}

				if (Util.getChannle(activity).equals("mz")) {

					double price=knPayInfo.getPrice();
					params.put("mztotal_price",String.valueOf(Integer.parseInt(new java.text.DecimalFormat("0").format(price))/100)); //金额总数
					params.put("mzproduct_per_price",String.valueOf(Integer.parseInt(new java.text.DecimalFormat("0").format(price))/100));//游戏道具单价，默认值：总金额
					params.put("mzapp_id",knPayInfo.getExtraInfo());//appid
					LogUtil.log("appid= "+knPayInfo.getExtraInfo());
					params.put("mzuid",gamuser.getExtraInfo()); //sdk登录成功后的 uid
					params.put("mzproduct_id",knPayInfo.getProductId());//CP 游戏道具 ID,
					params.put("mzproduct_subject",knPayInfo.getProductName());//订单标题,格式为：”购买 N 枚金币”
					params.put("mzproduct_unit","元");//游戏道具的单位，默认值：””
					params.put("mzproduct_body","元宝");//游戏道具说明，默认值：””
					params.put("mzbuy_amount",String.valueOf(1));//道具购买的数量，默认值：”1”
					params.put("mzcreate_time",userInfo.getExtenInfo());//创建时间戳
					params.put("mzpay_type",String.valueOf(0));//支付方式，默认值：”0”（即定额支付）
					params.put("mzuser_info","");//CP 自定义信息，默认值：””
					params.put("mzsign_type","md5");//签名算法，默认值：”md5”(不能为空)


					LogUtil.log("支付请求参数mzuid====="+gamuser.getExtraInfo()+" 创建时间戳="+userInfo.getExtenInfo()+"订单名称="+knPayInfo.getProductName()+" 总额="
							+String.valueOf(Integer.parseInt(new java.text.DecimalFormat("0").format(price))/100)+" product_id="+knPayInfo.getProductId()+
							" product_subject="+knPayInfo.getProductName()+" pay_type"+String.valueOf(0));

				}if (Util.getChannle(activity).equals("duoku")) {
					params.put("dkuid",gamuser.getExtraInfo());//支付传递的uid
					params.put("dkextraInfo",userInfo.getExtenInfo());//透传
					LogUtil.log("下单发送游戏服务器dkuid="+gamuser.getExtraInfo()+"透传="+userInfo.getExtenInfo());

				}if (Util.getChannle(activity).equals("sanxing")) { //三星

					double price=knPayInfo.getPrice(); //金额
					params.put("SxPrice", String.valueOf(Integer.parseInt(new java.text.DecimalFormat("0").format(price))/100));
					params.put("Sxappuserid",gamuser.getUid()); //用户在商户应用的唯一标识，建议为用户帐号。
					params.put("SxCurrency","RMB"); //货币类型
					params.put("SxWaresid",knPayInfo.getProductId()); //商品编号

				}if(Util.getChannle(activity).equals("jinli")){//金立

					double price=knPayInfo.getPrice();
					params.put("jluid",gamuser.getExtraInfo()); //sdk登录成功返回的user_id。
					params.put("jlsubject",knPayInfo.getProductName()); //商品名称
					params.put("jltotal_fee",String.valueOf(Integer.parseInt(new java.text.DecimalFormat("0").format(price))/100)); //需支付金额
					params.put("jldeliver_type","1"); //付款方式：1为立即付款，2为货到付款
					params.put("jldeal_price",String.valueOf(Integer.parseInt(new java.text.DecimalFormat("0").format(price))/100)); //商品总金额
					//params.put("jladChannel","2802003"); //后台兼容新老支付接口判断标识

				}if(Util.getChannle(activity).equals("xiaoqi")){//小七

					JSONObject json=new JSONObject();
					double price=knPayInfo.getPrice();
					try {
						json.put("extends_info_data","");
						json.put("game_area",gamuser.getServerName());
						json.put("game_level",gamuser.getUserLevel());
						//json.put("game_ordeid",userInfo.getExtenInfo());
						json.put("game_price",String.valueOf(Integer.parseInt(new java.text.DecimalFormat("0").format(price))/100));
						json.put("game_role_id",gamuser.getRoleId());
						json.put("game_role_name",gamuser.getUsername());
						json.put("notify_id","-1");
						json.put("subject","元宝");
						json.put("game_guid",userInfo.getExtenInfo());
					}catch(JSONException e){
						e.printStackTrace();
					}
					LogUtil.log("小七json数据:"+json.toString());
					params.put("extra",json.toString()); //商品名称
				}
			}

			params.put("productName", knPayInfo.getProductName());
			params.put("lv", String.valueOf(gamuser.getUserLevel()));
			params.put("nick_name", gamuser.getUsername());
			params.put("price" , String.valueOf(knPayInfo.getPrice()));
			params.put("extraInfo", knPayInfo.getExtraInfo());
			/*LogUtil.log("支付sig签名："+"game_id:"+game_id + " channel:"+channel +" platform:"+ platform +
					"  uid:"+uid + "  open_id:"+open_id+"  server_id:"
					+ server_id + "  AppKey:"+gameInfo.getAppKey());*/
			params.put(
					"sign",
					Md5Util.getMd5(game_id + channel + platform + uid + open_id
							+ server_id + gameInfo.getAppKey()));

			if(!Util.isNetWorkAvailable(activity)){
				LoadingDialog.dismiss();
				Util.ShowTips(activity,"请检查网络是否连接");
				return ;
			}

			HttpRequestUtil.okPostFormRequest( Constants.APPLY_ORDER, params,callBack);

		} catch (Exception e) {
			LoadingDialog.dismiss();
			Delegate.listener.callback(ResultCode.APPLY_ORDER_FAIL,"申请订单号失败");
			//listener.onFail(new Result(ResultCode.FAIL, "申请订单号失败"));
			e.printStackTrace();
		}
	}


	//发送等级url
	public static void enterGame(Activity activity,String sceneId,HttpRequestUtil.DataCallBack callBack) {
		try {
			GameUser gameUser = Data.getInstance().getGameUser();
			HashMap<String,String> params = getCommonParams(activity.getApplicationContext());
			if(gameUser!=null){
				params.put("lv", String.valueOf(gameUser.getUserLevel()));
				params.put("extraInfo", gameUser.getExtraInfo());
				params.put("nick_name", gameUser.getUsername());
			}
			if(sceneId.equals("1")){
				params.put("act_type", sceneId);
			}else if(sceneId.equals("2") || sceneId.equals("4") ) {
				params.put("act_type", sceneId);
			}
			HttpRequestUtil.okPostFormRequest( Constants.ENTER_GAME, params,callBack);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//热血传奇获取打包测试url
	public static void doHtmlUrl(final Activity activity, Map<String, Object> data, HttpRequestUtil.DataCallBack callBack){

		try{
			HashMap<String , String> params = new HashMap<String, String>();
			params.put("game_id",(String) data.get("game_id"));
			params.put("app_key",(String) data.get("app_key"));
			params.put("platform",(String) data.get("platform"));
			params.put("channel",(String) data.get("channel"));
			params.put("ad_channel",(String) data.get("adchannel"));
			params.put("time",(String) data.get("time"));
			params.put("is_first",(String) data.get("is_first"));
			params.put("proxyVersion","1.0.0");
			String game_id = (String) data.get("game_id");
			String appkey = (String) data.get("app_key");
			String channel =(String) data.get("channel");
			String platform    = (String) data.get("platform");
			String time = (String) data.get("time");
			params.put("sign",Md5Util.getMd5(game_id+channel+platform+time+appkey));
			//LogUtil.log("请求地址数据地址:"+Constants.HTMLURL);
			String URL = "http://oms.u7game.cn/api/get_h5_url.php"; //此时SDK没有初始化，读取静态变量时避免出现java.lang.ExceptionInInitializerError
			HttpRequestUtil.okPostFormRequest(URL , params,callBack);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//获取新参数协议，该请求在应用首次启动发送
	public static void doControl_channel(final Activity activity , Map<String,Object> data ,  BaseListener listener){

		try{
			HashMap<String , String> params = new HashMap<String, String>();
			params.put("game_id",(String) data.get("game_id"));
			params.put("app_secret",(String) data.get("app_key"));
			params.put("platform",(String) data.get("platform"));
			params.put("channel",(String) data.get("channel"));
			String game_id = (String) data.get("game_id");
			String app_secret = (String) data.get("app_key");
			String channel =(String) data.get("channel");
			String platform    = (String) data.get("platform");
			String getH5url ="http://oms.u7game.cn/api/game/control_channel.php";
			params.put("sign",Md5Util.getMd5(game_id+channel+platform+app_secret));
			new CommonAsyncTask(null,getH5url, listener).execute(new Map[] { params, null, null });
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	//在线心跳包协议(30s)一次：
	public static void doHeartbeat(Activity activity, HttpRequestUtil.DataCallBack callBack){
		try{
			HashMap<String,String> params = getCommonParams(activity.getApplicationContext());
			GameInfo gameInfo = Data.getInstance().getGameInfo();
			String imei = DeviceUtil.getDeviceId();
			String app_secret = gameInfo.getAppKey();
			params.put("imei",imei);
			Collection<String> keyset= params.keySet();
			List<String> list = new ArrayList<String>(keyset);
			Collections.sort(list);
			String key = "";
			for(int i=0;i<list.size();i++){
				if(params.get(list.get(i))==null || params.get(list.get(i))=="" ){
					continue;
				}
				key += list.get(i)+"="+params.get(list.get(i))+"&";
			}
			key += "app_secret="+app_secret;
			//LogUtil.log("key="+key);
			params.put("verify",Md5Util.getMd5(key));
			HttpRequestUtil.okPostFormRequest( Constants.HEARTBRAT, params,callBack);
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	//游戏开始push数据地址(上报设备激活接口)
	public static void doPushData( final Activity activity,
								   HttpRequestUtil.DataCallBack callBack){
		try{
			HashMap<String , String> params = getCommonParams(activity.getApplicationContext());
			GameInfo gameInfo = Data.getInstance().getGameInfo();
			String appkey = "";
			String game_id = "";
			String imei = DeviceUtil.getDeviceId();
			if(null!=gameInfo.getAppKey()){
				appkey =gameInfo.getAppKey();
			}
			if(null!=gameInfo.getGameId()){
				game_id =gameInfo.getGameId();
			}
			params.put("app_key",appkey);
			params.put("sign",Md5Util.getMd5(game_id+appkey+imei));
			LogUtil.log("请求地址数据地址:"+params.toString());
			String URL = "http://oms.u7game.cn/api/record_activate.php"; //此时SDK没有初始化，读取静态变量时避免出现java.lang.ExceptionInInitializerError
			HttpRequestUtil.okPostFormRequest(Constants.PUSH_DATA, params,callBack);
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	//注销接口与退出(暂时没用)
	public static void doCancel(Activity activity, String act_type,
								HttpRequestUtil.DataCallBack callBack) {
		try {
			HashMap<String,String> params = getCommonParams(activity.getApplicationContext());
			User userInfo = Data.getInstance().getUser();
			GameInfo gameInfo = Data.getInstance().getGameInfo();
			GameUser gamuser = Data.getInstance().getGameUser();
			String nick_name ="";
			if(userInfo!=null){
				nick_name = gamuser.getUsername();
			}
			String app_secret = gameInfo.getAppKey();
			String msi = DeviceUtil.getDeviceId();
			params.put("imei",msi );
			params.put("act_type",act_type);
			params.put("nick_name",nick_name);
			Collection<String> keyset= params.keySet();
			List<String> list = new ArrayList<String>(keyset);
			Collections.sort(list);
			String key = "";
			for(int i=0;i<list.size();i++){
				if(params.get(list.get(i))==null || params.get(list.get(i))=="" ){
					continue;
				}
				key += list.get(i)+"="+params.get(list.get(i))+"&";
			}
			key += "app_secret="+app_secret;
			params.put("sign",Md5Util.getMd5(key));
			//LogUtil.log("排序字段:"+key);
			HttpRequestUtil.okPostFormRequest( Constants.CANCEL, params,callBack);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//游戏邀请码数据请求地址(暂时没用)
	public static void doPushActivation(final Activity activity , Map<String,Object> data ,  BaseListener listener){

		try{
			HashMap<String , String> params = new HashMap<String, String>();
			params.put("m",(String) data.get("m"));
			params.put("a",(String) data.get("a"));
			params.put("uid",(String) data.get("uid"));
			params.put("game",(String) data.get("game"));
			params.put("channel",(String) data.get("channel"));
			params.put("zone",(String)data.get("zone"));
			params.put("server_id",(String)data.get("server_id"));
			params.put("cdkey",(String)data.get("cdkey"));
			params.put("vip",(String)data.get("vip"));
			params.put("level",(String)data.get("level"));

			params.put("app_id",(String)data.get("app_id"));
			params.put("open_id",(String)data.get("open_id"));
			params.put("send",(String)data.get("send"));
			params.put("sign",(String)data.get("sign"));
			LogUtil.e("params:"+params.toString());
			new CommonAsyncTask(activity, Constants.ACTIVATION, listener).execute(new Map[] { params, null, null });
		}catch(Exception e){
			e.printStackTrace();
		}


	}

	//暂时没用
	public static void payData(Activity activity,
							   String content , BaseListener listener) {

		try {

			HashMap<String , String> params = new HashMap<String, String>();
			LogUtil.e("params:"+params.toString());

			params.put("content", content);

			new CommonAsyncTask(activity , Constants.PAYDATAURL, listener).execute(new Map[] { params, null, null });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static HashMap<String, String> getCommonParams(Context context){

		HashMap<String, String> params = new HashMap<String, String>();
		User userInfo = Data.getInstance().getUser();
		GameInfo gameInfo = Data.getInstance().getGameInfo();
		GameUser gamuser = Data.getInstance().getGameUser();
		String open_id="",game_id="",channel="",ad_channel="",platform="",gid="";
		String uid="",server_id="",server_name ="";

		String msi = DeviceUtil.getDeviceId();
		String time = Util.getTimes();
		String ip =DeviceUtil.getIPAddress(); //手机ip地址
		String product = DeviceUtil.getProduct();
		String mode = DeviceUtil.getPhoneType();
		String DisplayMetrics = Util.ImageGalleryAdapter(context.getApplicationContext());

		if(gameInfo!=null){
			game_id = gameInfo.getGameId();
			channel = gameInfo.getChannel();
			LogUtil.e("ad_channel="+ad_channel);
			ad_channel = gameInfo.getAdChannel();
			platform = gameInfo.getPlatform();
		}

		if(userInfo!=null){
			open_id = userInfo.getOpenId();
		}

		if(gamuser!=null){
			uid = gamuser.getUid();
			server_id = String.valueOf(gamuser.getServerId());
			server_name = String.valueOf(gamuser.getServerName());
		}

		//params.put("gid", gameInfo.getGid()); (参数暂时没用，传0时，php后端识别不了)
		params.put("game_id", game_id);
		params.put("channel", channel);
		params.put("ad_channel", ad_channel);
		params.put("uid", uid+"");
		params.put("open_id", open_id);
		params.put("server_id", server_id);
		params.put("server_name", server_name);
		params.put("mac", DeviceUtil.getMacAddress());
		params.put("platform", platform);
		params.put("phone_type", product+"_"+mode); //手机型号
		//params.put("netType", DeviceUtil.getNetWorkType()); //手机网络状态
		params.put("network", DeviceUtil.getNetWorkType()); //手机网络状态
		params.put("msi", msi ); //手机IMEI码
		params.put("channelVersion", OpenSDK.getInstance().getChannelVersion());
		//params.put("channelVersion", "1.0.1");
		params.put("proxyVersion", OpenSDK.getInstance().getProxyVersion());
		String appInfo = Util.getAppInfo( Data.getInstance().getGameActivity() );
		params.put("packageName", Util.getJsonStringByName(appInfo, "packageName") );
		params.put("versionName", Util.getJsonStringByName(appInfo, "versionName") );
		params.put("versionCode", Util.getJsonStringByName(appInfo, "versionCode") );
		params.put("ip",ip); //手机型号
		params.put("time",time); //当前时间
		params.put("system",Util.getSystemVersion()); //手机系统版本
		params.put("memory",Util.getTotalMemorySize()); //手机内存大小
		params.put("resolution",DisplayMetrics); //当前手机分辨率
		return params;

	}

}
