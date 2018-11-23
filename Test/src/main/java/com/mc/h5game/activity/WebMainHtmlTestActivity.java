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
public class WebMainHtmlTestActivity extends Activity {
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
		mweview.addJavascriptInterface(new InitGame(), "MCBridge");
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
					ThreadPoolUtil.execute(new Runnable() {
						@Override
						public void run() {
							WebMainHtmlTestActivity.this.runOnUiThread(new Runnable() {
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
			Map<String, String> json = Util.readHttpData(WebMainHtmlTestActivity.this);
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
			roleDate = roleReportContent;
			ThreadPoolUtil.execute(new Runnable() {
				@Override
				public void run() {
					WebMainHtmlTestActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								JSONObject jsonObject = new JSONObject(roleDate);
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

		/**
		 * 获取android手机设备相关信息
		 */
		@JavascriptInterface
		public void u7SystemInfo() {
			String DisplayMetrics = Util.ImageGalleryAdapter(WebMainHtmlTestActivity.this);
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("network",DeviceUtil.getNetWorkType()); //当前网络类型
				jsonObject.put("resolution",DisplayMetrics); //当前手机分辨率
				jsonObject.put("system",Util.getSystemVersion()); //手机系统版本
				jsonObject.put("memory",Util.getTotalMemorySize()); //手机内存大小
				jsonObject.put("imei",DeviceUtil.getDeviceId()); //手机imei
				LogUtil.log("读取到的手机信息：" + jsonObject.toString());
				u7gameSystemInfo(jsonObject.toString());

			} catch (JSONException e) {
				e.printStackTrace();
			}
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
	@TargetApi(Build.VERSION_CODES.KITKAT)
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

	@TargetApi(Build.VERSION_CODES.KITKAT)
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

	// js接口（返回android手机设备相关信息）
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void u7gameSystemInfo(final String json) {

		final int version = Build.VERSION.SDK_INT;
		if (version < 18) {
			// 调用js初始化回调
			mweview.loadUrl("javascript:u7gameSystemInfo('" + json+ "')");
		} else { // 该方法在 Android 4.4 版本才可使用，
			// 调用js初始化回调
			ThreadPoolUtil.execute(new Runnable() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// 调用js初始化回调
							mweview.evaluateJavascript("javascript:u7gameSystemInfo('" + json
											+ "')",
									new com.tencent.smtt.sdk.ValueCallback<String>() {
										@Override
										public void onReceiveValue(String value) {
											// TODO Auto-generated method stub
											LogUtil.log("上报手机信息，返回："+value);
										}
									});
						}
					});
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
		Map<String, String> json = Util.readHttpData(WebMainHtmlTestActivity.this);
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
	private void roleData(final String data) {

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
									"javascript:roleReportCallback('" + data+"')",
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


	@Override
	protected void onPause() {
		mweview.onPause();
		//mweview.pauseTimers();// 调用pauseTimers()全局停止Js
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
							WebMainHtmlTestActivity.this);
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
						WebMainHtmlTestActivity.this);
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
