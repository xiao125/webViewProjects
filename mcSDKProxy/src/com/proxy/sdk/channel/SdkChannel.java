package com.proxy.sdk.channel;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;

import com.game.sdk.GameSDK;
import com.game.sdk.bean.GameInfo;
import com.game.sdk.bean.UserInfo;
import com.game.sdk.callback.SdkCallbackListener;
import com.game.sdk.config.SDKStatusCode;
import com.game.sdk.floatmenu.SusViewMager;
import com.proxy.Data;
import com.proxy.OpenSDK;
import com.proxy.ResultCode;
import com.proxy.bean.KnPayInfo;
import com.proxy.bean.Result;
import com.proxy.bean.User;
import com.proxy.call.Delegate;
import com.proxy.listener.BaseListener;
import com.proxy.listener.InitListener;
import com.proxy.sdk.SdkProxy;
import com.proxy.service.HttpService;
import com.proxy.tools.HttpRequestUtil;
import com.proxy.util.DeviceUtil;
import com.proxy.util.LoadingDialog;
import com.proxy.util.LogUtil;

public class SdkChannel extends SdkProxy {

	private static SdkChannel instance = null;
	private Data knData = Data.getInstance();
	private static Activity  mActivity;
	protected InitListener mInitListener = null;
	private GameSDK gameSDK = GameSDK.getInstance();
	UserInfo userInfo = new UserInfo();

	/**
	 * @return sdk渠道的版本号
	 */
	@Override
	public String getChannelVersion(){
		return "1.0.1";
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

		GameInfo info = new GameInfo(mGameInfo.getAppKey(), mGameInfo.getGameId(), mGameInfo.getChannel(), mGameInfo.getPlatform(), mGameInfo.getAdChannel(), mGameInfo.getScreenOrientation() , mGameInfo.getAdChannelTxt());
		info.setGid(mGameInfo.getGid());
		LogUtil.log("萌创sdk初始化="+mGameInfo.getAppKey()+ mGameInfo.getGameId()+mGameInfo.getChannel()
				+mGameInfo.getPlatform()+mGameInfo.getAdChannel()
				+mGameInfo.getScreenOrientation()+mGameInfo.getAdChannelTxt());

		gameSDK.initSDK(mActivity ,info,new SdkCallbackListener<String>() {
			@Override
			public void callback(int code, String response) {
				switch (code) {
					case SDKStatusCode.SUCCESS:
						LogUtil.log("萌创SDK初始化成功");
						break;
					case SDKStatusCode.FAILURE:
						LogUtil.log("萌创SDK初始化失败");
						break;
					case SDKStatusCode.OTHER:
						break;
				}
			}
		});


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
//		LogUtil.log("提交用户信息: 角色创建时间="+Integer.valueOf(roleCtime)+"角色id="+uid+"");
		//UserInfo userInfo = new UserInfo();
		userInfo.setServerId(Integer.valueOf(serverid));
		userInfo.setUid(uid);
		userInfo.setUsername(username);
		userInfo.setOpenId(Data.getInstance().getUser().getOpenId());
		gameSDK.setUserInfo(userInfo);

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
		gameSDK.destoryFloat();
		super.onDestroy();
		LogUtil.log("onDestroy");


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
		return false;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void login(Activity activity, Map<String, Object> params) {
		super.login(activity , params);
		LogUtil.log("中间键开始调用sdk登录");

		gameSDK.login(mActivity, new SdkCallbackListener<String>() {
			@Override
			public void callback(int code, String response) {
				switch (code) {
					case SDKStatusCode.SUCCESS:
						JSONObject obj = null;
						final User user = new User();
						String invite = "";
						String codes = "";
						try {
							obj = new JSONObject(response);
							LogUtil.log("游戏登录成功返回的 ogj=" + response);
							user.setOpenId(obj.getString("open_id"));
							user.setSid(obj.getString("sid"));
							user.setSign(obj.getString("sign"));
							user.setIsIncompany(Integer.parseInt(obj.getString("iscompany")));
							user.setLogin(true);
							user.setExtenInfo(obj.getString("extra_info"));
							invite = obj.getString("invite");
							JSONObject inviteObj = new JSONObject(invite);
							codes = inviteObj.getString("code");
							Data.getInstance().setUser(user);
							Data.getInstance().setInviteCode(codes);
							//回调数据
							Delegate.listener.callback(ResultCode.LOGIN_SUCCESS,response);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case SDKStatusCode.FAILURE:
						Delegate.listener.callback(ResultCode.LOGIN_FAIL,response);
						break;
					case SDKStatusCode.OTHER:
						Delegate.listener.callback(ResultCode.LOGIN_FAIL,response);
						break;
				}

			}
		},new SusViewMager.OnLogoutListener() {
			@Override
			public void onExitFinish() {
				Delegate.listener.callback(ResultCode.XF_LOGOUT,"1");
				camcel();
			}
		});
	}



	@Override
	public void pay( final Activity activity , final KnPayInfo knPayInfo) {
		super.pay(activity,knPayInfo);
		LoadingDialog.show(activity, "拼命加载中.....", false);
		HttpService.applyOrder(activity, knPayInfo, new HttpRequestUtil.DataCallBack() {
			@Override
			public void requestSuccess(String result) throws Exception {
				LoadingDialog.dismiss();
				LogUtil.log("订单申请成功+"+result);
				JSONObject obj1 = null;
				try {
					obj1 = new JSONObject(result.toString());
					final String order = obj1.getString("order_no");
					String gamepay = obj1.getString("isGamePay");
					String code = obj1.getString("code");
					//下单成功返回的数据
					Delegate.listener.callback(ResultCode.APPLY_ORDER_SUCCESS,code);

					double price = knPayInfo.getPrice();
					final int  priceInt=Integer.parseInt(new java.text.DecimalFormat("0").format(price/100));
					final String num = Integer.toString(priceInt);
					final String gameId = Data.getInstance().getGameInfo().getGameId();
					LogUtil.log("order = "+order +"  num = "+num +"  gameId= "+gameId);
					//根据返回isGamePay字段判断微信或爱贝
					if (Integer.valueOf(gamepay) == 1) {
						String url = obj1.getString("webPayUrl"); //web支付总界面url
						String wxUrl = obj1.getString("wimipayUrl");//微信支付跳转url
						LogUtil.log("支付url = "+url+"  wxUrl="+wxUrl);
						OpenSDK.getInstance().openWeb(mActivity,url,wxUrl);
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
		camcel();
		//游戏切换账号接口
		gameSDK.mcLogout(new SusViewMager.OnLogoutListener() {
			@Override
			public void onExitFinish() {
				Delegate.listener.callback(ResultCode.LOGOUT,"2");
			}
		});

	}



	@Override
	protected void onThirdPartyExit() {
		// TODO Auto-generated method stub
		super.onThirdPartyExit();
		LogUtil.log("退出游戏");
	}

	@Override
	protected void logout() {
		// TODO Auto-generated method stub
		super.logout();
		LogUtil.log("调用sdk注销登录");
		gameSDK.mcQuit();
		Delegate.listener.callback(ResultCode.LOGOUT,1);
	}

	@Override
	protected void onQuit() {
		super.onQuit();
		gameSDK.mcQuit();
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





