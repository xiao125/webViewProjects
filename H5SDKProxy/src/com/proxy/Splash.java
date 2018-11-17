package com.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;
import com.game.sdkproxy.R;
import com.proxy.activity.SplashActivity;
import com.proxy.listener.BaseListener;
import com.proxy.listener.SplashListener;
import com.proxy.task.CommonAsyncTaskTimeOut;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;
import com.proxy.util.Md5Util;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 
 * @author Administrator
 *  开机动画/闪屏设置
 */
public class Splash {
	
	private static Activity mActivity = null ;
	private ImageView   mViewAni = null ;
	private static View        mBaseView = null ;
	private AssetManager  	mAssets  = null ; 
	private String[]   		mImages = null ;
	private static int        mImageCnt = 0 ;
	private static int        mCount  = 0 ;
	private AnimationDrawable animationDrawable;  
	private static ArrayList<String> mImagesForSplashList ;  
	private static SplashListener  mSplashLister = null ;
	private PopupWindow mPopupWindow;
	private TextView    mTvshowText;
	private Dialog      mShouyouDialog ;

	private static String msg ;
	private static String state ;
	private static String url ;
	private static String code="1" ;
	private static String simulator="0" ; 
	private static String simulatorUrl  ;
	
	public static String getSimulator(){
		return simulator;
	}
	
	public static String getSimulatorUrl(){
		return simulatorUrl;
		
	}
	
	public static View getView(){
		return mBaseView;
	}
	
	public static SplashListener getSplashListener(){
		return mSplashLister ; 
		
	}
	
	
	public static void setSplash( final  Activity activity ){
		mActivity = activity ;
	}
	
	public static Splash getInstance( final  Activity activity ){
		mImageCnt = 0 ;
		mCount  = 0 ;
		mImagesForSplashList = new ArrayList<String>();
		setSplash( activity );
		return new Splash();
		
	}
	
	/*	flag = true 先游戏的闪屏再执行sdk的闪屏  flag = false 先sdk的闪屏再执行游戏的闪屏
	 * *
	 */
	
