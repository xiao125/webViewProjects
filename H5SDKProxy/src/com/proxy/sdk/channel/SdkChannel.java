package com.proxy.sdk.channel;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import com.game.sdk.GameSDK;
import com.game.sdk.bean.GameInfo;
import com.game.sdk.bean.UserInfo;
import com.game.sdk.floatmenu.SusViewMager;
import com.game.sdk.listener.LoginListener;
import com.proxy.Data;
import com.proxy.OpenSDK;
import com.proxy.ResultCode;
import com.proxy.bean.KnPayInfo;
import com.proxy.bean.Result;
import com.proxy.bean.User;
import com.proxy.listener.BaseListener;
import com.proxy.listener.InitListener;
import com.proxy.sdk.SdkProxy;
import com.proxy.service.HttpService;
import com.proxy.util.DeviceUtil;
import com.proxy.util.LoadingDialog;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;


public class SdkChannel extends SdkProxy {

	private static SdkChannel instance = null;
	private Data knData = Data.getInstance();
	private static Activity  mActivity;
	protected InitListener mInitListener = null;
	private GameSDK gameSDK = GameSDK.getInstance();
	private boolean hasLogin = false;	// 登录成功后才可调用上传角色信息接口
	public static String paySign = null;
	public static String payKey = null;

	/**
	 * @return sdk渠道的版本号
	 */
	@Override
	public String getChannelVersion(){
		return null;
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
		activationGame(activity);
		mInitListener = kNListener.getInitListener();
		//sdk初始化
		sdkinit();
		if(kNListener.getInitListener() != null){
			kNListener.getInitListener().onSuccess(1);
		}
	}

	//sdk初始化
	public void sdkinit(){

		LogUtil.log("点击初始化");
		LogUtil.log("channel="+mGameInfo.getAdChannel());
		GameInfo info = new GameInfo(mGameInfo.getAppKey(), mGameInfo.getGameId(), mGameInfo.getChannel(), mGameInfo.getPlatform(), mGameInfo.getAdChannel(), mGameInfo.getScreenOrientation() , mGameInfo.getAdChannelTxt());
		info.setGid(mGameInfo.getGid());

		LogUtil.log("萌创sdk初始化="+mGameInfo.getAppKey()+ mGameInfo.getGameId()+mGameInfo.getChannel()
				+mGameInfo.getPlatform()+mGameInfo.getAdChannel()
				+mGameInfo.getScreenOrientation()+mGameInfo.getAdChannelTxt());

		gameSDK.initSDK(mActivity ,info );

		// IAppPay.init(mActivity, IAppPay.LANDSCAPE,SDKConfig.appid); //需要渠道分包功能，请传入对应渠道标识ACID, 可以为空
//		if( Util.fileExits(mActivity ,"SDKFile/config.png")){
//			gameSDK.initIappaySDK( mActivity,Util.getApiappId(mActivity) , Util.getApiprivateKey(mActivity) ,  Util.getApipublicKey(mActivity)  );
//		    LogUtil.log("爱贝支付初始化成功1="+Util.getApiappId(mActivity) +" privateKey="+Util.getApiprivateKey(mActivity)+" publicKey="+Util.getApipublicKey(mActivity) );
//
//		}else{
//			gameSDK.initIappaySDK( mActivity, SDKConfig.appid ,SDKConfig.privateKey , SDKConfig.publicKey );
//			LogUtil.log("爱贝支付初始化成功2="+SDKConfig.appid +" privateKey="+SDKConfig.privateKey+" publicKey="+SDKConfig.publicKey);
//
//		}
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
		UserInfo userInfo = new UserInfo();
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
		//gameSDK.hideFloat();
		gameSDK.destoryFloat();
		super.onDestroy();
		LogUtil.log("onDestroy");


	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}


	@Override
	protected void onNewIntent(Intent newIntent) {
		// TODO Auto-generated method stub
		super.onNewIntent(newIntent);
	}


	@Override
	protected void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
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
		String gameId = Data.getInstance().getGameInfo().getGameId();

		gameSDK.login(activity, new LoginListener() {
			@Override
			public void onSuccess(Object result) {
				JSONObject obj = null;
				final User user = new User();
				String invite = "";
				String code = "";
				try {
					obj = new JSONObject(result.toString());

					LogUtil.log("游戏登录成功返回的 ogj=" + result.toString());

					user.setOpenId(obj.getString("open_id"));
					user.setSid(obj.getString("sid"));
					user.setSign(obj.getString("sign"));
					user.setIsIncompany(Integer.parseInt(obj.getString("iscompany")));
					user.setLogin(true);
					user.setExtenInfo(obj.getString("extra_info"));
					invite = obj.getString("invite");

					JSONObject inviteObj = new JSONObject(invite.toString());
					code = inviteObj.getString("code");
					//Log.e("code="+code);
					Data.getInstance().setUser(user);
					Data.getInstance().setInviteCode(code);

					mLoginListener.onSuccess(user);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFail(Object result) {
				LogUtil.log("res+fal" + result.toString());
				mLoginListener.onFail(result.toString());
			}
		},new SusViewMager.a() {

			@Override
			public void a() {
				// TODO Auto-generated method stub
				//gameSDK.hideFloat();
				kNListener.getLogoutListener().onSuccess(1);
				camcel();
			}
		});
	}



	@Override
	public void pay( final Activity activity , final KnPayInfo knPayInfo) {
		super.pay(activity,knPayInfo);

		LoadingDialog.show(activity, "拼命加载中.....", false);
		LogUtil.log("订单申请");
		HttpService.applyOrder(activity, knPayInfo, new BaseListener() {

			@Override
			public void onSuccess(Object result) {
				LoadingDialog.dismiss();
				LogUtil.log("订单申请成功+"+result);
				JSONObject obj1 = null;
				try {
					obj1 = new JSONObject(result.toString());
					final String order = obj1.getString("order_no");
					String gamepay = obj1.getString("isGamePay");
					String code = obj1.getString("code");

					//下单成功返回的数据
					mPayListener.onSuccess("code:"+code);
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
			public void onFail(Object result) {
				LoadingDialog.dismiss();
				LogUtil.log("订单申请失败");
				mPayListener.onFail(new Result(ResultCode.APPLY_ORDER_FAIL, "申请订单失败"));
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
		gameSDK.McLogout(new SusViewMager.a() {
			@Override
			public void a() {
				// TODO Auto-generated method stub
				kNListener.getLogoutListener().onSuccess(2);
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
		gameSDK.McQuit();
		kNListener.getLogoutListener().onSuccess(1);
	}

	@Override
	protected void onQuit() {
		super.onQuit();
		gameSDK.McQuit();
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

	//游戏开始push数据地址
	public void pushData( final Activity activity , Map<String,Object> data ){
		HttpService.doPushData(activity, data, new BaseListener() {
			@Override
			public void onSuccess(Object result) {
			}
			@Override
			public void onFail(Object result) {
			}
		} );

	}

	public void activationGame( Activity activity  ){
		//游戏激活
		Map<String, Object>  data = new HashMap<String, Object>();
		data.put("game_id",mGameInfo.getGameId());
		data.put("ip",DeviceUtil.getMacAddress());
		data.put("app_key",mGameInfo.getAppKey());
		String mis =DeviceUtil.getDeviceId(); //IMEI码
		data.put("imei",mis);
		data.put("platform",mGameInfo.getPlatform());
		data.put("ad_channel",mGameInfo.getAdChannel());
		data.put("phone_type",DeviceUtil.getPhoneType());
		data.put("channel",mGameInfo.getChannel());
		pushData(activity,data);
	}



}





