package com.proxy.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.game.sdkproxy.R;
import com.proxy.Constants;
import com.proxy.Data;
import com.proxy.Listener;
import com.proxy.Splash;
import com.proxy.bean.User;
import com.proxy.listener.BaseListener;
import com.proxy.listener.InvitationListener;
import com.proxy.sdk.SdkCenter;
import com.proxy.sdk.SdkProxy;
import com.proxy.task.CommonAsyncTask;
import com.proxy.util.DeviceUtil;
import com.proxy.util.LoadingDialog;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Invitation extends Activity {
	
	private static Activity mActivity = null ;
	private static View     mBaseView = null ;
	private static InvitationListener mListener = null ;
	
	private static EditText    mEditText   			= null ;
	private static String      mServer_id            = null ;
	
	private SdkCenter sdkCenter = SdkCenter.getInstance();
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mActivity = this ;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//setContentView(R.layout.kn_invite);
		setContentView(R.layout.pmc_invite);
		
		Intent intent = getIntent();
		if(intent.hasExtra("server_id")){
			mServer_id = intent.getStringExtra("server_id"); 
		}
		
		mEditText = (EditText) findViewById(R.id.invite__et);
		
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}


	public static InvitationListener getListener(){
		
		return mListener ; 
		
	}
	
	
	public void submit(View vt){
		
		LogUtil.e("邀请码确定");
		String yaoqing = mEditText.getText().toString().trim();
		
		 if(TextUtils.isEmpty(yaoqing)){
	            Util.ShowTips(mActivity,"请输入邀请码");
	            return ;
	        }
		
		 //请求邀请码接口
		 postactivations(yaoqing);
		
	}
	
	
	private void postactivations (String ed){
		
		LogUtil.e("yaoqing="+ed);
		String app_secret = "3d759cba73b253080543f8311b6030bf";
		String imei = DeviceUtil.getDeviceId();
		final String PROXY_VERSION = "1.0.1" ;
		String gameId= Data.getInstance().getGameInfo().getGameId();

		//获取时间
		 SimpleDateFormat formatter   =   new   SimpleDateFormat("yyyyMMddHHmmss");
	     Date curDate =  new Date(System.currentTimeMillis());
	     String   time  =   formatter.format(curDate);
		
		
		Map<String, String> params = new TreeMap<String, String>( new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg0.compareTo(arg1);
			}
		} );
		
		
		
		
		params.put("time", String.valueOf(time));	
		params.put("cdk", ed);
		params.put("msi", imei);
		params.put("proxyVersion",PROXY_VERSION);
		params.put("game_id",gameId);
		Map<String, String> update_params = Util.getSign( params , app_secret );
	
		
		LoadingDialog.show(mActivity,"请求中...", true);
		new CommonAsyncTask(mActivity,Constants.URL.ACTIVATIONS, new BaseListener() {

			@Override
			public void onSuccess(Object result) {
				// TODO Auto-generated method stub
				LoadingDialog.dismiss();
				LogUtil.e("result="+result.toString());
				
				Util.ShowTips(mActivity,"游戏激活成功，开始登录游戏");	
				
			
				
				mActivity.finish();		
			
			}

			@Override
			public void onFail(Object result) {
				// TODO Auto-generated method stub
				LoadingDialog.dismiss();
				LogUtil.e("result="+result.toString());				
				
				try {
					JSONObject jsonObject = new JSONObject(result.toString());
					int resultCode = jsonObject.getInt("code");
					  if(resultCode ==5){
						  Util.ShowTips(mActivity,"激活码已经被使用");
					  }else {
						  Util.ShowTips(mActivity,"激活码错误，请重新输入");
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
				
			}
			
		}).execute(new Map[] { update_params , null, null });
		
		
		
		
	}
	
	
	//发送激活码
	/*public void submit( View vt){
		
		LogUtil.e("邀请码确定");
		String yaoqing = mEditText.getText().toString();
		LogUtil.e("yaoqing="+yaoqing);
		
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
		params.put("a", "applyInvite");
		params.put("open_id", Data.getInstance().getUser().getOpenId());
		params.put("cdkey", yaoqing);
		String ad_channel = Data.getInstance().getGameInfo().getAdChannel();
		if(null==mServer_id){
			
		}else{
			
			params.put("server_id",mServer_id);
			
		}
		
		params.put("ad_channel",ad_channel);
		params.put("app_id",app_id);
		
		Map<String, String> update_params1 = Util.getSign( params , app_secret );
		
		LoadingDialog.show(mActivity,"请求中...", true);
		new CommonAsyncTask(mActivity,Constants.URL.ACTIVATION, new BaseListener() {

			@Override
			public void onSuccess(Object result) {
				// TODO Auto-generated method stub
				LoadingDialog.dismiss();
				LogUtil.e("result="+result.toString());
				LogUtil.e("发送数据接收数据成功==");
				
				JSONObject obj = new JSONObject();
				try {
					
					obj = new JSONObject(result.toString());
					String version = obj.getString("version");
					LogUtil.e("version="+version.toString());
					
					JSONObject   resJson = new JSONObject(version);
					String       code    = resJson.getString("code");
					String       msg     = resJson.getString("msg");
					LogUtil.e("code="+code);
					LogUtil.e("msg="+msg);
					
					if (code.equals("0")){
						
						LogUtil.e("激活码验证成功了");
						Util.ShowTips(mActivity,"邀请码激活成功");
						Listener.getInstance().getInvivationListener().onSuccess(obj);
						Invitation.this.finish();
						
					}else{
						
						LogUtil.e("激活码验证失败了");
						Util.ShowTips(mActivity,msg);
						Listener.getInstance().getInvivationListener().onFail(obj);
						
					}
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}

			@Override
			public void onFail(Object result) {
				// TODO Auto-generated method stub
				LoadingDialog.dismiss();
				LogUtil.e("result="+result.toString());
				Util.ShowTips(mActivity,result.toString());
			}
			
		}).execute(new Map[] { update_params1 , null, null });
		
	}
	
	*/
	/*public void cancel(View vt){
	
		LogUtil.e("邀请码取消");		
		Listener.getInstance().getInvivationListener().onFail("邀请码取消");
		Invitation.this.finish();
		
		
	}*/
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if(keyCode == KeyEvent.KEYCODE_BACK){
		
			return true;
		}
		
		return false;
	}
	
	
}
