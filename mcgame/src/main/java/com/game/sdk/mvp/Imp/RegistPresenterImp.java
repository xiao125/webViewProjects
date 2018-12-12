package com.game.sdk.mvp.Imp;

import android.content.Context;
import android.text.TextUtils;

import com.game.sdk.config.ConstData;
import com.game.sdk.config.HttpUrlConstants;
import com.game.sdk.config.SDKStatusCode;
import com.game.sdk.mvp.model.LoginBean;
import com.game.sdk.mvp.presenter.RegistPresenter;
import com.game.sdk.mvp.view.RegistView;
import com.game.sdk.service.HttpService;
import com.game.sdk.tools.HttpRequestUtil;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.KnLog;

import org.json.JSONObject;

import java.io.IOException;

/**
 * 账号或手机注册逻辑类 请求---响应判断---
 * 通过RegistView将结果回调出去给View
 */

public class RegistPresenterImp implements RegistPresenter {

    private String userName;
    private String passWord;
    private RegistView mRegistView;

    @Override
    public void attachView(RegistView registView) {
        this.mRegistView = registView;
    }

    @Override
    public void detachView() {
        this.mRegistView = null;
    }

    //用户名注册
    @Override
    public void RegistAccount(String username, String pasword, Context context) {
        final String  name = username;
        final String  pas = pasword;
        HttpService.Register(context, username, pasword, new HttpRequestUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                try {
                    if(result !=null){
                        JSONObject obj = new JSONObject(result);
                        int dataCode = obj.getInt("code");
                        String reason = obj.getString("reason");

                        if (dataCode == 0){
                           // DBHelper.getInstance().insertOrUpdateUser( name, pas);
                            mRegistView.registAccountSuccess(SDKStatusCode.SUCCESS,reason);
                        }else{
                            mRegistView.registAccountFailed(SDKStatusCode.FAILURE,reason);
                        }
                    }else {
                        mRegistView.registAccountFailed(SDKStatusCode.CHECK_NET_NOT, HttpUrlConstants.NET_NO_LINKING);
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestFailure(String request, IOException e) {
                mRegistView.registAccountFailed(SDKStatusCode.FAILURE,request);
            }

            @Override
            public void requestNoConnect(String msg, String data) {
                mRegistView.registAccountFailed(SDKStatusCode.CHECK_NET_NOT, HttpUrlConstants.NET_NO_LINKING);
            }
        });
    }

    //手机注册
    @Override
    public void RegistPhone(String phone, String pasword, String code, Context context) {
           final String  name = phone;
           final String  pas = pasword;
            HttpService.MobileRegister(context, phone, code, pasword, new HttpRequestUtil.DataCallBack() {
                @Override
                public void requestSuccess(String result) throws Exception {
                    try {
                        if(result !=null){
                            JSONObject obj = new JSONObject(result);
                            int dataCode = obj.getInt("code");
                            String reason = obj.getString("reason");
                            if (dataCode == 0){
                                DBHelper.getInstance().insertOrUpdateUser( name, pas);
                                mRegistView.registMobileSuccess(SDKStatusCode.SUCCESS,reason);
                            }else{
                                mRegistView.registMobileFailed(SDKStatusCode.FAILURE,reason);
                            }
                        }else {
                            mRegistView.registMobileFailed(SDKStatusCode.CHECK_NET_NOT, HttpUrlConstants.NET_NO_LINKING);
                        }

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void requestFailure(String request, IOException e) {
                    mRegistView.registMobileFailed(SDKStatusCode.FAILURE,request);
                }

                @Override
                public void requestNoConnect(String msg, String data) {
                    mRegistView.registMobileFailed(SDKStatusCode.CHECK_NET_NOT, HttpUrlConstants.NET_NO_LINKING);
                }
            });
    }


    @Override
    public void RandUserNam(String time) {

         HttpService.RandUserName(time, new HttpRequestUtil.DataCallBack() {
             @Override
             public void requestSuccess(String result) throws Exception {
                 try {
                     if (result != null) {
                         JSONObject obj = new JSONObject(result);
                         int dataCode = obj.getInt("code");
                         String username =obj.getString("user_name"); //用户名
                         String password =obj.getString("password"); //用户名
                         if (dataCode == 0) {
                             KnLog.log("获取到随机账号1"+result);
                             mRegistView.randUserNameSuccess(SDKStatusCode.SUCCESS, username);
                         } else {
                             KnLog.log("获取到随机账号2"+result);
                             mRegistView.randUserNameFailed(SDKStatusCode.FAILURE, result);
                         }
                     } else {
                         mRegistView.randUserNameFailed(SDKStatusCode.FAILURE, result);
                     }

                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }

             @Override
             public void requestFailure(String request, IOException e) {
                 mRegistView.randUserNameFailed(SDKStatusCode.FAILURE, request);
             }

             @Override
             public void requestNoConnect(String msg, String data) {
                 mRegistView.randUserNameFailed(SDKStatusCode.CHECK_NET_NOT, HttpUrlConstants.NET_NO_LINKING);
             }
         });
    }
}
