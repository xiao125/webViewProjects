package com.proxy.sdk.channel;
import java.io.IOException;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.proxy.Data;
import com.proxy.OpenSDK;
import com.proxy.ResultCode;
import com.proxy.bean.KnPayInfo;
import com.proxy.bean.User;
import com.proxy.call.Delegate;
import com.proxy.listener.BaseListener;
import com.proxy.sdk.SdkProxy;
import com.proxy.service.HttpService;
import com.proxy.tools.HttpRequestUtil;
import com.proxy.util.LoadingDialog;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;
import cn.uc.gamesdk.UCGameSdk;

import cn.gundam.sdk.shell.even.*;
import cn.gundam.sdk.shell.param.*;
import cn.gundam.sdk.shell.exception.*;
import cn.gundam.sdk.shell.open.*;
import cn.gundam.sdk.shell.open.UCOrientation;


public class SdkChannel extends SdkProxy {

	private static SdkChannel instance = null;
	private Data knData = Data.getInstance();
	private static Activity  mActivity;
	public boolean mRepeatCreate = false;
	public boolean isSdkInit =false;
	private String uc_sid;
	public  String  uc_sign;
	private String accountId ; //登录成功后获取的accountId

	/**
	 * @return sdk渠道的版本号
	 */
	@Override
	public String getChannelVersion(){
		return "8.0.4.0";
	}

	public static SdkChannel getInstance(){
		if(instance == null)
			instance = new SdkChannel();
		return instance;
	}

	@Override
	public void onCreate(Activity activity){
		super.onCreate(activity);
		mActivity=activity;
		//sdk初始化
		sdkinit();
	}

	//sdk初始化
	public void sdkinit(){
		LogUtil.log("中间件开始调用SDK初始化");
		/*if ((mActivity.getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
			mRepeatCreate = true;
			mActivity.finish();
			return;
		}*/
		isSdkInit =false;
		LoadingDialog.show(mActivity, "正在初始化中.....", false);
		ucNetworkAndInitUCGameSDK();
		UCGameSdk.defaultSdk().registerSDKEventReceiver(receiver);
	}

	public void ucNetworkAndInitUCGameSDK() {
		//!!!在调用SDK初始化前进行网络检查
		ucSdkinit();//执行UCGameSDK初始化
	}

	//UC初始化
	private void ucSdkinit(){
		ParamInfo gameParamInfo = new ParamInfo();
		gameParamInfo.setGameId(SDKConfig.Game_Id); //游戏参数
		gameParamInfo.setOrientation(UCOrientation.PORTRAIT); //横屏
		gameParamInfo.setEnableUserChange(true);//开启账号切换功能
		SDKParams sdkParams = new SDKParams();
		sdkParams.put(SDKParamKey.GAME_PARAMS, gameParamInfo);
		try {
			UCGameSdk.defaultSdk().initSdk(mActivity, sdkParams);
		} catch (AliLackActivityException e) {
			e.printStackTrace();
		}
	}

