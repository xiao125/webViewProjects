package com.proxy.activity;


import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.game.sdkproxy.R;
import com.proxy.OpenSDK;
import com.proxy.bean.GameInfo;
import com.proxy.bean.KnPayInfo;
import com.proxy.bean.User;
import com.proxy.listener.ExitListener;
import com.proxy.listener.InitListener;
import com.proxy.listener.LoginListener;
import com.proxy.listener.LogoutListener;
import com.proxy.listener.PayListener;
import com.proxy.util.LogUtil;


public class MainActivity extends Activity {
	
	OpenSDK sdkProxy = OpenSDK.getInstance();
	
	private String appKey = "xlfC2ELejTocv7ZBP1G9Q4KWwsybpAit";
	private String gameId = "ghz";
	private int screenOrientation = 0;
	
	private LoginListener loginListener = new LoginListener() {
		
		@Override
		public void onSuccess(User user) {
			//可以从user中获取登陆验证所需要的 open_id和sid
		}
		
		@Override
		public void onFail(String result) {
			
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.pmc_main);
		setRequestedOrientation(0);
		
//		KnGameInfo gameInfo = new KnGameInfo("test", appKey, gameId,channel, screenOrientation);
		
		GameInfo gameInfo = new GameInfo("test", appKey, gameId, screenOrientation);
		
		sdkProxy.isDebugModel(true);
		
//		sdkProxy.init(this, gameInfo , new InitListener() {
//			
//			@Override
//			public void onSuccess(Object result) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onFail(Object result) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		
		sdkProxy.setLogoutListener(new LogoutListener() {
			
			@Override
			public void onSuccess(Object result) {
				// TODO Auto-generated method stub
				com.proxy.util.LogUtil.e("=================== logout success ===============");
			}
			
			@Override
			public void onFail(Object result) {
				// TODO Auto-generated method stub
				
			}
		});
		
		sdkProxy.setExitListener(new ExitListener() {
			@Override
			public void onConfirm() {
				
			}
			@Override
			public void onCancel() {
				
			}
		});
		
		//设置登陆回调
		sdkProxy.setLogoinListener(new LoginListener() {
			@Override
			public void onSuccess(User user) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("userId", 1);							//游戏玩家ID
				data.put("serverId", 1);						//游戏玩家所在的服务器ID
				data.put("userLv", 1);							//游戏玩家等级
				data.put("extraInfo", "xxx");
				sdkProxy.onEnterGame(data);
				
			}
			@Override
			public void onFail(String result) {
			}
		});
		
		
		sdkProxy.setInitListener(new InitListener() {
			
			@Override
			public void onSuccess(Object result) {
				
			}
			
			@Override
			public void onFail(Object result) {
				
			}

		});
		
		//设置支付回调
		sdkProxy.setPayListener(new PayListener() {
			
			@Override
			public void onSuccess(Object result) {
				System.err.println("onSuccess = " + result.toString());
			}
			
			@Override
			public void onFail(Object result) {
				System.err.println("onFail = " + result.toString());
			}
		});
		
		findViewById(R.id.gc_login).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						sdkProxy.login(MainActivity.this);
					}
				});
		findViewById(R.id.gc_test).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						sdkProxy.callSettingView();
					}
				});
		
		findViewById(R.id.gc_pay).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
//						CrashReport.testNativeCrash();
//						CrashReport.testJavaCrash();
						
						KnPayInfo payInfo = new KnPayInfo();
						payInfo.setCoinName("黄金");
						payInfo.setProductName("黄金");
						payInfo.setCoinRate(10);
						payInfo.setPrice(100);
						payInfo.setProductId("1");
						payInfo.setExtraInfo("aaa");
//						
						sdkProxy.pay( MainActivity.this ,  payInfo);
						
					}
				});
		
		
	}
	
	public void onEnterGame(Map<String, Object> data)
	{
		sdkProxy.onEnterGame(data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		sdkProxy.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		sdkProxy.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		sdkProxy.onDestroy();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		sdkProxy.onRestart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		sdkProxy.onStop();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		sdkProxy.onActivityResult(requestCode, resultCode, data);
	}
	
	private void showExitGameDialog() 
	{
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage("退出要退出游戏吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() 
        {
			@Override
			public void onClick(DialogInterface dialog, int index) 
			{
				// TODO Auto-generated method stub
				exitGame();
			}
		});
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() 
        {
			@Override
			public void onClick(DialogInterface dialog, int index) 
			{
				// TODO Auto-generated method stub
				
			}
		});
        builder.create().show();
	}
	
	private void exitGame() 
	{
		this.finish();
		System.exit(0);
	}
	
	
	private void onExit(){
		if( sdkProxy.hasThirdPartyExit() ){
			sdkProxy.setExitListener(new ExitListener() {
				@Override
				public void onConfirm() {
					exitGame();
				}
				
				@Override
				public void onCancel() {
				}
			});
		}else{
			showExitGameDialog();
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		if( keyCode == KeyEvent.KEYCODE_BACK ){
			onExit();
		}
		
		return super.onKeyDown(keyCode, event);
	}

}