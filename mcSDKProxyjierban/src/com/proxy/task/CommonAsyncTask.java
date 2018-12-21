package com.proxy.task;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.proxy.ResultCode;
import com.proxy.bean.Result;
import com.proxy.listener.BaseListener;
import com.proxy.util.HttpUtil;
import com.proxy.util.LoadingDialog;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;

public class CommonAsyncTask extends AsyncTask<Map<String, String>, Void, Void> {
	private String postUrl;
	private Activity activity;
	private BaseListener listener;

	public CommonAsyncTask(Activity activity, String postUrl) {
		this.activity = activity;
		this.postUrl = postUrl;
	}

	public CommonAsyncTask(Activity activity, String postUrl, BaseListener listener) {
		this.activity = activity;
		this.postUrl = postUrl;
		this.listener = listener;
	}

	@Override
	protected Void doInBackground(Map<String, String>[] params) {
		LogUtil.e("postUrl : " + this.postUrl+ "CommonAsyncTask params = " + params[0]);
		
		for (int i = 0; i < 3; i++) {
			try {
				String result = HttpUtil.doHttpPost(params[0], this.postUrl);
				LogUtil.e( "CommonAsyncTask result = " + result );
				if (result == null) {
					Thread.sleep(500L);
					if (i == 2) {
						LogUtil.e("请检查网络是否连接");
						excuteCallback(ResultCode.FAIL , new Result(ResultCode.NET_DISCONNET, "请检查网络是否连接").toString() );
						break;
					}
				}
				else{
					JSONObject jsonObject = new JSONObject(result);
					int resultCode = jsonObject.getInt("code");
	
					switch (resultCode) {
					case ResultCode.SUCCESS:
						excuteCallback(ResultCode.SUCCESS, result);
						break;
	
					default:
						excuteCallback(ResultCode.FAIL , result);
						break;
					}
					break;
				}

			} catch (Exception e) {
				e.printStackTrace();

			/*	if (i == 2)
				{
					excuteCallback(ResultCode.UNKNOW , new  Result(ResultCode.UNKNOW, "unknow").toString());
				}*/

			}
		}

		return null;
	}


	public void call(final int type , final String result){
		JSONObject  errJson = null ;
		switch (type) {
			case ResultCode.SUCCESS:
				this.listener.onSuccess(result);
				break;
				
			case ResultCode.FAIL:
				this.listener.onFail(result);
				
				if (activity==null){
					
				}else{
					if(!TextUtils.isEmpty(result))
						try {
							errJson = new JSONObject(result);
							String  code = "";
							String  msg  = "";
							code  = errJson.get("code").toString();
							msg   = errJson.get("reason").toString();
							if(!TextUtils.isEmpty(code)&&!TextUtils.isEmpty(msg)){
								String tips = msg;
								//Util.ShowTips(com.mc.h5game.activity,tips);
							}else{
								Util.ShowTips(activity,result.toString() );
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
//						KnUtil.ShowTips(com.mc.h5game.activity,result.toString() );
				}
				break;
				
			case ResultCode.UNKNOW:
				this.listener.onFail(result);
				break;
			default:
				break;
		}
	}
	
	public void excuteCallback(final int type , final String result){
		
		if (activity==null) {
			call(type, result);
		}else{
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					call(type, result);
				}
			});
		}
	}
}