	SDKEventReceiver receiver = new SDKEventReceiver() {
		@Subscribe(event = SDKEventKey.ON_INIT_SUCC)
		private void onInitSucc() {
			//初始化成功
			LogUtil.log("UC sdk初始化成功");
			isSdkInit = true;
			LoadingDialog.dismiss();
			Delegate.listener.callback(ResultCode.INIT_SUCCESS,"初始化成功");
		}

		@Subscribe(event = SDKEventKey.ON_INIT_FAILED)
		private void onInitFailed(String data) {
			//初始化失败
			LogUtil.log("UC sdk初始化失败"+data);
			LoadingDialog.dismiss();
			Util.ShowTips(mActivity,data);
		}

		@Subscribe(event = SDKEventKey.ON_LOGIN_SUCC)
		private void onLoginSucc(String sid) {
			uc_sid = sid;
			Knlogin(sid);
		}

		@Subscribe(event = SDKEventKey.ON_LOGIN_FAILED)
		private void onLoginFailed(String desc) {
			LogUtil.log("UC sdk登录失败....");
			//Util.ShowTips(mActivity,desc);
			if(isSdkInit == true){
				try {
					UCGameSdk.defaultSdk().login(mActivity, null);
				} catch (AliLackActivityException e) {
					e.printStackTrace();
				} catch (AliNotInitException e) {
					e.printStackTrace();
				}
			}else {
				LogUtil.log("sdk未初始化成功");
				Util.ShowTips(mActivity,"sdk未初始化成功,调起登录失败.");
				return;
			}

		}

		@Subscribe(event = SDKEventKey.ON_CREATE_ORDER_SUCC)
		private void onCreateOrderSucc(OrderInfo orderInfo) {
			if (orderInfo != null) {
				String txt = orderInfo.getOrderAmount() + "," + orderInfo.getOrderId() + "," + orderInfo.getPayWay();
				LogUtil.log( "订单已生成，获取支付结果请留意服务端回调"+txt);
			}
		}

		@Subscribe(event = SDKEventKey.ON_PAY_USER_EXIT)
		private void onPayUserExit(OrderInfo orderInfo) {

			if (orderInfo != null) {
				String txt = orderInfo.getOrderAmount() + "," + orderInfo.getOrderId() + "," + orderInfo.getPayWay();
				LogUtil.log( "UC 支付界面关闭"+txt);
			}
		}

		@Subscribe(event = SDKEventKey.ON_LOGOUT_SUCC)
		private void onLogoutSucc() {
			LogUtil.log("UC sdk退出账号回调");

		}

		@Subscribe(event = SDKEventKey.ON_LOGOUT_FAILED)
		private void onLogoutFailed() {
			LogUtil.log("sdk退出账号失败");
		}

		@Subscribe(event = SDKEventKey.ON_EXIT_SUCC)
		private void onExit(String desc) {
			LogUtil.log("sdk退出游戏成功");
			mActivity.finish();
			System.exit(0);
		}

		@Subscribe(event = SDKEventKey.ON_EXIT_CANCELED)
		private void onExitCanceled(String desc) {
			LogUtil.log("sdk退出游戏失败");
		}
	};

