package com.mc.h5game.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.game.sdkproxy.R;
import com.mc.h5game.util.SPreferencesUtil;
import com.mc.h5game.util.ThreadPoolUtil;
import com.mc.h5game.util.TimeUtils;
import com.proxy.Data;
import com.proxy.service.HttpService;
import com.proxy.tools.HttpRequestUtil;
import com.proxy.util.LoadingFramelayout;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SplashHtmlActivity extends AppCompatActivity {


	//传世H5
	private String m_platform = "android";
	private String m_appKey = "XovHFMSPKstE0Gwrf8cBudDIV6JN5hC4";
	private String m_gameId = "cqsjh5";
	private String m_gameName = "cqsjh5";
	private LoadingFramelayout mLoadingFramelayout;
	public String HtmlUrl;
	private Activity mativity;
	private ImageView   mView = null ;
	private InputStream  splashFile = null ;
	private AssetManager  	mAssets  = null ;
	private static int        mImageCnt = 0 ;
	private String[]   		mImages = null ;
	private static int mCount = 0;
	private static ArrayList<String> mImagesForSplashList ;
	private List<ImageView> images;
	RxPermissions rxPermissions = new RxPermissions(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mLoadingFramelayout = new LoadingFramelayout(this, R.layout.pmc_splash);
		setContentView(mLoadingFramelayout);
		calculateStartTime();
		mativity = this;
		//setContentView(R.layout.pmc_splash);
		Data.getInstance().setGameActivity(mativity);
        InitPermissions();
	}

	private void InitPermissions(){

        rxPermissions.request(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.CALL_PHONE
                )
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            init();
                            LogUtil.log("同意权限请求了");
                        } else {
                            Toast.makeText(SplashHtmlActivity.this,"请开启权限，重新进入游戏！",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",getApplication().getPackageName(),null);
                            intent.setData(uri);
                            startActivity(intent);

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
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

		LogUtil.log("开始请求H5游戏地址：");
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

						//Intent intent = new Intent(SplashHtmlActivity.this, WebMainHtmlTestActivity.class);
						Intent intent = new Intent(SplashHtmlActivity.this, WebMainHtmlHActivity.class);
						intent.putExtra("HTML_URL",HtmlUrl);
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

		/*new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
					Intent intent = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(intent);
					SplashActivity.this.finish();
			}
		},1000);*/
        LogUtil.log("开始跳转Main加载页面");

		getHtmlUrl();

       /* Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();*/

	/*	Intent intent = new Intent(SplashHtmlActivity.this, WebMainActivity.class);
		startActivity(intent);
		SplashHtmlActivity.this.finish();*/



		/*mView = new ImageView(this);
		mView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		mAssets = getAssets();
		mImagesForSplashList = new ArrayList<String>();
		try {
			mImages = mAssets.list("Splash");
			for( int i = 0 ; i<mImages.length;i++){
				if( mImages[i].endsWith(".jpg") ){
					mImagesForSplashList.add(mImages[i]);
					mImageCnt++;
				}
			}
			Collections.sort(mImagesForSplashList);
			if(mImagesForSplashList.size() >0){
				showSplash();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}


	@Override
	protected void onRestart() {
		super.onRestart();
		LogUtil.log("==========onRestart()===============");
		InitPermissions();
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.log("==========onResume()===============");
	}




	private void showSplash(){
		try {
			splashFile   =  mAssets.open("Splash/"+mImagesForSplashList.get(mCount));
		} catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mView.setImageBitmap(BitmapFactory.decodeStream(splashFile));
		mView.setScaleType(ImageView.ScaleType.FIT_XY);
		LayoutInflater inflater = LayoutInflater.from(this);
		final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.pmc_splash, null);
		FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		addContentView(layout, params2);
		layout.addView(mView);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				mCount++;
				if (mCount ==mImageCnt) {

					Intent intent = new Intent(SplashHtmlActivity.this, MainActivity.class);
					//Intent intent = new Intent(SplashActivity.this, H5MainActivity.class);
					startActivity(intent);
					SplashHtmlActivity.this.finish();
				} else {
					layout.removeView(mView);
					showSplash();
				}
			}
		},1000);
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		System.out.println("   onBackPressed()");
	}


	private void calculateStartTime() {
		long coldStartTime = TimeUtils.getTimeCalculate(TimeUtils.COLD_START);
		// 这里记录的TimeUtils.coldStartTime是指Application启动的时间，最终的冷启动时间等于Application启动时间+热启动时间
		TimeUtils.sColdStartTime = coldStartTime > 0 ? coldStartTime : 0;
		TimeUtils.beginTimeCalculate("Splash");
		LogUtil.log("=======Application启动时间==============="+coldStartTime);
	}


}
