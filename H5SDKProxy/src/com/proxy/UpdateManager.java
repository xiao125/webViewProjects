package com.proxy;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.game.sdkproxy.R;
import com.proxy.util.LogUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


public class UpdateManager {
	
	private Activity  mContext ;
	private String   mUpdateMsg = "游戏版本更新" ;
	private String   mApkUrl = "" ; 
	private Dialog   mNoticeDialog ;
	private Dialog   mUpdateDialog ;
	private static   final String mSavePath = "/sdcard/updateApk/";
	private static   final String mSaveFileName = mSavePath+"ghz.apk";
	
	private ProgressBar   mProgressBar;
	private static final int DOWNUPDATE = 1 ;
	private static final int DOWNOK     = 0 ;
	private int mProgress;
	private Thread mDownLoadThread; 
	private boolean mInterceptFlag = false;
	private String  mState ;
	
	public void setUpdateMsg( String msg ){
		this.mUpdateMsg = msg ;
	}
	
	public void setApkUrl( String url ){
		
		LogUtil.e("url="+url);
		String head = url.substring(0,7);
		if(head.equals("http://")){
		
		}else{
			
			url = "http://"+url;
			this.mApkUrl = url ;
			
		}
		
		LogUtil.e("url="+this.mApkUrl);
		
	}
	
	public void setState( String state ){
		this.mState = state ;
	}

	
	
	private Handler mHandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
		   switch (msg.what) {
		   case DOWNUPDATE:
			   mProgressBar.setProgress(mProgress);
		    break;
		   case DOWNOK:
			   installApk();
		    break;
		   }
		   super.handleMessage(msg);
		  }
	 };
	 
	 public void installApk(){
		 
		 File apkfile = new File(mSaveFileName);
		  if (!apkfile.exists()) {
		   return;
		  }
		  Intent i = new Intent(Intent.ACTION_VIEW);
		  i.setDataAndType(Uri.parse("file://" + apkfile.toString()),"application/vnd.android.package-archive");
		  mContext.startActivity(i);
	 }
	 
	 public UpdateManager(Activity context) {
		  this.mContext = context;
	 }
	 
	 public void checkUpdateInfo() {
		  showNoticeDialog();
	}
	 
	 private void showNoticeDialog() {
		  android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
		  builder.setTitle("游戏版本更新");
		  builder.setMessage(mUpdateMsg);
		  builder.setPositiveButton("立即下载", new OnClickListener() {
		   @Override
		   public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		    showDownloadDialog();
		   }
		  });
		  
		  if(mState.equals("0")){
			  
			  builder.setNegativeButton("以后再说", new OnClickListener() {
				   @Override
				   public void onClick(DialogInterface dialog, int which) {
				    dialog.dismiss();
				    /*
				    if(KnSplash.getView()!=null){
				    	((ViewGroup)KnSplash.getView().getParent()).removeView(KnSplash.getView());
				    }
				    */
				    /*  
				    if(KnSplash.getSplashListener()!=null){
				    	KnSplash.getSplashListener().onSuccess(null);
				    }
				    */
				    Splash.getInstance(mContext).shouYouWeb();
				    
				   }
			});
			  
		  }
		 
		  mNoticeDialog = builder.create();
		  mNoticeDialog.show();
		 }
	 
	 protected void showDownloadDialog() {
		 
		  android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
		  builder.setTitle("游戏版本更新");
		  final LayoutInflater inflater = LayoutInflater.from(mContext);
		  View v = inflater.inflate(R.layout.pmc_progress, null);

		  mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
		  builder.setView(v);
		  builder.setNegativeButton("取消", new OnClickListener() {
		   @Override
		   public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		    
		    mContext.finish();
		    mInterceptFlag = true;
		    
		    
		   }
		  });
		  mUpdateDialog = builder.create();
		  mUpdateDialog.show();
		  downloadApk();
 }
	 
	 private void downloadApk() {
		 
		 mDownLoadThread = new Thread(mDownApkRunnable);
		 mDownLoadThread.start();
		 
	}
	 
	 private Runnable mDownApkRunnable = new Runnable() {
		  @Override
		  public void run() {
			  
			   URL url;
			   try {
				   
					LogUtil.e("mApkUrl="+mApkUrl);
				    url = new URL(mApkUrl);
				    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				    conn.connect();
				    int length = conn.getContentLength();
				    InputStream ins = conn.getInputStream();
				    LogUtil.e("mSavePath="+mSavePath);
				    File file = new File(mSavePath);
				    if (!file.exists()) {
				    	LogUtil.e("mkdir");
				    	file.mkdir();
				    }
				    String apkFile = mSaveFileName;
				    File ApkFile = new File(apkFile);
				    FileOutputStream outStream = new FileOutputStream(ApkFile);
				    int count = 0;
				    byte buf[] = new byte[1024];
				    do {
					     int numread = ins.read(buf);
					     LogUtil.e("下载中==numread="+numread);
					     count += numread;
					     mProgress = (int) (((float) count / length) * 100);
					     mHandler.sendEmptyMessage(DOWNUPDATE);
					     if (numread <= 0) {
					      mHandler.sendEmptyMessage(DOWNOK);
					      break;
					     }
					     outStream.write(buf, 0, numread);
				    } while (!mInterceptFlag);
				    outStream.close();
				    ins.close();
				   } catch (Exception e) {
				    e.printStackTrace();
				   }
			  }
			  
			 
		 };
		
}