	public void splashSDK( SplashListener  SplashLister ){
		mSplashLister = SplashLister ;
		upDateSdk();
		mAssets = mActivity.getAssets();
		try {
			mImages = mAssets.list("SDKFile");
			for( int i = 0 ; i<mImages.length;i++){
				if( mImages[i].endsWith(".jpg") ){	
					mImagesForSplashList.add(mImages[i]);
					mImageCnt++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			animalAction();
		}
		//排序
		Collections.sort(mImagesForSplashList);
		splashforNSDK();
	}

	public void animalAction(){
		showUpdateViewHandler();

	}
	
	public void splashGame( SplashListener  SplashLister ){
		
		mSplashLister = SplashLister ;
		upDateSdk();
		mBaseView = new View(mActivity);
		mBaseView.setBackgroundColor(Color.rgb(57,57, 57));
		mActivity.addContentView(mBaseView,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		mAssets = mActivity.getAssets();
		try {
			mImages = mAssets.list("SDKFlie");
			
			for( int i = 0 ; i<mImages.length;i++){
				
				if( mImages[i].endsWith(".jpg") ){
					mImagesForSplashList.add(mImages[i]);	
					mImageCnt++;
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showUpdateViewHandler();
			
		}
		
		
		mViewAni = new ImageView(mActivity);
		 
		mViewAni.setScaleType(ScaleType.CENTER);
		
		mActivity.addContentView(mViewAni,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        
		animationDrawable = (AnimationDrawable) mViewAni.getDrawable();  
        animationDrawable.start(); 
        int duration = 0; 
        for(int i=0;i<animationDrawable.getNumberOfFrames();i++){ 
        	duration += animationDrawable.getDuration(i); 
        } 
        
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() { 

              public void run() {
            	  
            	 
            	  ((ViewGroup)mViewAni.getParent()).removeView(mViewAni);
            	  
            	  splashforN();   	  
                  //此处调用第二个动画播放方法  	     	  
            	  
              } 

           }, duration); 
		
		//再显示启动页	
		
	}
	//启动动画的先后顺序
	public void splash( boolean flag , SplashListener  SplashLister  ){
		if(true==flag){
			LogUtil.e("game splash");
			splashGame(SplashLister);
		}else{
			LogUtil.e("sdk splash");
			splashSDK(SplashLister);
		}
		
	}
	

	public void splashforNSDK() {
		final ImageView mView = new ImageView(mActivity);
		//处理在模拟器中闪屏影响调用YSDK接口报错问题
		if (Util.getChannle(mActivity).equals("yyb")) {
			//YSDKApi.onCreate(mActivity);
		}
		String fileName = "";
		if (mImagesForSplashList.size() > 0) {
			if(Util.getChannle(mActivity).equals("baidu")){
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						LogUtil.e("baidu++");
						Intent intent = new Intent();
						intent.setClass(mActivity, SplashActivity.class);
						mActivity.startActivity(intent);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								LogUtil.e("postDelayed");
								mCount++;
								animalAction();
							}
						}, 3000 * mImageCnt); // 启动动画持续3秒钟
					}
					
				},2000);
			}else{
				Intent intent = new Intent();
				intent.setClass(mActivity, SplashActivity.class);
				mActivity.startActivity(intent);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						LogUtil.e("postDelayed");
						mCount++;
						animalAction();
					}
				}, 3000 * mImageCnt); // 启动动画持续3秒钟
			}
		} else {
			animalAction();
		}
	}
	


	
	
	public void splashforN(){

		final ImageView mView = new ImageView(mActivity);
		String fileName = "";
		if(mImagesForSplashList.size()>0){
			fileName = "splash/"+mImagesForSplashList.get(mCount);
			InputStream splashFile = null ;
			if(Util.fileExits(mActivity, fileName)){
				try {
					splashFile   =  mAssets.open(fileName);
					mView.setImageBitmap(BitmapFactory.decodeStream(splashFile));
		     		mView.setScaleType(ImageView.ScaleType.FIT_XY);
		     		mActivity.addContentView(mView,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		            Animation anim = new Animation() { };
		            anim.setDuration(1000);
		            anim.setRepeatCount(1);
		            mView.startAnimation(anim);
		            anim.setAnimationListener( new AnimationListener() {
		     			
		     			@Override
		     			public void onAnimationStart(Animation arg0) {
		     				// TODO Auto-generated method stub
		     			}
		     			
		     			@Override
		     			public void onAnimationRepeat(Animation arg0) {
		     				// TODO Auto-generated method stub
		     				
		     			}
		     			
		     			@Override
		     			public void onAnimationEnd(Animation arg0) {
		     				// TODO Auto-generated method stub
		     				
		     				((ViewGroup)mView.getParent()).removeView(mView);
		     				
		     				mCount ++ ;
		     				if (mCount ==  mImageCnt ){
		     					
		     					//		继续做版本更新工作
		     					showUpdateViewHandler();
		     					
		     				}
		     				else{
		     					
		     					splashforN();
		     					
		     				}
		     			}
		     		} );
		     			
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					showUpdateViewHandler();
					
				}
				
			}else{
				showUpdateViewHandler();
			}
		}else{
			
			showUpdateViewHandler();
			
		}
		
	}

	public void upDateSdk(){
	
		try {
			
			PackageInfo pi=mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
			String  versionName = pi.versionName;
			String  gameName = Data.getInstance().getGameInfo().getGameId();
			String  channel = Util.getChannle(mActivity);
			String  platForm = Data.getInstance().getGameInfo().getPlatform();
			
			Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

				@Override
				public int compare(String arg0, String arg1) {
					// TODO Auto-generated method stub
					return arg0.compareTo(arg1);
				}
			} );
			
			String app_id     = "1005";
			String app_secret = "5e7f30b128c6bcdf04ab5c042154e1d0";

			update_params.put("version", versionName);
			update_params.put("game", gameName);
			update_params.put("channel", channel);
			update_params.put("platform", platForm);
			update_params.put("m", "knsdk");
			update_params.put("a", "click");
			update_params.put("app_id",app_id);
			
			String sign = "";
			
			Map<String, String> update_paramsTmp = new TreeMap<String, String>( new Comparator<String>() {

				public int compare(String arg0, String arg1) {
					// TODO Auto-generated method stub
					return arg0.compareTo(arg1);
				}


			} );
			
			
			for(Map.Entry<String, String> entry:update_params.entrySet()){    
				
				update_paramsTmp.put(entry.getKey(), entry.getValue());
				
			}   
			
			int    len = update_paramsTmp.size();
			int    count = 0;
			
			for(Map.Entry<String, String> entry:update_paramsTmp.entrySet()){
				
				count++;
				if(count==len){
					sign = sign+entry.getKey()+"="+entry.getValue();
				}else{
					sign = sign+entry.getKey()+"="+entry.getValue()+"&";
				}
				
			} 
			
			sign = sign+"&app_secret="+app_secret;
			update_params.put("sign",Md5Util.getMd5(sign).toLowerCase());
	
			new CommonAsyncTaskTimeOut(null,Constants.URL.SDKUPDATEURL, 2000 ,  new BaseListener() {
				
				@Override
				public void onSuccess(Object result) {
					// TODO Auto-generated method stub
					//获取返回的值来说明中间件是否需要热更新
					try {
						
						JSONObject   getJson = new JSONObject(result.toString());
									 
						String       version = getJson.getString("version");
						
						JSONObject   versionJson = new JSONObject(version);
						code = versionJson.getString("code");
						
						String       simulatorstr = getJson.getString("simulator");
						JSONObject   simulatorJson = new JSONObject(simulatorstr);
						
						
						simulator = simulatorJson.getString("state");
						simulatorUrl = simulatorJson.getString("url");
						
						if(code.equals("0")){
							
							//更新对话框
							msg    = versionJson.getString("msg");
							state  = versionJson.getString("state");
							url    = versionJson.getString("url");
		
						}else{
							//跳过
							LogUtil.e("无版本更新");
							code="1";
							
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						code="1";
					}
		
				}
				
				@Override
				public void onFail(Object result) {
					// TODO Auto-generated method stub
					LogUtil.e("ret:"+result.toString());
					code="1";
					
				}
			}).execute(new Map[] { update_params , null, null });
		
	} catch (NameNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
			
}
	public void showUpdateViewHandler(){
		LogUtil.e("code="+code);
		if(code.equals("0")){
			showUpdateView(msg,state);
		}else{
			shouYouWeb();
		}
	}
	
	public void showUpdateView( String msg , String state ){
		UpdateManager mUpdateManger = new UpdateManager(mActivity);// 注意此处不能传入getApplicationContext();会报错，因为只有是一个Activity才可以添加窗体
		mUpdateManger.setApkUrl(url);
		mUpdateManger.setUpdateMsg(msg);
		mUpdateManger.setState(state);
		mUpdateManger.checkUpdateInfo();
	}

	public void shouYouWeb(){

		if(simulator.equals("1")){

			//检测模拟器
			LogUtil.e("检测模拟器");
			//手游岛模拟器?
			LogUtil.e(" mSplashLister.onSuccess 111111");
			mSplashLister.onSuccess(null);

		}else{
			//不检测模拟器
			if(mBaseView!=null){
				((ViewGroup)mBaseView.getParent()).removeView(mBaseView);
				mBaseView = null ;
			}
			LogUtil.e(" mSplashLister.onSuccess 33333333");
			mSplashLister.onSuccess(null);
		}
	}

	public void shouYouWebView(){
		DisplayMetrics dm = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		final RelativeLayout rl = new RelativeLayout(mActivity);
		RelativeLayout.LayoutParams relLayoutParams=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		RelativeLayout rl1 = new RelativeLayout(mActivity);
		RelativeLayout.LayoutParams relLayoutParams1=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		relLayoutParams1.setMargins(200, 100, 200, 100);
		View        mDialogView = null ;
		mDialogView = new View(mActivity);
		mDialogView.setBackgroundColor(Color.rgb(255,255,255));
		mDialogView.setLayoutParams(new LayoutParams((int)(width*0.6),(int)(height*0.9)));
		rl1.addView(mDialogView);
		rl1.setLayoutParams(relLayoutParams1);
		rl.addView(rl1);
		RelativeLayout rl2 = new RelativeLayout(mActivity);
		TextView   tView = new TextView(mActivity);
		String gameName = Data.getInstance().getGameInfo().getGameName();
		LogUtil.e("gameName = "+ gameName);
		tView.setText(gameName+"游戏需要使用手游岛模拟器,请下载");
		tView.setTextColor(Color.rgb(57,57, 57));
		tView.setLayoutParams( new LayoutParams((int)(height*0.9),40));
		rl2.addView(tView);
		RelativeLayout.LayoutParams relLayoutParams2=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		relLayoutParams2.setMargins(250,200,0,200);
		rl2.setLayoutParams(relLayoutParams2);
		rl.addView(rl2);
		//添加按钮
		RelativeLayout rl4 = new RelativeLayout(mActivity);
		Button  updateBtn = new Button(mActivity);
		updateBtn.setText("点击访问手游岛");
		updateBtn.setTextSize(14);
		updateBtn.setTextColor(Color.rgb(57,57, 57));
		updateBtn.setLayoutParams(new LayoutParams(300,50));
		updateBtn.setPadding(0,0,0,0);
		updateBtn.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LogUtil.e("点击访问手游岛");
				LogUtil.e("shouyouUrl"+simulatorUrl);	
				Intent intent = new Intent(Intent.ACTION_VIEW);  
		        Uri data = Uri.parse(Html.fromHtml(simulatorUrl).toString());  
		        intent.setData(data);  
		        intent.setPackage("com.google.android.browser");  
		        intent.addCategory("android.intent.category.BROWSABLE");  
		        intent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));  
		        mActivity.startActivity(intent);  
		        mActivity.finish();
				
			}
		} );
		
		rl4.addView(updateBtn);
		
		RelativeLayout.LayoutParams relLayoutParams4=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		relLayoutParams4.setMargins(400,350,0,0);
		rl4.setLayoutParams(relLayoutParams4);
		rl.addView(rl4);
		
		//	添加稍后再说
		
		RelativeLayout rl3 = new RelativeLayout(mActivity);
		
		TextView   tView1 = new TextView(mActivity);
		tView1.setText("稍后再说");
		tView1.setTextColor(Color.rgb(57,57, 57));
		tView1.setLayoutParams( new LayoutParams((int)(height*0.9),40));
		
		tView1.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.e("KNSDKProxy","稍后再说");
	
				 if(mBaseView!=null){
	        		 ((ViewGroup)mBaseView.getParent()).removeView(mBaseView);
		        	 mBaseView = null ;
	        	 }
				
				LogUtil.e("removeView-rl");
				((ViewGroup)rl.getParent()).removeView(rl);
				LogUtil.e(" mSplashLister.onSuccess 4444444444");
				mSplashLister.onSuccess(null);
				
				
			
			}
		} );
		
		rl3.addView(tView1);
		
		RelativeLayout.LayoutParams relLayoutParams3=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		relLayoutParams3.setMargins(700,400,0,200);
		rl3.setLayoutParams(relLayoutParams3);
		rl.addView(rl3);
		
		mActivity.addContentView(rl, relLayoutParams);
		
	}
	
	public void popuptWindows(){
		
		 View popupWindow_view = mActivity.getLayoutInflater().inflate(R.layout.pmc_modegame, null,false);  
	     mPopupWindow = new PopupWindow(popupWindow_view,450,350, true);    
	     Button bt_scanf = (Button)popupWindow_view.findViewById(R.id.bt_scanf);   //dialog.xml视图里面的控件  
	     TextView bt_nextTime = (TextView)popupWindow_view.findViewById(R.id.bt_nextTime);  
	     
	     mTvshowText         = (TextView)popupWindow_view.findViewById(R.id.content);
	     mTvshowText.setText("古惑仔游戏需要使用手游岛模拟器,请下载");
	      
	     bt_scanf.setOnClickListener(new OnClickListener() {  
	              
	            @Override  
	            public void onClick(View v) {
	                
	            	LogUtil.e("点击访问手游岛");
					LogUtil.e("shouyouUrl"+simulatorUrl);	
					Intent intent = new Intent(Intent.ACTION_VIEW);  
			        Uri data = Uri.parse(Html.fromHtml(simulatorUrl).toString());  
			        intent.setData(data);  
			        intent.setPackage("com.google.android.browser");  
			        intent.addCategory("android.intent.category.BROWSABLE");  
			        intent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));  
			        mActivity.startActivity(intent);  
			        mActivity.finish();
	            	
	            }  
	     });  
	          
	     bt_nextTime.setOnClickListener(new OnClickListener() {  
	              
	            @Override  
	            public void onClick(View v) {  
	                // TODO Auto-generated method stub  
	                mPopupWindow.dismiss(); 
	                if(mBaseView!=null){
		        		 ((ViewGroup)mBaseView.getParent()).removeView(mBaseView);
			        	 mBaseView = null ;
		        	}
	                
	            }  
	     });   
		
	}
	
	
	public void shouYouWebVw(){
		/*
		KnLog.e("show shouyou web");
		getPopupWindow();
		mPopupWindow.showAtLocation(mActivity.getWindow().getDecorView(),Gravity.CENTER,10,50);  
		*/
		
		SpannableStringBuilder ssBuilser = new SpannableStringBuilder("立刻下载");
		StyleSpan span = new StyleSpan(Typeface.BOLD);
//		ScaleXSpan span1 = new ScaleXSpan(1);
//		ssBuilser.setSpan(span, 0, 7, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//		ssBuilser.setSpan(span1,0, 7, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		ssBuilser.setSpan(new RelativeSizeSpan(1.3f), 0,4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //1.5f表示默认字体大小的1.5倍
		//ssBuilser.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,6,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mActivity);
		builder.setTitle("温馨提示");
		builder.setMessage("为获得更好的游戏体验，请使用“手游岛”模拟器。");
		builder.setPositiveButton(ssBuilser, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
            	LogUtil.e("点击访问手游岛");
				LogUtil.e("shouyouUrl"+simulatorUrl);	
				Intent intent = new Intent(Intent.ACTION_VIEW);  
		        Uri data = Uri.parse(Html.fromHtml(simulatorUrl).toString());  
		        intent.setData(data);  
		        intent.setPackage("com.google.android.browser");  
		        intent.addCategory("android.intent.category.BROWSABLE");  
		        intent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));  
		        mActivity.startActivity(intent);  
		        mActivity.finish();
				
			}
		} );
		 builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
			   @Override
			   public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();
			    if(Splash.getView()!=null){
			    	((ViewGroup)Splash.getView().getParent()).removeView(Splash.getView());
			    }
			    LogUtil.e(" mSplashLister.onSuccess 55555555");
			    mSplashLister.onSuccess(null);
			    
			   }
		});
		mShouyouDialog = builder.create();
		mShouyouDialog.show();
		
	}
	
	private void getPopupWindow() {  
        
        if(null != mPopupWindow) {  
        	mPopupWindow.dismiss();  
            return;  
        }else {  
        	popuptWindows();  
        }  
    }  
	
	
}
