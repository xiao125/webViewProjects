package com.proxy.activity;

import com.game.sdkproxy.R;
import com.proxy.sdk.channel.SdkChannel;
import com.proxy.util.LogUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class PaymentActivity extends Activity{
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {//横屏  
	        //你要执行的操作，可以不写  
			setContentView(R.layout.pmc_pay);
			LogUtil.e("onConfigurationChanged++");
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	   }
	}
	private static Activity mActivity = null ;
	public Button buttonwx = null;
	public Button buttonqt = null;
	public Button backqt = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
				super.onCreate(savedInstanceState);
				
				mActivity = this ;
				requestWindowFeature(Window.FEATURE_NO_TITLE);
				setContentView(R.layout.pmc_pay);
				buttonwx = (Button)findViewById(R.id.weixin);
				buttonqt = (Button)findViewById(R.id.qita);
				backqt = (Button)findViewById(R.id.back);
				buttonwx.setOnClickListener(SdkChannel.getInstance().onclickwx);
				buttonqt.setOnClickListener(SdkChannel.getInstance().onclickqt);
				backqt.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mActivity.finish();
						mActivity=null;
						}
				});
				
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		return super.onKeyUp(keyCode, event);
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
		LogUtil.e("onResume++");
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
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
