package com.game.sdk.mvp.Imp;

import android.content.Context;

import com.game.sdk.config.HttpUrlConstants;
import com.game.sdk.config.SDKStatusCode;
import com.game.sdk.mvp.presenter.PasswordUpdatePresenter;
import com.game.sdk.mvp.view.PasswordUpdateView;
import com.game.sdk.service.HttpService;
import com.game.sdk.tools.HttpRequestUtil;
import com.game.sdk.util.KnLog;

import org.json.JSONObject;

import java.io.IOException;

/**
 * 绑定手机逻辑类 请求---响应判断---
 * 通过<PasswordUpdateView将结果回调出去给View
 */

public class PasswordUpdatePresenterImp implements PasswordUpdatePresenter {

    private PasswordUpdateView mPasswordUpdateView;

    @Override
    public void attachView(PasswordUpdateView passwordUpdateView) {
        this.mPasswordUpdateView = passwordUpdateView;
    }

    @Override
    public void detachView() {
        this.mPasswordUpdateView = null;
    }

    @Override
    public void ModifyPasword(String phone,String code, String pass, String newsdk, Context context) {
        HttpService.ModifyPaswordSubmit(context, phone, code, pass, newsdk, new HttpRequestUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                try {
                    if(result !=null){
                        JSONObject obj = new JSONObject(result);
                        int dataCode = obj.getInt("code");
                        if (dataCode == 0){
                            String user_name = obj.getString("user_name");
                            mPasswordUpdateView.modifySuccess(SDKStatusCode.SUCCESS, user_name);
                        }else {
                            String reason = obj.getString("reason");
                            mPasswordUpdateView.modifyFailed(SDKStatusCode.FAILURE,reason);
                        }
                    }else {
                        mPasswordUpdateView.modifyFailed(SDKStatusCode.CHECK_NET_NOT, HttpUrlConstants.NET_NO_LINKING);
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestFailure(String request, IOException e) {
                mPasswordUpdateView.modifyFailed(SDKStatusCode.FAILURE,request);
            }

            @Override
            public void requestNoConnect(String msg, String data) {
                mPasswordUpdateView.modifyFailed(SDKStatusCode.CHECK_NET_NOT, HttpUrlConstants.NET_NO_LINKING);
            }
        });

    }

}
