package com.mc.weblib.interfaces;

import android.content.Context;

import com.proxy.bean.KnPayInfo;
import com.tencent.smtt.sdk.WebView;

import java.util.Map;

/**
 * DWebView回调统一处理
 * 所有涉及到WebView交互的都必须实现这个callback
 */
public interface DWebViewCallBack {

    void webLogin();
    void webActivate();
    void webPay(KnPayInfo knPayInfo);
    void webRoleReport(Map<String, Object> data);
    void webU7SystemInfo();
    int  getCommandLevel();
    void exec(Context context, int commandLevel, String cmd, String params, WebView webView);
}
