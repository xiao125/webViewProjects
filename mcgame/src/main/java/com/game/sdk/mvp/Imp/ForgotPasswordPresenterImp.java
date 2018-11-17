package com.game.sdk.mvp.Imp;

import android.content.Context;

import com.game.sdk.config.HttpUrlConstants;
import com.game.sdk.config.SDKStatusCode;
import com.game.sdk.mvp.presenter.ForgotPasswordPresenter;
import com.game.sdk.mvp.view.ForgotPasswordView;
import com.game.sdk.service.HttpService;
import com.game.sdk.tools.HttpRequestUtil;

import org.json.JSONObject;

import java.io.IOException;

/**
 * 根据手机号码修改密码 逻辑类 请求---响应判断---
 * 通过ForgotPasswordView将结果回调出去给View
 */

public class ForgotPasswordPresenterImp implements ForgotPasswordPresenter {

    private ForgotPasswordView mForgotPasswordView;

    @Override
    public void attachView(ForgotPasswordView forgotPasswordView) {
        this.mForgotPasswordView = forgotPasswordView;
    }

    @Override
    public void detachView() {
        this.mForgotPasswordView = null;
    }


    @Override
    public  void GetUsername(String username, Context context) { //查询是否有该账号

        HttpService.GetUsername(context, username, new HttpRequestUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                try {
                    if (result != null) {
                        JSONObject obj = new JSONObject(result);
                        int dataCode = obj.getInt("code");
                        //String reason = obj.getString("reason");
                        if (dataCode == 0) {
                            mForgotPasswordView.getUsernameSuccess(SDKStatusCode.SUCCESS, result);
                        } else {
                            String reason = obj.getString("reason");
                            mForgotPasswordView.getUsernameFailed(SDKStatusCode.FAILURE, reason);
                        }
                    } else {
                        mForgotPasswordView.getUsernameFailed(SDKStatusCode.FAILURE, result);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestFailure(String request, IOException e) {
                mForgotPasswordView.getUsernameFailed(SDKStatusCode.FAILURE, request);
            }

            @Override
            public void requestNoConnect(String msg, String data) {
                mForgotPasswordView.getUsernameFailed(SDKStatusCode.CHECK_NET_NOT, HttpUrlConstants.NET_NO_LINKING);
            }
        });
    }



}
