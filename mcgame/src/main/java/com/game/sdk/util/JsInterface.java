package com.game.sdk.util;

import android.os.Handler;
//import android.util.Log;
import android.webkit.JavascriptInterface;

public class JsInterface {
	
	Handler mHandler = new Handler();

	
	public interface wvClientClickListener {  
		 public void wvHasClickEnvent(String title, String content, String imageUrl, String url);
		 public void wvCloseWebEvent();
		 public void wvWxWebPayEvent();

	}  
		   
	private wvClientClickListener wvEnventPro = null;

	public void setWvClientClickListener(wvClientClickListener listener) {  
		  
		  wvEnventPro = listener;  
	}  
	
	
	//
	@JavascriptInterface  
	public void javaFunction( final String title , final String content , final String imageUrl, final String url ) {  		
		if(wvEnventPro != null){
			 KnLog.e("javaFunction");
			 wvEnventPro.wvHasClickEnvent(title,content,imageUrl,url);
		}
	} 
	

     
	@JavascriptInterface
	public void openWXWebFunction(){
		KnLog.log("---openWXWeb web now---");
		if(wvEnventPro!=null){
			KnLog.e("openWXWeb");
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					wvEnventPro.wvWxWebPayEvent();
				}
			});
		
		}
	}

	
	//支付界面关闭
	@JavascriptInterface  
	public void closeWebFunction(){
		KnLog.e("---close web now---");
		if(wvEnventPro!=null){
			KnLog.e("closeWebFunction");
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					wvEnventPro.wvCloseWebEvent();
				}
			});
		
		}
		
	}
	

}
