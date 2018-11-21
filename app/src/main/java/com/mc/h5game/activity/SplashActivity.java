package com.mc.h5game.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.game.sdkproxy.R;
import com.mc.h5game.util.PermissionHelper;
import com.mc.h5game.util.PermissionInterface;
import com.proxy.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SplashActivity extends Activity implements PermissionInterface {

	private ImageView   mView = null ;
	private InputStream  splashFile = null ;
	private AssetManager  	mAssets  = null ;
	private static int        mImageCnt = 0 ;
	private String[]   		mImages = null ;
	private static int mCount = 0;
	private static ArrayList<String> mImagesForSplashList ;
	private List<ImageView> images;
	private PermissionHelper mPermissionHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//初始化并发起权限申请
		mPermissionHelper = new PermissionHelper(SplashActivity.this,this);
		mPermissionHelper.requestPermissions();
		setContentView(R.layout.pmc_splash);


	}

	private void init(){

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
					Intent intent = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(intent);
					SplashActivity.this.finish();
			}
		},2000);


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
				android.Manifest.permission.READ_PHONE_STATE,
				android.Manifest.permission.CALL_PHONE,
				android.Manifest.permission.SEND_SMS,
				android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
				android.Manifest.permission.READ_EXTERNAL_STORAGE
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
		//Toast.makeText(this,"请重新开启权限！",Toast.LENGTH_SHORT).show();
		mPermissionHelper.requestPermissions();

	}

}
