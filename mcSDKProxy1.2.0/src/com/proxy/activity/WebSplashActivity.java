package com.proxy.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.game.sdkproxy.R;
import com.proxy.Data;
import com.proxy.service.HttpService;
import com.proxy.tools.HttpRequestUtil;
import com.proxy.util.LoadingFramelayout;
import com.proxy.util.LogUtil;
import com.proxy.util.PermissionHelper;
import com.proxy.util.PermissionInterface;
import com.proxy.util.SPreferencesUtil;
import com.proxy.util.TimeUtils;
import com.proxy.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WebSplashActivity extends Activity  implements PermissionInterface {


	//传世H5
	private String m_platform = "android";
	private String m_appKey = "XovHFMSPKstE0Gwrf8cBudDIV6JN5hC4";
	private String m_gameId = "cqsjh5";
	private String m_gameName = "cqsjh5";
	private LoadingFramelayout mLoadingFramelayout;
	public String HtmlUrl;
	private Activity mativity;
	private PermissionHelper mPermissionHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mativity = this;
		mLoadingFramelayout = new LoadingFramelayout(this, R.layout.mcpr_splash);
		//初始化并发起权限申请
		mPermissionHelper = new PermissionHelper(WebSplashActivity.this,this);
		mPermissionHelper.requestPermissions();
		setContentView(mLoadingFramelayout);
		Data.getInstance().setGameActivity(mativity);
		calculateStartTime();
	}


	private void getHtmlUrl() {
		final String is_first = (String) SPreferencesUtil.get(
				mativity, "is_first", "1"); // 获取到保存的key数据
		String result = Util.getAssetsFileContent(mativity,
				"SDKFile/adChannel.png");
		final String channel = Util.getJsonStringByName(result, "channel");
		final String adchannel = Util.getJsonStringByName(result, "adChannel");
		Map<String, Object> data = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());// 设置日期格式
		String time = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
		if (!"1".equals(is_first)) {
			Map<String, String> json = Util.readHttpData(mativity);
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

		HttpService.doHtmlUrl(mativity, data, new HttpRequestUtil.DataCallBack() {
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
									.put(mativity, "is_first", "0");// 保存sp标识
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

						Intent intent = new Intent(WebSplashActivity.this, WebMainActivity.class);
						intent.putExtra("HTML_URL",HtmlUrl);
						//int rabdoms = Rabdoms();
						//intent.putExtra("HTML_URL","http://111.230.145.106/game.html?r="+rabdoms);
						startActivity(intent);
						finish();

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

	// 配置不同平台号
	private String platformData() {
		String result = Util.getAssetsFileContent(mativity,
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



	private void init(){
		getHtmlUrl();
	}


	@Override
	protected void onRestart() {
		super.onRestart();
		mPermissionHelper.requestPermissions();
		LogUtil.log("==========onRestart()===============");
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.log("==========onResume()===============");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}


	@TargetApi(23)
	@Override
	public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
		if(mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){
			//权限请求结果，并已经处理了该回调
			return;
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public int getPermissionsRequestCode() {
		//设置权限请求requestCode，只有不跟onRequestPermissionsResult方法中的其他请求码冲突即可
		return 10000;
	}

	@Override
	public String[] getPermissions() {
		//设置该界面所需的全部权限
		return new String[]{
				android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
				android.Manifest.permission.READ_EXTERNAL_STORAGE,
				android.Manifest.permission.READ_PHONE_STATE,
				android.Manifest.permission.CALL_PHONE
		};
	}

	@Override
	public void requestPermissionsSuccess() {
		//权限请求用户已经全部允许
		LogUtil.log("权限请求用户已经全部允许");
		init();
	}

	@Override
	public void requestPermissionsFail() {
		//权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
		Toast.makeText(WebSplashActivity.this,"请开启权限，重新进入游戏！",Toast.LENGTH_SHORT).show();
		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package",getApplication().getPackageName(),null);
		intent.setData(uri);
		startActivity(intent);
	}


	private void calculateStartTime() {
		long coldStartTime = TimeUtils.getTimeCalculate(TimeUtils.COLD_START);
		// 这里记录的TimeUtils.coldStartTime是指Application启动的时间，最终的冷启动时间等于Application启动时间+热启动时间
		TimeUtils.sColdStartTime = coldStartTime > 0 ? coldStartTime : 0;
		TimeUtils.beginTimeCalculate("Splash");
		LogUtil.log("=======Application启动时间==============="+coldStartTime);
	}

	private  int Rabdoms(){
		int num = (int) ((Math.random() * 9 + 1) * 10000000);
		return num;
	}


}
