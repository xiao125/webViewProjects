package com.mc.h5game.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

//import com.game.sdkproxy.R;
import com.mc.h5game.util.LoadingLayout;
import com.mc.h5game.util.SPreferencesUtil;
import com.mc.h5game.util.ThreadPoolUtil;
import com.mc.h5game.util.WebResourceRequestAdapter;
import com.mc.h5game.util.WebResourceResponseAdapter;
import com.mc.h5game.x5.X5WebView;
import com.proxy.Constants;
import com.proxy.Data;
import com.proxy.OpenSDK;
import com.proxy.ResultCode;
import com.proxy.bean.GameInfo;
import com.proxy.bean.KnPayInfo;
import com.proxy.callback.SdkCallbackListener;
import com.proxy.service.HttpService;
import com.proxy.tools.HttpRequestUtil;
import com.proxy.util.DeviceUtil;
import com.proxy.util.LoadingFramelayout;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;
import com.rxcqh5.cs.mc.R;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;

@SuppressLint("NewApi")
public class WebMainActivity extends Activity {

	private  Activity mativity;
	private X5WebView mweview;

	private ViewGroup mViewParent;
	private LoadingFramelayout mLoadingFramelayout;
	private LoadingLayout mLoadingLayout;
	private ProgressBar progesss;
	private RelativeLayout mbgLayout;
	OpenSDK m_proxy = OpenSDK.getInstance();
	//热血传奇H5
	/*private String m_appKey = "uVkyGhiKWm7T2B43n5rEafHleXwPzjRU";
	private String m_gameId = "rxcqh5";
	private String m_gameName = "rxcqh5";
	private String m_platform = "android";	*/

	//传世H5
	private String m_platform = "android";
	private String m_appKey = "XovHFMSPKstE0Gwrf8cBudDIV6JN5hC4";
	private String m_gameId = "cqsjh5";
	private String m_gameName = "cqsjh5";

	/*
	 * private String m_appKey = "XHiGC90t5bVLxJdyrwAa4Ov6zkZ2BIQ7"; private
	 * String m_gameId = "xiyou"; private String m_gameName = "xiyou";
	 */
	// private String m_appKey = "XovHFMSPKstE0Gwrf8cBudDIV6JN5hC4";
	// private String m_gameId = "cqsjh5";
	// private String m_gameName = "cqsjh5";

