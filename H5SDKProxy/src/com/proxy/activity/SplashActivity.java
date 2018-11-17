package com.proxy.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.game.sdkproxy.R;
import com.proxy.bean.User;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SplashActivity extends Activity {
	
	private ImageView   mView = null ;
	private InputStream  splashFile = null ;
	private AssetManager  	mAssets  = null ; 
	private static int        mImageCnt = 0 ;
	private String[]   		mImages = null ;
	private static int mCount = 0;
	private static ArrayList<String> mImagesForSplashList ;
	private List<ImageView> images;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mView = new ImageView(this);
		mView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		mAssets = getAssets();
		mImagesForSplashList = new ArrayList<String>();
		try {
			mImages = mAssets.list("SDKFile");
			for( int i = 0 ; i<mImages.length;i++){
				if( mImages[i].endsWith(".jpg") ){
					mImagesForSplashList.add(mImages[i]);
					mImageCnt++;
				}
			}
			LogUtil.e("mImageCnt = "+mImageCnt +" mImagesForSplashList ="+mImagesForSplashList.size());
			Collections.sort(mImagesForSplashList);
			if(mImagesForSplashList.size() >0){
				showSplash();
			}else {
				SplashActivity.this.finish();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void showSplash(){
		try {
			LogUtil.e("file："+mImagesForSplashList.get(mCount));
			splashFile   =  mAssets.open("SDKFile/"+mImagesForSplashList.get(mCount));
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
			//	LogUtil.log("第几张图片"+mImagesForSplashList.get(mCount)+mCount+"  mImageCnt="+mImageCnt);
				if (mCount ==mImageCnt) {
					// 继续做版本更新工作
					SplashActivity.this.finish();
				} else {
					layout.removeView(mView);
					showSplash();
				}				
			}
		},2000);
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
		System.out.println("按下了back键   onBackPressed()");
	}
}
