package com.proxy.activity;
import com.game.sdkproxy.R;
import com.proxy.util.JsInterface;
import com.proxy.util.LoadingDialog;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;
import com.proxy.util.JsInterface.wvClientClickListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class StartWebView extends Activity{

	private WebView webView;
	private static String m_url = null;
	private JsInterface JsInterface = new JsInterface();
	private static Activity m_activity = null;
	public static int WIDTH = 0;
	public static int HEIGHT = 0;
	private static String m_wxUrl;
	private TextView tv_back;


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉应用标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.mcpr_webvidio);
		initView();
		initWebView();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		WIDTH = dm.widthPixels;
		HEIGHT = dm.heightPixels;

	}

	private void initWebView() {
		// 判断是否wifi如果不是wifi活着其他的就延时
		webView = (WebView) findViewById(R.id.webView);
		WebSettings ws = webView.getSettings();
		ws.setBuiltInZoomControls(true);// 隐藏缩放按钮
		ws.setUseWideViewPort(true);// 可任意比例缩放
		ws.setSavePassword(true);
		ws.setSaveFormData(true);// 保存表单数据
		ws.setJavaScriptEnabled(true);
		ws.setDomStorageEnabled(true);
		ws.setSupportMultipleWindows(true);// 新加
		// 如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
		ws.setJavaScriptEnabled(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //Android5.0上 WebView中Http和Https混合问题
			webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		webView.setWebViewClient(new MyWebViewClient());
		webView.addJavascriptInterface(JsInterface, "JsInterface");
		JsInterface.setWvClientClickListener(new WebviewClick());// 这里就是js调用java端的具体实现
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.loadUrl(m_url);
	}

	private  void initView(){

		m_url = getIntent().getExtras().getString("url");
		m_wxUrl = getIntent().getExtras().getString("wxUrl");
		LogUtil.e("url:" + m_url+"  wxUrl="+m_wxUrl);
		LoadingDialog.show(m_activity, "请稍等...", true);
		tv_back =  (TextView) findViewById(R.id.tv_back_id);
		tv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}



	public class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			/*if (url.startsWith("weixin://wap/pay?")) {
				LogUtil.log("微信支付拦截回调");
				if(Util.isWxInstall(StartWebView.this)){
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url));
					startActivity(intent);
					return true;
				}else {
					Util.ShowTips(StartWebView.this, "没有安装微信,请安装微信后支付");
					return true;
				}
			}
			else {
				PayTask task = new PayTask(StartWebView.this);
				task.payInterceptorWithUrl(url, true, new H5PayCallback() {
					public void onPayResult(H5PayResultModel result) {
						final String url = result.getReturnUrl();
						if(!TextUtils.isEmpty(url)) {
							StartWebView.this.runOnUiThread(new Runnable() {
								public void run() {
									StartWebView.this.webView.loadUrl(url);
								}
							});
						}

					}
				});
				return super.shouldOverrideUrlLoading(view, url);
			}*/
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
									String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			LoadingDialog.dismiss();
		}

	}


	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.e("onResume");
		if (webView != null) {
			LogUtil.e("webView is not null");
			webView.onResume();
			webView.resumeTimers();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.e("onPause");
		if (webView != null) {
			LogUtil.e("webView is not null");
			webView.onPause();
			webView.pauseTimers();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		webView.loadUrl("about:blank");
		webView.stopLoading();
		webView.setWebChromeClient(null);
		webView.setWebViewClient(null);
		webView.destroy();
		webView = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			LogUtil.e("返回++");
			webView.loadUrl("about:blank");
			StartWebView.this.finish();
		}
		return false;
	}


	//js调用方法
	class WebviewClick implements wvClientClickListener {

		@Override
		public void wvHasClickEnvent(String title, String content,
									 String imageUrl, String url) {

			LogUtil.e("title:" + title + "content:" + content + "imageUrl:"
					+ imageUrl + "url:" + url);

		}

		@Override
		public void wvCloseWebEvent() {
			// TODO Auto-generated method stub
			m_activity.finish();
			m_activity = null;
		}

		@Override
		public void wvWxWebPayEvent() {
			// TODO Auto-generated method stub
			LogUtil.e("wvWxWebPayEvent 回调");
			webView.loadUrl(m_wxUrl);

			//现在支付协议（已停用）
			//Map extraHeaders = new HashMap();
			//extraHeaders.put("Referer", "https://pay.ipaynow.cn");//例如 http://www.baidu.com
			//webView.loadUrl(m_wxUrl, extraHeaders);//targetUrl为微信下单地址

		}

	}
}
