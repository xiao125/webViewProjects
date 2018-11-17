package com.proxy.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import com.game.sdkproxy.R;
import com.proxy.OpenSDK;
import com.proxy.Data;
import com.proxy.activity.StartWebView;
import com.proxy.util.JsInterface.wvClientClickListener;
import android.R.bool;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class WxTools {

	private WebView m_view = null ;
	private JsInterface JsInterface = new JsInterface(); 
	private static Activity   mActivity = null;
	private static WxTools m_instance = null ;
	private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
	private  static View		    m_View = null ;
	private final String webUrl = "http://shaosi.gameact.szkuniu.com/shaosi/starinteraction/";
	private final String nonwebUrl = "http://shaosi.gameact.szkuniu.com/shaosi/baobao/";
	
	
	public View getM_View() {
		return m_View;
	}

	public void setM_View(View m_View) {
		this.m_View = m_View;
	}

	private WxTools(){
	
	}
	
	public static  WxTools getIntance(){
		if(m_instance!=null){
			
		}else{
			m_instance = new WxTools();
		}
		return m_instance;
	}
	
	private void initWeb( final Activity act , final  int serverId ,  final int charId , final String serverName , final  String playerName , final int playerType , final int curStarId  ){
		
		if(m_View!=null){
			return ;
		}
		
		String server_id =  Integer.toString(serverId) ;
		String char_id      = Integer.toString(charId) ;
		String platform = "android" ;
		String channel  = Util.getAdchannle(act);
		String packageName = act.getPackageName();
		String app_secret = "wodeandroid@#$1sd1@#";
		String server_name = serverName ;
		String player_name = playerName ;
		String player_type =  Integer.toString(playerType) ;
		String cur_star_id =  Integer.toString(curStarId)  ;
		
		Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg0.compareTo(arg1);
			}
		} );
		update_params.put("server_id", server_id);
		update_params.put("uid", char_id);
		update_params.put("platform",platform);
		update_params.put("channel",channel);
		update_params.put("packagename",packageName);
		
		Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
		update_params1.put("server_name",server_name);
		update_params1.put("player_name",player_name);
		update_params1.put("player_type",player_type);
		update_params1.put("cur_star_id",cur_star_id);
		
		String urlParams = Util.parseMapToString(update_params1);
		
		String newWeburl = webUrl +"?"+urlParams;
		
		mActivity = act ;
		
		LayoutInflater inflater = act.getLayoutInflater();
		m_View = inflater.inflate(R.layout.pmc_webview, null);
		m_view  = (WebView)m_View.findViewById(R.id.pmc_web);
	    m_view.setWebChromeClient(new WebChromeClient(){
	
				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					// TODO Auto-generated method stub
					super.onProgressChanged(view, newProgress);
				}
	    });
	    m_view.setWebViewClient(new WebViewClient(){
	
				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					// TODO Auto-generated method stub
					super.onReceivedError(view, errorCode, description, failingUrl);
				}
	   });
       m_view.getSettings().setJavaScriptEnabled(true);
       m_view.getSettings().setUseWideViewPort(true);
       m_view.getSettings().setLoadWithOverviewMode(true);
       m_view.addJavascriptInterface(JsInterface,"JsInterface");  
      
  	  if(URLUtil.isNetworkUrl(newWeburl)){
  		LogUtil.e("newWebUrl:"+newWeburl);
  		 m_view.loadUrl(newWeburl);
  	  }else{
  		LogUtil.e("the url is not valid");
  		 return ; 
  	  }
  	  act.addContentView(m_View, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
	}
	
	
	private void initWeb( Activity act , final  int serverId ,  final int charId , final String serverName , final  String playerName , final int playerType , final int curStarId ,   int width , int hight ){
		
		if(m_View!=null){
			return ;
		}
		
		String server_id =  Integer.toString(serverId) ;
		String char_id      = Integer.toString(charId) ;
		String platform = "android" ;
		String channel  = Util.getAdchannle(act);
		String packageName = act.getPackageName();
		String app_secret = "wodeandroid@#$1sd1@#";
		String server_name = serverName ;
		String player_name = playerName ;
		String player_type =  Integer.toString(playerType) ;
		String cur_star_id =  Integer.toString(curStarId)  ;
		
		
		Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg0.compareTo(arg1);
			}
		} );
		
		update_params.put("server_id", server_id);
		update_params.put("uid", char_id);
		update_params.put("platform",platform);
		update_params.put("channel",channel);
		update_params.put("packagename",packageName);
		
		Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
		update_params1.put("server_name",server_name);
		update_params1.put("player_name",player_name);
		update_params1.put("player_type",player_type);
		update_params1.put("cur_star_id",cur_star_id);
		
		String urlParams = Util.parseMapToString(update_params1);
		String newWeburl = nonwebUrl +"?"+urlParams;
		mActivity = act ;
		
		LayoutInflater inflater = act.getLayoutInflater();
		m_View = inflater.inflate(R.layout.pmc_webview, null);
		m_view  = (WebView)m_View.findViewById(R.id.pmc_web);
	    m_view.setWebChromeClient(new WebChromeClient(){
	
				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					// TODO Auto-generated method stub
					super.onProgressChanged(view, newProgress);
				}
	      });
	    m_view.setWebViewClient(new WebViewClient(){
	
				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					// TODO Auto-generated method stub
					super.onReceivedError(view, errorCode, description, failingUrl);
				}
	      });

       m_view.getSettings().setJavaScriptEnabled(true);
       m_view.getSettings().setUseWideViewPort(true);
       m_view.getSettings().setLoadWithOverviewMode(true);
       m_view.addJavascriptInterface(JsInterface,"JsInterface");
       m_view.getSettings().setSupportZoom(true);
       
  	  if(URLUtil.isNetworkUrl(newWeburl)){
  		LogUtil.e("newWebUrl:"+newWeburl);
  		 m_view.loadUrl(newWeburl);
  	  }else{
  		LogUtil.e("the url is not valid");
  		 return ; 
  	  }
  	  
  	 FrameLayout.LayoutParams  layoutParams1 = new FrameLayout.LayoutParams(width,hight);
  	 layoutParams1.gravity = Gravity.CENTER;
  	 act.addContentView(m_View, layoutParams1 );
  	  
  }
	
	
	public void openWeb( Activity act , final String url ){
		
		//initWeb(act,serverId,charId,serverName,playerName,playerType,curStarId);
		//JsInterface.setWvClientClickListener(new WebviewClick());//这里就是js调用java端的具体实现
		Intent intent = new Intent(act,StartWebView.class);
		intent.putExtra("url",  url );
		act.startActivity( intent );
		
	}
	
	public void shared( Activity act , final  int serverId ,  final int charId , final String serverName , final  String playerName , final int playerType , final int curStarId ,  int width , int hight ){
		//initWeb(act,serverId,charId,serverName,playerName,playerType,curStarId,width,hight);
		//JsInterface.setWvClientClickListener(new WebviewClick());//这里就是js调用java端的具体实现
		Intent intent = new Intent(act,StartWebView.class);
		intent.putExtra("serverId",   serverId);
		intent.putExtra("charId", 	  charId);
		intent.putExtra("serverName", serverName);
		intent.putExtra("playerName", playerName);
		intent.putExtra("playerType", playerType);
		intent.putExtra("curStarId",  curStarId);
		intent.putExtra("width",  	  width);
		intent.putExtra("hight", 	  hight);
	
		act.startActivity( intent );
	}
	
	public void sharedByPass( Activity act , final  int serverId ,  final int charId , final String serverName , final  String playerName , final int playerType , final int curStarId ){
		
		//initWeb(act,serverId,charId,serverName,playerName,playerType,curStarId);
		//JsInterface.setWvClientClickListener( new WebviewClickByPass());
		Intent intent = new Intent(act,StartWebView.class);
		intent.putExtra("serverId",   serverId);
		intent.putExtra("charId", 	  charId);
		intent.putExtra("serverName", serverId);
		intent.putExtra("playerName", playerName);
		intent.putExtra("playerType", playerType);
		intent.putExtra("curStarId",  curStarId);
		act.startActivity( intent );

	}
	
	
	
	
	 class WebviewClick implements wvClientClickListener {  
   	  
   	  @Override  
   	  public void wvHasClickEnvent(String title , String content , String imageUrl, String url) { 
   		 

	  }

	@Override
	public void wvCloseWebEvent() {
		// TODO Auto-generated method stub
			if(m_View!=null){
				((ViewGroup)m_View.getParent()).removeView(m_View);
				setM_View(null);
			}
		}

	@Override
	public void wvWxWebPayEvent() {
		// TODO Auto-generated method stub
		
	}    
	
    } 
	 
	 class WebviewClickByPass implements wvClientClickListener {  
	   	  
	   	  @Override  
	   	  public void wvHasClickEnvent(String title , String content , String imageUrl, String url) { 
	   		LogUtil.e("title:"+title+";content:"+content+";imageurl:"+imageUrl+";url:"+url);
	   	  }

		@Override
		public void wvCloseWebEvent() {
			// TODO Auto-generated method stub
			if(m_View!=null){
				((ViewGroup)m_View.getParent()).removeView(m_View);
				setM_View(null);
			}
		}

		@Override
		public void wvWxWebPayEvent() {
			// TODO Auto-generated method stub
			
		}  
	   	  
	 }
	 
	
	 
}
