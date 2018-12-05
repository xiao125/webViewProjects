package com.proxy.x5;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.game.sdkproxy.R;
import com.proxy.util.LogUtil;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;

/**
 *  WebView 设置配置
 */
public class X5WebView extends WebView {


    ProgressBar progressBar;
    private TextView tvTitle;
    private RelativeLayout mRelativeLayout;
    private ImageView mImageView;
    private Context mcontext;
    private static final int MAX_LENGTH = 8;

    public X5WebView(Context context) {
        super(context);
        mcontext = context;
        initUI();
    }

    public X5WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mcontext = context;
        initUI();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(Context context, AttributeSet arg1) {
        super(context, arg1);
        mcontext = context;
        initUI();
    }


    private void initUI() {

        setHorizontalScrollBarEnabled(false);//水平不显示小方块
        setVerticalScrollBarEnabled(false); //垂直不显示小方块
        View headView = LayoutInflater.from(getContext()).inflate(R.layout.pmc_webloading_layout , null , false) ;
        progressBar = headView.findViewById(R.id.loading_progesss);
        mRelativeLayout = headView.findViewById(R.id.loading_bgLayout);
        mImageView = headView.findViewById(R.id.loading_bg);
       //mRelativeLayout.setBackgroundResource(R.drawable.splash1);
       // Glide.with(mcontext).load(R.drawable.splash1).into(mImageView);
        tvTitle = headView.findViewById(R.id.loading_tv);
        addView(headView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initWebViewSettings();
    }


    private void initWebViewSettings() {

        setBackgroundColor(getResources().getColor(android.R.color.white));
        setWebViewClient(client);
        setWebChromeClient(chromeClient);
        setClickable(true);
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        WebSettings webSettings = this.getSettings();
        webSettings.setJavaScriptEnabled(true);  //支持js
        webSettings.setDomStorageEnabled(true); //是否使用文档存储
        webSettings.setPluginState(WebSettings.PluginState.ON); //是否可使用插件，插件未来将不会得到支持
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);  //提高渲染的优先级
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true);  //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setSupportZoom(true);  //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。
        //若上面是false，则该WebView不可缩放，这个不管设置什么都不能缩放。
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        webSettings.supportMultipleWindows();  //多窗口
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);  //关闭webview中缓存
        webSettings.setAllowFileAccess(true);  //设置可以访问文件
        webSettings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //支持Https和Http的混合模式
            webSettings.setMixedContentMode(webSettings.getMixedContentMode()); //此方法是X5内核方法
        }
        this.requestFocusFromTouch(); //如果webView中需要用户手动输入用户名、密码或其他
    }

    private WebChromeClient chromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (tvTitle == null || TextUtils.isEmpty(title)) {
                return;
            }
            if (title != null && title.length() > MAX_LENGTH) {
                tvTitle.setText(title.subSequence(0, MAX_LENGTH));
            } else {
                tvTitle.setText(title);
            }
        }

        //监听进度
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
            if (progressBar != null && newProgress < 100) {
                //Webview加载没有完成 就显示我们自定义的加载图
                progressBar.setVisibility(VISIBLE);
                tvTitle.setText("加载"+newProgress+"%");
            } else if (progressBar != null) {
                //Webview加载完成 就隐藏进度条,显示Webview
                progressBar.setVisibility(GONE);
                mRelativeLayout.setVisibility(GONE);
                tvTitle.setVisibility(GONE);
            }
        }

        @Override
        public boolean onConsoleMessage(com.tencent.smtt.export.external.interfaces.ConsoleMessage consoleMessage) { // 查看web
            // console日志
            Log.e("console",
                    "[" + consoleMessage.messageLevel() + "] "
                            + consoleMessage.message() + "("
                            + consoleMessage.sourceId() + ":"
                            + consoleMessage.lineNumber() + ")");
            return super.onConsoleMessage(consoleMessage);
        }
    };


    //腾讯X5内核WebView兼容处理
    private WebViewClient client = new WebViewClient() {

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {
            return WebResourceResponseAdapter.adapter(WebViewCacheInterceptorInst.getInstance().
                    interceptRequest(s));
        }

         // 此方法添加于API21，调用于非UI线程（项目minSdkVersion>=21）
        // 拦截资源请求并返回数据，返回null时WebView将继续加载资源
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {

            return WebResourceResponseAdapter.adapter(WebViewCacheInterceptorInst.getInstance().
                    interceptRequest(WebResourceRequestAdapter.adapter(webResourceRequest)));
        }




        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.log("拦截url:" + url);
            if (url.startsWith("weixin://wap/pay?")) {
                LogUtil.log("微信支付拦截回调");
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mcontext.startActivity(intent);
                return true;

            } else if (url.startsWith("alipays://")) {
                LogUtil.log("支付包拦截回调");
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mcontext.startActivity(intent);
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        // onPageStarted会在WebView开始加载网页时调用
        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);
            LogUtil.log("onPageStarted.在WebView开始加载网页时调用");

        }

        // onPageStarted会在WebView加载网页结束时调用(重定向和加载完 iframes 时都会调用这个方法,判断页面加载完毕不太准确)
        @Override
        public void onPageFinished(WebView webView, String url) {
            super.onPageFinished(webView, url);
            LogUtil.log("WebView加载结束时调用url:" + url);
        }

        // 该方法传回了错误码，根据错误类型可以进行不同的错误分类处理
        @Override
        public void onReceivedError(WebView webView, int i, String s,
                                    String s1) {
            super.onReceivedError(webView, i, s, s1);
            LogUtil.log("错误码：" + i);
        }

        // 将要加载资源(url)
        @Override
        public void onLoadResource(WebView webView, String url) {

            if (url.indexOf(".jpg") > 0) {
                LogUtil.log("将要加载资源(url):"+url);
            }

            super.onLoadResource(webView, url);
        }
    };


}
