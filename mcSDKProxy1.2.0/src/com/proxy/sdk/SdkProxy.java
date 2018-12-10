package com.proxy.sdk;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import com.proxy.Constants;
import com.proxy.Data;
import com.proxy.Listener;
import com.proxy.ResultCode;
import com.proxy.bean.FuncButton;
import com.proxy.bean.GameInfo;
import com.proxy.bean.GameUser;
import com.proxy.bean.KnPayInfo;
import com.proxy.call.Delegate;
import com.proxy.listener.BaseListener;
import com.proxy.listener.InitListener;
import com.proxy.listener.LoginListener;
import com.proxy.listener.PayListener;
import com.proxy.listener.PushActivationListener;
import com.proxy.listener.PushDataListener;
import com.proxy.listener.RoleReportListener;
import com.proxy.service.HttpService;
import com.proxy.tools.HttpRequestUtil;
import com.proxy.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class SdkProxy {
	
	protected Data knData = Data.getInstance();
	protected Listener kNListener = Listener.getInstance();
	protected LoginListener mLoginListener =  null;
	protected PayListener mPayListener = null;
	protected PushDataListener mPushDataListener = null ;
	protected PushActivationListener   mPushActivationListener = null ;
	protected RoleReportListener mRoleReportListener =null;
	protected InitListener mInitListener = null;
	protected GameInfo mGameInfo = null;
	protected GameUser mGameUser = null;
	private Timer mtimer;
	private Message message = null ;
	protected Activity mActivity = null;
	public static OnClickListener onclickwx = null;
	public static OnClickListener onclickqt = null;

	protected void onCreate(Activity activity){
		mActivity = activity;
		mGameInfo = knData.getGameInfo();
		mInitListener = kNListener.getInitListener();
		if(mInitListener==null){
			//LogUtil.e("没有设置初始化回调");
		}else{
			//LogUtil.e("设置初始化回调==");
		}
		mLoginListener =  kNListener.getLoginListener();

	}
	
	protected boolean canEnterGame() {
		return true;
	}

	/**
	 * 上报数据
	 * @param data
	 */
	protected void onEnterGame(Map<String, Object> data) {
		setUserData(data);
		String sceneId = data.get(Constants.SCENE_ID)!=null?data.get(Constants.SCENE_ID).toString():"";
		HttpService.enterGame(mActivity, sceneId, new HttpRequestUtil.DataCallBack() {
			@Override
			public void requestSuccess(String result) throws Exception {
				LogUtil.i("send enterGame data is successd! "+result);
				JSONObject obj = null;
				try {
					obj = new JSONObject(result);
					int code = obj.getInt("code");
					if(code == 0){
						Delegate.listener.callback(ResultCode.ENTERGAME_SUCCESS,result);
					}else {
						Delegate.listener.callback(ResultCode.ENTERGAME_FAIL,result);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void requestFailure(String request, IOException e) {
				LogUtil.i("send enterGame data is Failure! "+request);
				Delegate.listener.callback(ResultCode.ENTERGAME_FAIL,request);
			}

			@Override
			public void requestNoConnect(String msg, String data) {
				LogUtil.i("send enterGame data is Failure! "+msg);
				Delegate.listener.callback(ResultCode.ENTERGAME_FAIL,msg);
			}
		});

		if(sceneId.equals("1")){
			if(mtimer!=null){
				//LogUtil.log("先关闭心跳");
				mtimer.cancel();
			}
			mtimer=new Timer();
			mtimer.schedule(new TimerTask() {
				@Override
				public void run() {
					// (1) 使用handler发送消息
					LogUtil.log("=====doHeartbeat()====");
					message = new Message();
					message.what=0;
					mHandler.sendMessage(message);
				}
			},30000,30000);//每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行
		}
	}

	private  MyHandler mHandler = new MyHandler();

	private  class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0){
				HttpService.doHeartbeat(mActivity, new HttpRequestUtil.DataCallBack() {
					@Override
					public void requestSuccess(String result) throws Exception {
						LogUtil.i("send doHeartbeat data is Success!" + result);
					}
					@Override
					public void requestFailure(String request, IOException e) {
						LogUtil.i("send doHeartbeat data is failed!"+ request);
					}
					@Override
					public void requestNoConnect(String msg, String data) {
						LogUtil.i("send doHeartbeat data is failed!"+ msg);
					}
				});
			}
		}
	}

	
	private void setUserData(Map<String, Object> data) {

		mGameUser = new GameUser();

		LogUtil.e("setUserData func is entergame data222: " + data);
			try {
				String userId = data.get("userId") == null ? "none" : data.get("userId").toString();
				int serverId = data.get("serverId") == null ? 0 : Integer.parseInt(data.get("serverId").toString());
				LogUtil.e("entergame : userId=" + userId + " serverId =  "+ serverId);

				int userLevel = data.get("userLv") == null ? 0 : Integer.parseInt(data.get("userLv").toString());
				LogUtil.e("userLevel = " + userLevel);
				mGameUser.setUid(userId);
				mGameUser.setServerId(serverId);
				mGameUser.setUserLevel(userLevel);
				mGameUser.setExtraInfo(data.get("extraInfo") == null ? "none": data.get("extraInfo").toString());
				mGameUser.setServerName(data.get("serverName") == null ? "none": data.get("serverName").toString());
				mGameUser.setUsername(data.get("roleName") == null ? "none": data.get("roleName").toString());
				mGameUser.setVipLevel(data.get("vipLevel") == null ? "0" : data.get("vipLevel").toString());
				mGameUser.setFactionName(data.get("factionName") == null ? "none": data.get("factionName").toString());
				mGameUser.setNewRole(data.get("isNewRole") == null ? false: (Boolean) data.get("isNewRole"));
				mGameUser.setRoleId(data.get("roleId") == null ? "0" : data.get("roleId").toString());
				mGameUser.setScene_id(data.get("scene_id") == null ? "0" : data.get("scene_id").toString());
				mGameUser.setBalance(data.get("balance") == null ? "0" : data.get("balance").toString());
				mGameUser.setRoleCTime(data.get("roleCTime") == null ? "0": data.get("roleCTime").toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			knData.setGameUser(mGameUser);
	}
	
	protected void showUserCenter() {
		LogUtil.i("<============showUserCenter================>");
	}
	
	protected void onEnterGame(Map<String, Object> data , BaseListener baseListener) {
		LogUtil.i("<============onEnterGame================>");
		setUserData(data);
		String sceneId = data.get(Constants.SCENE_ID)!=null?data.get(Constants.SCENE_ID).toString():"";
		//HttpService.enterGame(mActivity,sceneId,baseListener);
	}
	private void setUserLv(int userLevel){
		LogUtil.e("userLevel: "+userLevel);			
		Data.getInstance().getGameUser().setUserLevel(userLevel);
	}

	protected void onGameLevelChanged(int newlevel) {
		LogUtil.i("<============onGameLevelChanged================>");
		setUserLv(newlevel);
	}

	protected void onResume() {
		LogUtil.i("<============onResume================>");
	}

	protected void onPause() {
		LogUtil.i("<============onPause================>");
	}

	protected void onStop() {
		LogUtil.i("<============onStop================>");
	}

	protected void onDestroy() {
		LogUtil.i("<============onDestroy================>");
		camcel();
	}
	
	protected void onRestart() {
		LogUtil.i("<============onRestart================>");
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtil.i("<============onActivityResult================>");
	}
	
	protected void login(Activity activity , Map<String, Object> params) {
		LogUtil.i("<============login================>");
	}
	
	protected void pay( Activity activity ,KnPayInfo knPayInfo) {
		LogUtil.i("<============pay================>");
		mPayListener = kNListener.getPayListener();
		if(mPayListener == null){
			//LogUtil.e("没有设置支付回调");
		}
	}

	protected boolean hasThirdPartyExit() {
		return false;
	}

	protected boolean hasSwitchUserView() {
		return false;
	}

	protected void onThirdPartyExit() {
		LogUtil.i("<============onThirdPartyExit================>");
	}
	
	protected void onQuit() {
		LogUtil.i("<============onQuit================>");
	}
	
	protected String getChannelVersion() {
		return "1.0.0";
	}

	public String getChannelName() {
		return mGameInfo.getChannel();
	}

	protected void onNewIntent(Intent intent) {
		LogUtil.i("<============onNewIntent================>");
	}
	
	protected void onConfigurationChanged(Configuration newConfig){
		LogUtil.i("<============onConfigurationChanged================>");
	}

	protected void onStart() {
		LogUtil.i("<============onStart================>");
	}
	
	protected FuncButton[] getSettingItems(){
		/*return  new FuncButton[]{
				new FuncButton("账号注销",new FuncButton.OnClickListener() {
					@Override
					public void onClick() {
						System.err.println("账号注销");
					}
				}),
			};*/
		return null;
	}
	
	protected void callSettingView() {
		
		if(this.getSettingItems()!=null)
		{
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//mActivity.startActivity(new Intent(mActivity , settingActivity.class));
				}
			});
		}
		else
			LogUtil.e("No SDK settings page");
		
	}

	protected void onSaveInstanceState(Bundle outState) {
		
		LogUtil.i("<============onSaveInstanceState================>");
		
	}

	public String getAdChannel() {
		return mGameInfo.getAdChannel();
	}
	
	protected void  pushData( final Activity activity , Map<String,Object> data  ) {
		LogUtil.e("<============pushData================>");
		mPushDataListener = kNListener.getPushDataListener();
		if(mPushDataListener == null ){
			//LogUtil.e("没有设置推送回调");
			return ;
		}
	}
	
	public void  pushActivation( final  Activity activity , Map<String,Object> data ) {
		
		LogUtil.e("<============pushActivation================>");
		mPushActivationListener = kNListener.getPushActivationListener();
		if(mPushActivationListener == null ){
			//LogUtil.e("没有设置激活码回调");
			return ;
		}
		
	}
	
	protected void activation( final Activity activity ) {
		
		
		
	}
	
	protected void finish(){
		
		LogUtil.i("<============finish================>");
		
	}
	
	protected void onWindowFocusChanged(boolean hasFocus) {
		
		LogUtil.i("<============onWindowFocusChanged================>");
		
	}
	
	protected void switchAccount() {
		LogUtil.i("<============switchAccount================>");
	}
	
	protected void logout(){
		LogUtil.i("<============logout================>");
		camcel();
	}
	
	protected void onBackPressed() {
		// TODO Auto-generated method stub
		LogUtil.i("<============onBackPressed================>");
	}
	
	//关闭心跳计时
	protected void camcel(){
		if(mtimer!=null){
			   LogUtil.log("关闭心跳");
			   mtimer.cancel();
			}
	}
}
