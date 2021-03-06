package com.mc.h5game.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class SplashActivity extends AppCompatActivity {

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
		setContentView(R.layout.pmc_splash);
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
                            Toast.makeText(SplashActivity.this,"请开启权限，重新进入游戏！",Toast.LENGTH_SHORT).show();
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
       /* Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();*/

		Intent intent = new Intent(SplashActivity.this, WebMainActivity.class);
		startActivity(intent);
		SplashActivity.this.finish();



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

					Intent intent = new Intent(SplashActivity.this, MainActivity.class);
					//Intent intent = new Intent(SplashActivity.this, H5MainActivity.class);
					startActivity(intent);
					SplashActivity.this.finish();
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
}
