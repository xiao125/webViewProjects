package com.mc.h5game.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.mc.h5game.util.ThreadPoolUtil;
import com.mc.h5game.util.TimeUtils;
import com.mc.h5game.x5.WebAppInterface;
import com.mc.h5game.x5.X5WebView;
import com.proxy.Constants;
import com.proxy.OpenSDK;
import com.proxy.ResultCode;
import com.proxy.bean.GameInfo;
import com.proxy.bean.KnPayInfo;
import com.proxy.callback.SdkCallbackListener;
import com.proxy.util.DeviceUtil;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;
import com.rxcqh5.cs.mc.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;


@SuppressLint("NewApi")
public class WebMainHtmlHActivity extends Activity {
	//传世H5
	private String m_platform = "android";
	private String m_appKey = "XovHFMSPKstE0Gwrf8cBudDIV6JN5hC4";
	private String m_gameId = "cqsjh5";
	private String m_gameName = "cqsjh5";
	private int m_screenOrientation = 1;
	private  Activity mativity;
	private X5WebView mweview;
	private RelativeLayout mbgLayout;
	OpenSDK m_proxy = OpenSDK.getInstance();
	private FrameLayout  mFrameLayout;
	public String HtmlUrl;
	private String roleDate;
	private boolean isInit = false;
	private WebAppInterface webAppInterface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webactivity_main);
		mativity = this;
		Intent intent = getIntent();//获取传来的intent对象
		HtmlUrl = intent.getStringExtra("HTML_URL");
		init();
		showHtml(HtmlUrl);

	}

	private void init() {
		mweview = new X5WebView(this);
		mFrameLayout = findViewById(R.id.wb);
		mFrameLayout.addView(mweview);
		mweview.setBackgroundColor(Color.BLACK);
		mweview.setLayerType(View.LAYER_TYPE_HARDWARE,null);//开启硬件加速

	}

	// 显示HTML页面
	private void showHtml(String htmlUrl) {

		//initWebViewSettings();
		// 设置webView监听回调
		//WebViewListener();
		// 与js协议接口
		//mweview.addJavascriptInterface(new InitGame(), "MCBridge");
		webAppInterface = new WebAppInterface(WebMainHtmlHActivity.this,m_gameName,
				m_appKey,m_screenOrientation,mweview);
		mweview.addJavascriptInterface(webAppInterface, "MCBridge");
		webAppInterface.setWvClientClickListener(new WebviewClick()); // 这里就是js调用java端的具体实现
		mweview.loadUrl(htmlUrl);
		WebViewCacheInterceptorInst.getInstance().loadUrl(htmlUrl,mweview.getSettings().getUserAgentString());
	}


	SdkCallbackListener callbackListener = new SdkCallbackListener<String>() {
		@Override
		public void callback(int code, String response) {
			switch (code) {
				case ResultCode.INIT_SUCCESS: //初始化成功
					LogUtil.log("中间件初始化成功");
					isInit = true;
					webAppInterface.activateCallback();
					break;
				case ResultCode.INIT_FAIL:  //初始化失败
					LogUtil.log("中间件初始化失败");
					break;
				case ResultCode.LOGIN_SUCCESS:  //登录成功
					JSONObject obj = null;
					try {
						obj = new JSONObject(response);
						String open_id = obj.getString("open_id");
						String sid = obj.getString("sid");
						webAppInterface.loginCallback(open_id,sid);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case ResultCode.LOGIN_FAIL:  //登录失败
					LogUtil.log("中间件登录失败!"+ response);
					break;

				case ResultCode.ENTERGAME_SUCCESS: //数据上报成功
					roleDate = response;
					webAppInterface.roleDataCallback(roleDate);
					break;
				case ResultCode.ENTERGAME_FAIL: //数据上报失败
					LogUtil.log("中间件上报数据失败返回结果=" + response);
					break;
				case ResultCode.APPLY_ORDER_SUCCESS:  //支付成功
					webAppInterface.payCallback();
					break;
				case ResultCode.APPLY_ORDER_FAIL:  //支付失败
					LogUtil.log("支付失败！"+ response);
					break;
				case ResultCode.LOGOUT:  //游戏内切换账号
					webAppInterface.logoutCallback();
					break;
				case ResultCode.XF_LOGOUT:  //SDK悬浮窗切换账号
					webAppInterface.logoutCallback();
					break;
				default:
					break;
			}
		}
	};

	class  WebviewClick implements WebAppInterface.wvClientClickListener{
		@Override
		public void wvLogin() {
			LogUtil.log("点击登录");
			if (!isInit) {
				return;
			} else {
				m_proxy.login(mativity);
			}
		}

		@Override
		public void wvActivate() {
			LogUtil.log("sdk开始初始化=");
			Map<String, String> json = Util.readHttpData(WebMainHtmlHActivity.this);
			String gameId = json.get("game_id");
			GameInfo m_gameInfo = new GameInfo(m_gameName,m_appKey, gameId,m_screenOrientation);
			//初始化中间件
			m_proxy.init(mativity, m_gameInfo,callbackListener);
		}

		@Override
		public void wvPay(KnPayInfo knPayInfo) {
			m_proxy.pay(mativity, knPayInfo);
		}

		@Override
		public void wvRoleReport(Map<String, Object> data) {
			m_proxy.onEnterGame(data);
		}

		@Override
		public void wvU7SystemInfo() {

		}
	}

	@Override
	protected void onPause() {
		mweview.onPause();
		mweview.pauseTimers();// 调用pauseTimers()全局停止Js
		super.onPause();
		if (isInit) {
			m_proxy.onPause();
		}
		LogUtil.log("==========onPause()===========");
	}

	@Override
	protected void onResume() {
		mweview.onResume();
		mweview.resumeTimers(); // 调用onResume()恢复js
		super.onResume();
		if (isInit) {
			m_proxy.onResume();
		}
		LogUtil.log("==========onResume()===========");
	}

	@Override
	protected void onStart() {
		super.onStart();
		LogUtil.log("==========onStart()===========");
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (isInit) {
			m_proxy.onStop();
		}
		LogUtil.log("==========onStop()===========");
	}

	@Override
	protected void onDestroy() {
		if (mweview != null) {
			mweview.clearHistory();
			((ViewGroup) mweview.getParent()).removeView(mweview);
			mweview.destroy();
			mweview = null;
		}
		super.onDestroy();
		/*if (isInit) {
			m_proxy.onDestroy();
		}*/
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// if(keyCode == KeyEvent.KEYCODE_BACK && mweview.canGoBack()){
		// mweview.goBack(); //返回上一页
		// LogUtil.log("退出游戏1");
		// return true;
		//
		// }else
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) { // 防止按音量键调用退出（减小键监听）
			// LogUtil.log("按了音量减");
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) { // //防止按音量键调用退出（音量增加键监听）
			// LogUtil.log("按了音量加");
		} else {
			LogUtil.log("退出游戏2");
			if (isInit) {
				if (m_proxy.hasThirdPartyExit()) {
					m_proxy.onThirdPartyExit();
					return true;
				} else {
					LogUtil.log("退出游戏3");
					AlertDialog.Builder builder = new AlertDialog.Builder(
							WebMainHtmlHActivity.this);
					builder.setTitle("游戏");
					builder.setMessage("真的忍心退出游戏么？");
					builder.setPositiveButton("忍痛退出",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
													int which) {
									m_proxy.Quit();
									dialog.dismiss();
									System.exit(0);
								}
							});
					builder.setNegativeButton("手误点错",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
													int which) {
									dialog.dismiss();
								}
							});

					builder.create().show();
					return true;
				}
			} else {
				LogUtil.log("退出游戏4");
				AlertDialog.Builder builder = new AlertDialog.Builder(
						WebMainHtmlHActivity.this);
				builder.setTitle("游戏");
				builder.setMessage("真的忍心退出游戏么？");
				builder.setPositiveButton("忍痛退出",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								dialog.dismiss();
								System.exit(0);
							}
						});
				builder.setNegativeButton("手误点错",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								dialog.dismiss();
							}
						});

				builder.create().show();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}


	private  int Rabdoms(){
		int num = (int) ((Math.random() * 9 + 1) * 10000000);
		return num;
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) { //此时Activity显示出，即用户可见页面时
		if (hasFocus ) {
			long hotStartTime = TimeUtils.getTimeCalculate(TimeUtils.COLD_START);
			// 真正的冷启动时间 = Application启动时间 + 热启动时间
			long coldStartTime = TimeUtils.sColdStartTime + hotStartTime;
			//LogUtil.log("=======WebMain启动时间==============="+coldStartTime);
			if (TimeUtils.sColdStartTime >= 0 && hotStartTime > 0) {

			} else if (hotStartTime > 0) {

			}
		}

	}
}