	// 游戏登录
	private void Knlogin(String sid) {
		JSONObject json = new JSONObject();
		try {
			json.put("uc_sid", sid);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		//游戏登录验证接口
		HttpService.doLogin(mActivity, json.toString(), new HttpRequestUtil.DataCallBack() {
			@Override
			public void requestSuccess(String result) throws Exception {
				LogUtil.log("游戏方登录成功....");
				LoadingDialog.dismiss();
				JSONObject obj = null;
				final User user = new User();
				int resultCode;
				try {
					obj = new JSONObject(result.toString());
					resultCode = obj.getInt("code");
					if(resultCode == 0){
						user.setOpenId(obj.getString("open_id"));
						user.setSid(obj.getString("sid"));
						user.setSign(obj.getString("sign"));
						user.setIsIncompany(Integer.parseInt(obj
								.getString("iscompany")));
						user.setLogin(true);
						accountId = obj.getString("accountId") ;
						user.setExtenInfo(obj.getString("extra_info") );
						user.setExtraInfo1(accountId); //用户唯一标识，支付需要的参数
						Data.getInstance().setUser(user);
						//回调数据
						Delegate.listener.callback(ResultCode.LOGIN_SUCCESS,result);
					}else {
						ToastResult(result);
						Delegate.listener.callback(ResultCode.LOGIN_FAIL,result);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public void requestFailure(String result, IOException e) {
				Delegate.listener.callback(ResultCode.LOGIN_FAIL,result);
				Util.ShowTips(mActivity,result);

			}

			@Override
			public void requestNoConnect(String msg, String data) {
				Delegate.listener.callback(ResultCode.LOGIN_FAIL,msg);
				Util.ShowTips(mActivity,msg);
			}
		});
	}


	public void ToastResult(String result){
		try {
			JSONObject obj = new JSONObject(result.toString());
			String reason= obj.getString("reason");
			Util.ShowTips(mActivity,reason);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	@Override
	public boolean canEnterGame() {
		return false;
	}

	/**
	 * 游戏进入游戏首页的时候调用
	 */
	@Override
	public void onEnterGame(Map<String, Object> data) {
		super.onEnterGame(data);
		//提交用户信息
		String serverid=String.valueOf(knData.getGameUser().getServerId());
		String servername = knData.getGameUser().getServerName();
		String level= String.valueOf(knData.getGameUser().getUserLevel());
		String username=knData.getGameUser().getUsername();
		String uid=knData.getGameUser().getUid();
		String roleCtime =knData.getGameUser().getRoleCTime();
		String vip=knData.getGameUser().getVipLevel();

		// 提交用户信息
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String serverid = String.valueOf(knData.getGameUser().getServerId());
				String servername = knData.getGameUser().getServerName();
				String roleLevel = String.valueOf(knData.getGameUser().getUserLevel());
				String roleName = knData.getGameUser().getUsername();
				String uid = knData.getGameUser().getUid();
				String roleCtime = knData.getGameUser().getRoleCTime();
				String vip = knData.getGameUser().getVipLevel();
				String roleId = knData.getGameUser().getRoleId();// roleId :游戏内角色id
				SDKParams sdkParams = new SDKParams();
				sdkParams.put(SDKParamKey.STRING_ROLE_ID, uid);
				sdkParams.put(SDKParamKey.STRING_ROLE_NAME, roleName);
				sdkParams.put(SDKParamKey.LONG_ROLE_LEVEL, Long.valueOf(roleLevel));
				sdkParams.put(SDKParamKey.LONG_ROLE_CTIME, Long.valueOf(roleCtime));
				sdkParams.put(SDKParamKey.STRING_ZONE_ID, serverid);
				sdkParams.put(SDKParamKey.STRING_ZONE_NAME, servername);
				try {
					UCGameSdk.defaultSdk().submitRoleData(mActivity, sdkParams);
					LogUtil.log("数据已提交，查看数据是否正确，请到开放平台接入联调工具查看="+sdkParams.toString());
					LogUtil.log("sdk上报数据成功");
				}catch (AliNotInitException e) {
					e.printStackTrace();
					LogUtil.log("sdk未初始化或正在初始化时，异常处理");
				} catch (AliLackActivityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e){
					LogUtil.log("sdk上报 传入参数错误异常处理");
				}
			}
		});

		if(knData.getGameUser().getScene_id().equals("1")) {//进入游戏
			LogUtil.log("进入游戏......");
		}if(knData.getGameUser().getScene_id().equals("2")) { //创建角色
			LogUtil.log("创建角色......");
		}if(knData.getGameUser().getScene_id().equals("4")) { //角色等级升级
			LogUtil.log("角色等级升级......");
		}
	}



	@Override
	public void onGameLevelChanged(int newlevel) {
		super.onGameLevelChanged(newlevel);
	}

	@Override
	public void onResume() {

		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.log("onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		LogUtil.log("onStop");

	}



	@Override
	public void onRestart() {
		super.onRestart();
		LogUtil.log("onRestart");
	}




	@Override
	public void onDestroy() {
		super.onDestroy();
		camcel();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}


	@Override
	protected void onNewIntent(Intent newIntent) {
		super.onNewIntent(newIntent);
	}


	@Override
	protected void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}



	//是否调用第三方退出
	public boolean hasThirdPartyExit(){
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void login(Activity activity, Map<String, Object> params) {
		super.login(activity , params);
		LogUtil.log("中间键开始调用sdk登录");
		//sdk登录接口
		if(isSdkInit == true){
			try {
				UCGameSdk.defaultSdk().login(mActivity, null);
			} catch (AliLackActivityException e) {
				e.printStackTrace();
			} catch (AliNotInitException e) {
				e.printStackTrace();
			}
		}else {
			LogUtil.log("sdk未初始化成功");
			return;
		}
	}



	@Override
	public void pay( final Activity activity , final KnPayInfo knPayInfo) {
		super.pay(activity,knPayInfo);
		LoadingDialog.show(activity, "拼命加载中.....", false);
		HttpService.applyOrder(activity, knPayInfo, new HttpRequestUtil.DataCallBack() {
			@Override
			public void requestSuccess(String result) throws Exception {
				LoadingDialog.dismiss();
				JSONObject obj1 = null;
				String orderno = null;
				try {
					obj1 = new JSONObject(result.toString());
					String code = obj1.getString("code");
					if("0".equals(code)){
						orderno = obj1.getString("order_no");
						String gamepay = obj1.getString("isGamePay");
						uc_sign = obj1.getString("uc_sign"); //uc 下单获取的参数服务器签名

						//下单成功返回的数据
						Delegate.listener.callback(ResultCode.APPLY_ORDER_SUCCESS,code);

						double price = knPayInfo.getPrice();
						int priceInt=Integer.parseInt(new java.text.DecimalFormat("0").format(price/100));
						String num = Integer.toString(priceInt);
						String gameId = Data.getInstance().getGameInfo().getGameId();
						String productId = knPayInfo.getProductId(); // 订单id
						String productName = knPayInfo.getProductName();// 订单名称
						String username=knData.getGameUser().getUsername();
						String serverid=Integer.toString(knData.getGameUser().getServerId());
						String des = knPayInfo.getCoinName();

						//根据返回isGamePay字段判断微信或爱贝
						if (Integer.valueOf(gamepay) == 1) {
							String url = obj1.getString("webPayUrl"); //web支付总界面url
							String wxUrl = obj1.getString("wimipayUrl");//微信支付跳转url
							LogUtil.log("支付url = "+url+"  wxUrl="+wxUrl);
							OpenSDK.getInstance().openWeb(mActivity,url,wxUrl);
						}else {

							//sdk支付接口
							try {
								SDKParams sdkParams = new SDKParams();
								sdkParams.put(SDKParamKey.CALLBACK_INFO,"自定义信息");
								sdkParams.put(SDKParamKey.AMOUNT,String.valueOf(priceInt));//充值金额,单位元
								sdkParams.put(SDKParamKey.NOTIFY_URL,SDKConfig.NOTIFY_URL);//服务器通知地址
								sdkParams.put(SDKParamKey.CP_ORDER_ID,orderno);//cp充值订单号
								sdkParams.put(SDKParamKey.ACCOUNT_ID,accountId);//服务端调用登录会话验证接口verifySession返回的accountId，即用户唯一标识
								sdkParams.put(SDKParamKey.SIGN_TYPE, "MD5");//签名类型，MD5或者RSA
								sdkParams.put(SDKParamKey.SIGN,uc_sign);//签名结果，该参数不需要参与签名MD5(签名内容+apiKey); 服务端生成
								LogUtil.log("订单号:"+orderno+"订单支付金额:"+String.valueOf(priceInt)+"角色:"+username+"区服id:"
										+serverid+" accountId:"+accountId+" 商品id="+productId);
								UCGameSdk.defaultSdk().pay(mActivity, sdkParams);

							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (AliLackActivityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (AliNotInitException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}else {
						Util.ShowTips(mActivity,result);
						Delegate.listener.callback(ResultCode.APPLY_ORDER_FAIL,result);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public void requestFailure(String request, IOException e) {
				LoadingDialog.dismiss();
				Delegate.listener.callback(ResultCode.APPLY_ORDER_FAIL,request);
			}
			@Override
			public void requestNoConnect(String msg, String data) {
				LoadingDialog.dismiss();
				Delegate.listener.callback(ResultCode.APPLY_ORDER_FAIL,msg);
			}
		});
	}


	@Override
	protected void switchAccount() {
		// TODO Auto-generated method stub
		super.switchAccount();
		//注销账号
		LogUtil.log("切换游戏账号");
		camcel(); //关闭心跳包
		try {
			UCGameSdk.defaultSdk().logout(mActivity, null);
		} catch (AliLackActivityException e) {
			e.printStackTrace();
		} catch (AliNotInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onThirdPartyExit() {
		// TODO Auto-generated method stub
		super.onThirdPartyExit();
		LogUtil.log("退出游戏");
		try {
			UCGameSdk.defaultSdk().exit(mActivity, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void logout() {
		// TODO Auto-generated method stub
		super.logout();
		LogUtil.log("调用sdk注销登录");
		try {
			UCGameSdk.defaultSdk().logout(mActivity, null);
		} catch (AliLackActivityException e) {
			e.printStackTrace();
		} catch (AliNotInitException e) {
			e.printStackTrace();
		}
		Delegate.listener.callback(ResultCode.LOGOUT,"1");

	}

	@Override
	protected void onQuit() {
		super.onQuit();
	}

	//游戏邀请码（暂时没用）
	public void pushActivation(  final Activity activity , final Map<String, Object>  data  ){

		HttpService.doPushActivation(activity, data,  new BaseListener() {
			@Override
			public void onSuccess(Object result) {
				activity.finish();
			}

			@Override
			public void onFail(Object result) {
				activity.finish();
			}
		} );
	}


}





