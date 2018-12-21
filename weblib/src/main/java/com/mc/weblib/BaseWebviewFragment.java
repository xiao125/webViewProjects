package com.mc.weblib;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mc.weblib.interfaces.Action;
import com.mc.weblib.interfaces.ICallBack;
import com.mc.weblib.utils.AndroidBug5497Workaround;
import com.mc.weblib.utils.MainLooper;
import com.mc.weblib.utils.SystemInfoUtil;
import com.mc.weblib.utils.WebConstants;
import com.mc.weblib.view.WebAppInterface;
import com.mc.weblib.view.X5WebView;
import com.proxy.Data;
import com.proxy.OpenSDK;
import com.proxy.ResultCode;
import com.proxy.bean.GameInfo;
import com.proxy.bean.KnPayInfo;
import com.proxy.callback.SdkCallbackListener;
import com.proxy.util.DeviceUtil;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;


public abstract class BaseWebviewFragment extends BaseFragment{

    protected X5WebView webView;
    public String webUrl;
    OpenSDK m_proxy = OpenSDK.getInstance();
    private String roleDate;
    private boolean isInit = false;
    private WebAppInterface webAppInterface;
    private String m_appKey = "XovHFMSPKstE0Gwrf8cBudDIV6JN5hC4";
    private String m_gameName = "cqsjh5";
    private int m_screenOrientation = 1;
    private  Activity mActivity;

    @LayoutRes
    protected abstract int getLayoutRes();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        //获取到url
        if (bundle != null) {
            webUrl = bundle.getString(WebConstants.INTENT_TAG_URL);
            LogUtil.log("当前加载的url为："+webUrl);
        }
        mActivity= this.getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        webView = view.findViewById(R.id.web_view);




        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AndroidBug5497Workaround.assistActivity(getActivity());

