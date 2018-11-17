package com.game.sdk.util;

import android.util.Log;

public class KnLog {

	public static boolean mLogEnable = true;
	
	public static final String TAG = "KNGAMESDK";
	
	public static void setLogEnable( boolean flag ){
		mLogEnable = flag ; 
	}
	
	public static void e(String message) {
		if(mLogEnable){
			Log.e(TAG, message);
		}
		
	}

	public static void i(String message) {
		if (mLogEnable){
			Log.i(TAG, message);
		}

	}

	public static void w(String message) {
		if (mLogEnable){
			Log.w(TAG, message);
		}

	}

	public static void d(String message) {
		if (mLogEnable){
			Log.d(TAG, message);
		}
	}
	
	public static void log( String message ){
		if (mLogEnable){
			Log.d(TAG, message);
		}

	}

}
