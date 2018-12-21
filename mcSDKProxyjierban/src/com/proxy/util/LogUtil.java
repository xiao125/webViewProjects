package com.proxy.util;

import android.util.Log;

public class LogUtil {

	public static boolean mLogEnable = true;
	
	public static void setLogEnable( boolean flag ){
		
		mLogEnable = flag ; 
		
	}

	public static void e(String message) {
		if(mLogEnable){
			Log.e("SDKProxy", message);
		}
		
	}

	public static void i(String message) {
		if (mLogEnable){
			Log.i("SDKProxy", message);
		}
	}

	public static void w(String message) {
		if (mLogEnable){
			Log.w("SDKProxy", message);
		}
	}

	public static void d(String message) {
		if (mLogEnable){
			Log.d("SDKProxy", message);
		}
	}
	
	public static void log( String message ){
		LogUtil.e(message);
	}

}