        webAppInterface = new WebAppInterface(mActivity,webView);
        webView.addJavascriptInterface(webAppInterface, "MCBridge");
        webAppInterface.setWvClientClickListener(new WebviewClick()); //这里就是js调用java端的具体实现
        CommandDispatcher.getInstance().initAidlConnect(mActivity, new Action() {
            @Override
            public void call(Object o) {
                MainLooper.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadUrl();
                    }
                });
            }
        });
    }





    protected void loadUrl() {

        try {
            webView.setBackgroundColor(Color.BLACK);
            webView.loadUrl(webUrl);
            WebViewCacheInterceptorInst.getInstance().loadUrl(webUrl,webView.getSettings().getUserAgentString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }


    SdkCallbackListener callbackListener = new SdkCallbackListener<String>() {
        @Override
        public void callback(int code, String response) {
            switch (code) {
                case ResultCode.INIT_SUCCESS: //初始化成功
                    LogUtil.log("中间件初始化成功");
                    isInit = true;
                    CommandDispatcher.getInstance().exec(mActivity,WebConstants.LEVEL_BASE, "Activate", getJson().toString(), webView, getDispatcherCallBack());
                    break;
                case ResultCode.INIT_FAIL:  //初始化失败
                    LogUtil.log("中间件初始化失败");
                    break;
                case ResultCode.LOGIN_SUCCESS:  //登录成功

                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response);
                        String open_id = obj.getString("open_id");
                        String sid = obj.getString("sid");
                        String loginCallback = getLoginjson(open_id,sid).toString();
                        CommandDispatcher.getInstance().exec(mActivity,WebConstants.LEVEL_LOGIN, "login", loginCallback, webView, getDispatcherCallBack());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case ResultCode.LOGIN_FAIL:  //登录失败
                    LogUtil.log("中间件登录失败!"+ response);
                    CommandDispatcher.getInstance().exec(mActivity,WebConstants.LEVEL_LOGIN, "login", response, webView, getDispatcherCallBack());
                    break;

                case ResultCode.ENTERGAME_SUCCESS: //数据上报成功
                    roleDate = response;
                    CommandDispatcher.getInstance().exec(getContext(),WebConstants.LEVEL_ROLE, "role", roleDate, webView, getDispatcherCallBack());
                    break;
                case ResultCode.ENTERGAME_FAIL: //数据上报失败
                    LogUtil.log("中间件上报数据失败返回结果=" + response);
                    CommandDispatcher.getInstance().exec(mActivity,WebConstants.LEVEL_ROLE, "role", roleDate, webView, getDispatcherCallBack());

                    break;
                case ResultCode.APPLY_ORDER_SUCCESS:  //支付成功
                    JSONObject json = new JSONObject();
                    try {
                        json.put("reason", "支付成功");
                        json.put("code", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    CommandDispatcher.getInstance().exec(mActivity,WebConstants.LEVEL_PAY, "pay", json.toString(), webView, getDispatcherCallBack());
                    break;
                case ResultCode.APPLY_ORDER_FAIL:  //支付失败
                    LogUtil.log("支付失败！"+ response);
                    break;
                case ResultCode.LOGOUT:  //游戏内切换账号
                    CommandDispatcher.getInstance().exec(mActivity,WebConstants.LEVEL_LOGOUT, "logout","", webView, getDispatcherCallBack());

                    break;
                case ResultCode.XF_LOGOUT:  //SDK悬浮窗切换账号
                    LogUtil.log("注销回调！========");
                    CommandDispatcher.getInstance().exec(mActivity,WebConstants.LEVEL_LOGOUT, "logout","", webView, getDispatcherCallBack());
                    break;
                default:
                    break;
            }
        }
    };




    class  WebviewClick implements WebAppInterface.wvClientClickListener{

        @Override
        public void wvLogin() {
            LogUtil.log("点击登录");
            if (!isInit) {
                return;
            } else {
                m_proxy.login(mActivity);
            }
        }

        @Override
        public void wvActivate() {
            LogUtil.log("sdk开始初始化=");
            Map<String, String> json = Util.readHttpData(getContext());
            String gameId = json.get("game_id");
            GameInfo m_gameInfo = new GameInfo(m_gameName, m_appKey, gameId, m_screenOrientation);
            //初始化中间件
            Data.getInstance().setGameActivity(mActivity);
            m_proxy.init(mActivity, m_gameInfo, callbackListener);
            LogUtil.log("BasewebviewFragment当前是否主进程:" + SystemInfoUtil.inMainProcess(mActivity, android.os.Process.myPid()));


        }

        @Override
        public void wvPay(KnPayInfo knPayInfo) {
            m_proxy.pay(mActivity, knPayInfo);
        }

        @Override
        public void wvRoleReport(Map<String, Object> data) {
            m_proxy.onEnterGame(data);
        }

        @Override
        public void wvU7SystemInfo() {

        }
    }

    protected CommandDispatcher.DispatcherCallBack getDispatcherCallBack() {
        return null;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return onBackHandle();
        }
        return false;
    }

    protected boolean onBackHandle() {
        if (webView != null) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private JSONObject getLoginjson(String openId,String sid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("open_id",openId);
            jsonObject.put("sid", sid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.log("返回json数据："+jsonObject);
        return jsonObject;
    }

    // 初始化成功返回的对象数据
    private JSONObject getJson() {
        Map<String, String> json = Util.readHttpData(mActivity);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("reason", "初始化成功");
            jsonObject.put("code", 0);
            jsonObject.put("ad_channel",json.get("adchannel"));
            jsonObject.put("channel",json.get("channel"));
            jsonObject.put("imei", DeviceUtil.getDeviceId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    @Override
    public void onPause() {
        webView.onPause();
        webView.pauseTimers();// 调用pauseTimers()全局停止Js
        super.onPause();
        if (isInit) {
            m_proxy.onPause();
        }
        LogUtil.log("==========onPause()===========");
    }

    @Override
    public void onResume() {
        webView.onResume();
        webView.resumeTimers(); // 调用onResume()恢复js
        super.onResume();
        if (isInit) {
            m_proxy.onResume();
        }
        LogUtil.log("==========onResume()===========");
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.log("==========onStart()===========");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isInit) {
            m_proxy.onStop();
        }
        LogUtil.log("==========onStop()===========");
    }

    @Override
    public void onDestroy() {
        if (webView != null) {
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
		/*if (isInit) {
			m_proxy.onDestroy();
		}*/
    }

    public void sendMessage(ICallBack callBack){
        callBack.get_message_from_Fragment(isInit,m_proxy);
    }

}