	private int m_screenOrientation = 1;
	public String HtmlUrl;
	private String roleDate;
	private String roledata;
	private boolean isInit = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLoadingFramelayout = new LoadingFramelayout(this, R.layout.webactivity_main);
		setContentView(mLoadingFramelayout);
		mativity = this;
		init();
		Data.getInstance().setGameActivity(mativity);
		getHtmlUrl();

	}

	private void init() {

		//H5布局
		mbgLayout = (RelativeLayout)findViewById(R.id.bgLayout);
		progesss = (ProgressBar) findViewById(R.id.progesss1);
		mViewParent = (ViewGroup) findViewById(R.id.wb);
		mweview = new X5WebView(this, null);
		mViewParent.addView(mweview, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT));
		mweview.setBackgroundColor(Color.BLACK);
		mweview.setLayerType(View.LAYER_TYPE_HARDWARE,null);//开启硬件加速

	}


	private void getHtmlUrl() {

		LogUtil.log("进入主Manin页面：");

		final String is_first = (String) SPreferencesUtil.get(
				WebMainActivity.this, "is_first", "1"); // 获取到保存的key数据
		String result = Util.getAssetsFileContent(WebMainActivity.this,
				"SDKFile/adChannel.png");
		final String channel = Util.getJsonStringByName(result, "channel");
		final String adchannel = Util.getJsonStringByName(result, "adChannel");
		Map<String, Object> data = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());// 设置日期格式
		String time = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
		if (!"1".equals(is_first)) {
			Map<String, String> json = Util.readHttpData(WebMainActivity.this);
			LogUtil.log("读取log文件数据:" + json);
			data.put("game_id", json.get("game_id"));
			data.put("channel", json.get("channel"));
			data.put("app_key", m_appKey);
			data.put("platform", json.get("platform"));
			data.put("adchannel", json.get("adchannel"));
			data.put("is_first", "0");
			data.put("time", time);
		} else { // 首次启动
			data.put("game_id", m_gameId);
			data.put("app_key", m_appKey);
			data.put("channel", channel);
			data.put("adchannel", adchannel);
			// data.put("platform", m_platform);
			data.put("platform", platformData());
			data.put("is_first", "1");
			data.put("time", time);
		}

		GetHtmlUrl(data,is_first);

	}


	//请求获取游戏url地址
	private void GetHtmlUrl(Map<String, Object> data,final String is_first){

		LogUtil.log("开始请求H5游戏地址：");

		HttpService.doHtmlUrl(WebMainActivity.this, data, new HttpRequestUtil.DataCallBack() {
			@Override
			public void requestSuccess(String result) throws Exception {
				LogUtil.log("获取h5地址result=" + result);
				mLoadingFramelayout.completeLoading();
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.getInt("code");
					if(code == 0){
						HtmlUrl = jsonObject.getString("loginUrl");
						if (("1").equals(is_first) && HtmlUrl !=null) {
							SPreferencesUtil
									.put(WebMainActivity.this, "is_first", "0");// 保存sp标识
							String game_id = jsonObject.getString("game_id");
							String channel = jsonObject.getString("channel");
							String platform = jsonObject.getString("platform");
							String adchannel = jsonObject.getString("ad_channel");
							Map<String, String> pay_params = new HashMap<String, String>();
							pay_params.put("game_id", game_id);
							pay_params.put("channel", channel);
							pay_params.put("platform", platform);
							pay_params.put("adchannel", adchannel);
							Util.writeHttpData(mativity, pay_params); // 保存
						}

						ThreadPoolUtil.execute(new Runnable() {
							@Override
							public void run() {
								WebMainActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										showHtml(HtmlUrl);
									}
								});
							}
						});
					}else {
						RestartUrl();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void requestFailure(String request, IOException e) {
				RestartUrl();
			}

			@Override
			public void requestNoConnect(String msg, String data) {
				RestartUrl();
			}
		});

	}


	//错误页面显示
	private void  RestartUrl(){
		mLoadingFramelayout.loadingFailed();
		mLoadingFramelayout.setOnReloadListener(new LoadingFramelayout.OnReloadListener() {
			@Override
			public void onReload() {
				mLoadingFramelayout.startLoading(mativity);
				getHtmlUrl();
			}
		});
	}

	// 显示HTML页面
	private void showHtml(String htmlUrl) {
		// 设置webView监听回调
		WebViewListener();
		mweview.loadUrl(htmlUrl);
	}


	// 设置webView监听回调
	private void WebViewListener() {

		mweview.setWebViewClient(new WebViewClient() {
			// 不启动浏览器加载html
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtil.log("拦截url:" + url);
				if (url.startsWith("weixin://wap/pay?")) {
					LogUtil.log("微信支付拦截回调");
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url));
					startActivity(intent);
					return true;

				} else if (url.startsWith("alipays://")) {
					LogUtil.log("支付包拦截回调");
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url));
					startActivity(intent);
					return true;
				}

				return super.shouldOverrideUrlLoading(view, url);
			}

			// onPageStarted会在WebView开始加载网页时调用
			@Override
			public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
				super.onPageStarted(webView, s, bitmap);
				//LoadingDialog.show(MainActivity.this, "拼命加载游戏中....", false);
				mbgLayout.setVisibility(View.VISIBLE);
				// 与js协议接口
				mweview.addJavascriptInterface(new InitGame(), "MCBridge");
			}

			// onPageStarted会在WebView加载网页结束时调用
			@Override
			public void onPageFinished(WebView webView, String url) {
				super.onPageFinished(webView, url);
				//LoadingDialog.dismiss();
				mbgLayout.setVisibility(View.GONE);
				LogUtil.log("WebView加载结束时调用url:" + url);

			}

			// 该方法传回了错误码，根据错误类型可以进行不同的错误分类处理
			@Override
			public void onReceivedError(WebView webView, int i, String s,
										String s1) {
				super.onReceivedError(webView, i, s, s1);
				LogUtil.log("错误码：" + i);
			}

			@Override
			public WebResourceResponse shouldInterceptRequest(WebView webView,
															  String s) {
				return WebResourceResponseAdapter
						.adapter(WebViewCacheInterceptorInst.getInstance()
								.interceptRequest(s));
			}

			@Override
			public WebResourceResponse shouldInterceptRequest(WebView webView,
															  WebResourceRequest webResourceRequest) {

				return WebResourceResponseAdapter
						.adapter(WebViewCacheInterceptorInst.getInstance()
								.interceptRequest(
										WebResourceRequestAdapter
												.adapter(webResourceRequest)));
			}

		});

		mweview.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView webView, int newProgress) { // 页面加载进度
				LogUtil.log("加载进度=" + newProgress);
			//	super.onProgressChanged(webView, newProgress);
				if(newProgress ==100){
					mbgLayout.setVisibility(View.GONE);
					progesss.setVisibility(View.GONE);
				}else {
					mbgLayout.setVisibility(View.VISIBLE);
					progesss.setVisibility(View.VISIBLE);
					progesss.setProgress(newProgress);
				}
			}

			@Override
			public boolean onConsoleMessage(com.tencent.smtt.export.external.interfaces.ConsoleMessage consoleMessage) { // 查看web
				// console日志
				Log.e("console",
						"[" + consoleMessage.messageLevel() + "] "
								+ consoleMessage.message() + "("
								+ consoleMessage.sourceId() + ":"
								+ consoleMessage.lineNumber() + ")");
				return super.onConsoleMessage(consoleMessage);
			}
		});

	}



	SdkCallbackListener callbackListener = new SdkCallbackListener<String>() {
		@Override
		public void callback(int code, String response) {
			switch (code) {
				case ResultCode.INIT_SUCCESS: //初始化成功
					LogUtil.log("中间件初始化成功");
					isInit = true;
					ThreadPoolUtil.execute(new Runnable() {
						@Override
						public void run() {
							WebMainActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									activateCallback();
								}
							});
						}
					});

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
						LogUtil.log("中间件开始返回登录数据给游戏");
						loginCallback(open_id,sid);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case ResultCode.LOGIN_FAIL:  //登录失败
					LogUtil.log("中间件登录失败!"+ response);
					break;

				case ResultCode.ENTERGAME_SUCCESS: //数据上报成功
					roleDate = response;
					LogUtil.log("中间件上报数据成功返回结果=" + roleDate);
					roleData(roleDate);
					break;
				case ResultCode.ENTERGAME_FAIL: //数据上报失败
					LogUtil.log("中间件上报数据失败返回结果=" + response);
					break;
				case ResultCode.APPLY_ORDER_SUCCESS:  //支付成功
					LogUtil.log("setPayListener 支付回调成功=" + response);
					payCallback();
					break;
				case ResultCode.APPLY_ORDER_FAIL:  //支付失败
					LogUtil.log("支付失败！"+ response);
					break;
				case ResultCode.LOGOUT:  //游戏内切换账号
					LogUtil.log("游戏内切换账号！");
					logoutCallback();
					break;
				case ResultCode.XF_LOGOUT:  //SDK悬浮窗切换账号
					LogUtil.log("SDK悬浮窗切换账号！");
					// 调用js回调
					logoutCallback();
					break;
				default:
					break;
			}
		}
	};

	public class InitGame {
		@JavascriptInterface
		public void activate() {
			LogUtil.log("sdk开始初始化=");
			Map<String, String> json = Util.readHttpData(WebMainActivity.this);
			String gameId = json.get("game_id");
			GameInfo m_gameInfo = new GameInfo(m_gameName,m_appKey, gameId,m_screenOrientation);
			//初始化中间件
			m_proxy.init(mativity, m_gameInfo,callbackListener);
		}


		@JavascriptInterface
		public void login() {
			LogUtil.log("点击登录");
			if (!isInit) {
				return;
			} else {
				m_proxy.login(mativity);
			}
		}

		/**
		 * 支付
		 * @param
		 */
		@JavascriptInterface
		public void pay(String payContent) {
			LogUtil.log("开始支付"+payContent);
			try {
				JSONObject jsonObject = new JSONObject(payContent);
				String OrderNo = jsonObject.getString("extra_info")!=null?jsonObject.getString("extra_info"):""; // 游戏订单号
				String Price = jsonObject.getString("price")!=null?jsonObject.getString("price"):""; // 商品价格
				String productName = jsonObject.getString("productName")!=null?jsonObject.getString("productName"):"钻石"; // 商品名称
				String CoinName = jsonObject.getString("coinName")!=null?jsonObject.getString("coinName"):"";//货币名称
				String CoinRate = jsonObject.getString("coinRate")!=null?jsonObject.getString("coinRate"):"";//游戏货币的比率
				String ProductId = jsonObject.getString("productId")!=null?jsonObject.getString("productId"):""; //商品id
				String Desc = jsonObject.getString("desc")!=null?jsonObject.getString("desc"):"购买钻石"; //商品描述

				final KnPayInfo payInfo = new KnPayInfo();
				payInfo.setProductName(productName); // 商品名称
				payInfo.setCoinName(CoinName); // 货币名称 如:元宝
				payInfo.setCoinRate(Integer.valueOf(CoinRate)); // 游戏货币的比率
				// 如:1元=10元宝
				// 就传10
				payInfo.setPrice(Double.valueOf(Price) * 100); // 商品价格 分
				payInfo.setProductId(ProductId); // 商品Id，没有填“1"
				payInfo.setOrderNo(OrderNo); // 订单号
				payInfo.setDesc(Desc);
				payInfo.setExtraInfo(OrderNo); //透传默认订单号
				m_proxy.pay(mativity, payInfo);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		/** 上报接口
		 * js传递过来的参数={"senceType":"1","userId":"1","serverId":"3","userLv":"4",
		 * "serverName"
		 * :"战","roleName":"xiao","vipLevel":"0","roleCTime":"roleCTime"}
		 * @param roleReportContent
		 */

		@JavascriptInterface
		public void roleReport(String roleReportContent) {
			roledata = roleReportContent;

			ThreadPoolUtil.execute(new Runnable() {
				@Override
				public void run() {
					WebMainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								JSONObject jsonObject = new JSONObject(roledata);
								LogUtil.log("js传递过来的参数=" + jsonObject.toString());
								String userId = jsonObject.getString("userId")!=null?jsonObject.getString("userId"):"";// 用户id
								String serverId = jsonObject.getString("serverId")!=null?jsonObject.getString("serverId"):""; // 服务器Id
								String gameLv = jsonObject.getString("lv")!=null?jsonObject.getString("lv"):""; // 游戏等级
								String serverName = jsonObject.getString("serverName")!=null?jsonObject.getString("serverName"):""; // 玩家所在服区名称
								String roleName = jsonObject.getString("roleName")!=null?jsonObject.getString("roleName"):""; // 游戏角色名称
								String roleCTime = jsonObject.getString("roleCTime")!=null?jsonObject.getString("roleCTime"):""; // 游戏角色创建时间（时间戳）
								String vipLevel = jsonObject.getString("vipLevel")!=null?jsonObject.getString("vipLevel"):""; // 玩家VIP等级
								String user_sex = jsonObject.getString("user_sex")!=null?jsonObject.getString("user_sex"):""; // 玩家性别
								String user_age = jsonObject.getString("user_age")!=null?jsonObject.getString("user_age"):""; // 玩家年龄
								String factionName = jsonObject.getString("factionName")!=null?jsonObject.getString("factionName"):""; // 用户所在帮派名称
								String senceType = jsonObject.getString("senceType")!=null?jsonObject.getString("senceType"):""; // /场景ID(值为1则是进入游戏场景，值为2则是创建角色场景，值为4则是提升等级场景)
								String diamondLeft = jsonObject.getString("diamondLeft")!=null?jsonObject.getString("diamondLeft"):""; // 玩家货币余额
								String extraInfo = jsonObject.getString("extraInfo")!=null?jsonObject.getString("extraInfo"):""; // 玩家信息拓展字段

								Map<String, Object> data = new HashMap<String, Object>();
								data.put(Constants.USER_ID, userId); // 游戏玩家ID
								data.put(Constants.SERVER_ID, serverId); // 游戏玩家所在的服务器ID
								data.put(Constants.USER_LEVEL, gameLv); // 游戏玩家等级
								data.put(Constants.ROLE_ID, userId); // 角色ID

								// int senceType =1; //场景ID
								// String extraInfo = ""; //玩家信息拓展字段
								// String vipLevell ="0"; //玩家VIP等级
								// String factionName=""; //用户所在帮派名称
								// 场景ID;//(值为1则是进入游戏场景，值为2则是创建角色场景，值为4则是提升等级场景)
								// String diamondLeft = diamondLeft; //玩家货币余额
								data.put(Constants.EXPEND_INFO, extraInfo); // 扩展字段
								data.put(Constants.SERVER_NAME, serverName); // 所在服务器名称
								data.put(Constants.ROLE_NAME, roleName);// 角色名称
								data.put(Constants.VIP_LEVEL, vipLevel); // VIP等级
								data.put(Constants.FACTION_NAME, factionName);// 帮派名称
								data.put(Constants.SCENE_ID, senceType); // 场景ID
								data.put(Constants.ROLE_CREATE_TIME, roleCTime);// 角色创建时间
								data.put(Constants.BALANCE, diamondLeft); // 剩余货币
								data.put(Constants.IS_NEW_ROLE,
										Integer.valueOf(senceType) == 2 ? true : false); // 是否是新角色
								data.put(Constants.USER_ACCOUT_TYPE, "1"); // 玩家账号类型账号类型，0:未知用于来源1:游戏自身注册用户2:新浪微博用户3:QQ用户4:腾讯微博用户5:91用户(String)
								data.put(Constants.USER_SEX, user_sex); // 玩家性别，0:未知性别1:男性2:女性；(String)
								data.put(Constants.USER_AGE, user_age); // 玩家年龄；(String)
								m_proxy.onEnterGame(data);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
				}
			});

		}
	}

	// 调用js接口（初始化成功回调方法）
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void activateCallback() {
		final int version = Build.VERSION.SDK_INT;
		if (version < 18) {
			// 调用js初始化回调
			mweview.loadUrl("javascript:activateCallback('"+ getJson().toString() + "')");

		} else { // 该方法在 Android 4.4 版本才可使用，
			// 调用js初始化回调
			mweview.evaluateJavascript("javascript:activateCallback('"+ getJson().toString() + "')",
					new com.tencent.smtt.sdk.ValueCallback<String>() {
						@Override
						public void onReceiveValue(String s) {
						}
					});
		}

	}

	// 调用js接口（登录回调方法）
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void loginCallback(String openId,String sid) {

		final int version = Build.VERSION.SDK_INT;
		if (version < 18) {

			// 调用js初始化回调
			mweview.loadUrl("javascript:loginCallback('" + getJson(openId,sid) + "')");

		} else { // 该方法在 Android 4.4 版本才可使用，
			// 调用js初始化回调
			mweview.evaluateJavascript("javascript:loginCallback('"
							+ getJson(openId,sid) + "')",
					new com.tencent.smtt.sdk.ValueCallback<String>() {
						@Override
						public void onReceiveValue(String value) {

						}
					});

		}
	}

	// js接口（游戏回到登录界面）
	private void logoutCallback() {
		final int version = Build.VERSION.SDK_INT;
		if (version < 18) {
			// 调用js初始化回调
			mweview.loadUrl("javascript:logoutCallback()");

		} else { // 该方法在 Android 4.4 版本才可使用，

			// 调用js初始化回调
			mweview.evaluateJavascript("javascript:logoutCallback()",
					new com.tencent.smtt.sdk.ValueCallback<String>() {
						@Override
						public void onReceiveValue(String value) {
							// TODO Auto-generated method stub
							LogUtil.log("注销sdk游戏账号11111111");

						}
					});

		}

	}

	// js接口（支付回调）

	@SuppressLint("NewApi")
	private void payCallback() {
		final int version = Build.VERSION.SDK_INT;

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("reason", "支付成功");
			jsonObject.put("code", 0);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (version < 18) {

			// 调用js支付回调
			mweview.loadUrl("javascript:payCallback('" + jsonObject.toString()
					+ "')");

		} else { // 该方法在 Android 4.4 版本才可使用，

			// 调用js初始化回调
			mweview.evaluateJavascript(
					"javascript:payCallback('" + jsonObject.toString() + "')",
					new com.tencent.smtt.sdk.ValueCallback<String>() {

						@Override
						public void onReceiveValue(String value) {
							// TODO Auto-generated method stub

						}
					});
		}

	}

	// sdk登录成功返回的对象数据
	@SuppressLint("NewApi")
	private JSONObject getJson(String openId,String sid) {
	JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("open_id",openId);
			jsonObject.put("sid", sid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		LogUtil.log("返回json数据："+jsonObject);
		return jsonObject;
	}

	// 初始化成功返回的对象数据
	private JSONObject getJson() {
		Map<String, String> json = Util.readHttpData(WebMainActivity.this);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("reason", "初始化成功");
			jsonObject.put("code", 0);
			jsonObject.put("ad_channel",json.get("adchannel"));
			jsonObject.put("channel",json.get("channel"));
			jsonObject.put("imei",DeviceUtil.getDeviceId());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	// 调用js接口（上报回调方法）,目前不需要此方法
	private void roleData(String data) {

		final int version = Build.VERSION.SDK_INT;
		if (version < 18) {
			mweview.loadUrl("javascript:roleReportCallback('" + data + "')");
		} else { // 该方法在 Android 4.4 版本才可使用，
			//主线程调用（ java.lang.IllegalStateException: Calling View methods on another thread than the UI thread.）                                                                          ）
			ThreadPoolUtil.execute(new Runnable() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// 调用js初始化回调
							mweview.evaluateJavascript(
									"javascript:roleReportCallback('" + roleDate+"')",
									new com.tencent.smtt.sdk.ValueCallback<String>() {
										@Override
										public void onReceiveValue(String value) {

									}
							});
						}
					});
				}
			});

		}

	}

	// 配置不同平台号
	private String platformData() {
		String result = Util.getAssetsFileContent(WebMainActivity.this,
				"SDKFile/adChannel.png");
		String adchannel = Util.getJsonStringByName(result, "adChannel");
		if (adchannel.equals("20180928")) {
			m_platform = "android13";
		}
		if (adchannel.equals("39001001")) {
			m_platform = "android2";
		}
		if (adchannel.equals("39007003")) {
			m_platform = "android2";
		}
		if (adchannel.equals("39007004")) {
			m_platform = "android2";
		}
		if (adchannel.equals("39007005")) {
			m_platform = "android2";
		}
		if (adchannel.equals("39007006")) {
			m_platform = "android2";
		}
		if (adchannel.equals("39007007")) {
			m_platform = "android2";
		}

		return m_platform;
	}

	@Override
	protected void onPause() {
		mweview.onPause();
		mweview.pauseTimers();// 调用pauseTimers()全局停止Js
		super.onPause();
		if (isInit) {
			m_proxy.onPause();
		}

	}

	@Override
	protected void onResume() {
		mweview.onResume();
		mweview.resumeTimers(); // 调用onResume()恢复js
		super.onResume();
		if (isInit) {
			m_proxy.onResume();
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (isInit) {
			m_proxy.onStop();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mweview != null) {
			LogUtil.log("onDestroy+++++销毁webview");
			mweview.clearHistory();
			((ViewGroup) mweview.getParent()).removeView(mweview);
			mweview.destroy();
			mweview = null;
		}
		if (isInit) {
			m_proxy.onDestroy();
		}
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
							WebMainActivity.this);
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
						WebMainActivity.this);
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

}
