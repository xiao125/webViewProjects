package com.mc.weblib.command;

import android.app.Activity;
import android.content.Context;

import com.mc.weblib.CommandDispatcher;
import com.mc.weblib.utils.WebConstants;
import com.mc.weblib.view.WebAppInterface;
import com.mc.weblib.view.X5WebView;
import com.proxy.OpenSDK;
import com.proxy.ResultCode;
import com.proxy.bean.GameInfo;
import com.proxy.callback.SdkCallbackListener;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;
import com.tencent.smtt.sdk.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class InitProxy {

    private static InitProxy instance;
    OpenSDK m_proxy = OpenSDK.getInstance();
    private WebAppInterface webAppInterface;
    private String m_appKey = "XovHFMSPKstE0Gwrf8cBudDIV6JN5hC4";
    private String m_gameName = "cqsjh5";
    private int m_screenOrientation = 1;

    public static InitProxy getInstance(){
        if(instance == null){
            instance = new InitProxy();
        }
        return instance;
    }
    public void init(Context context, X5WebView webView ){
        Map<String, String> json = Util.readHttpData(context);
        String gameId = json.get("game_id");
        GameInfo m_gameInfo = new GameInfo(m_gameName,m_appKey, gameId,m_screenOrientation);
        //初始化中间件
        m_proxy.init((Activity) context, m_gameInfo,callbackListener);
        webAppInterface = new WebAppInterface(context,webView);
    }


    SdkCallbackListener callbackListener = new SdkCallbackListener<String>() {
        @Override
        public void callback(int code, String response) {
            switch (code) {
                case ResultCode.INIT_SUCCESS: //初始化成功
                    LogUtil.log("中间件初始化成功");
                    webAppInterface.activateCallback();
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
                         webAppInterface.loginCallback(open_id,sid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case ResultCode.LOGIN_FAIL:  //登录失败
                    LogUtil.log("中间件登录失败!"+ response);
                    break;

                case ResultCode.ENTERGAME_SUCCESS: //数据上报成功
                    webAppInterface.roleDataCallback(response);
                    break;
                case ResultCode.ENTERGAME_FAIL: //数据上报失败
                    LogUtil.log("中间件上报数据失败返回结果=" + response);

                    break;
                case ResultCode.APPLY_ORDER_SUCCESS:  //支付成功
                    webAppInterface.payCallback();

                    break;
                case ResultCode.APPLY_ORDER_FAIL:  //支付失败
                    LogUtil.log("支付失败！"+ response);
                    break;
                case ResultCode.LOGOUT:  //游戏内切换账号
                    webAppInterface.logoutCallback();
                    break;
                case ResultCode.XF_LOGOUT:  //SDK悬浮窗切换账号
                    webAppInterface.logoutCallback();
                    break;
                default:
                    break;
            }
        }
    };


}
