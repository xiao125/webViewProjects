package com.game.sdk.mvp.Imp;

import android.content.Context;

import com.game.sdk.config.HttpUrlConstants;
import com.game.sdk.config.SDKStatusCode;
import com.game.sdk.mvp.presenter.SendCodePresenter;
import com.game.sdk.mvp.view.SendCodeView;
import com.game.sdk.service.HttpService;
import com.game.sdk.tools.HttpRequestUtil;
import org.json.JSONObject;
import java.io.IOException;

public class SendCodePresenterImp implements SendCodePresenter {

    private SendCodeView mSendCodeView;

    @Override
    public void attachView(SendCodeView sendCodeView) {
        this.mSendCodeView = sendCodeView;
    }

    @Override
    public void detachView() {
        this.mSendCodeView = null;
    }
    @Override
    public void SendCode(String phone, Context context) {
        HttpService.SecCode(context, phone, new HttpRequestUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                try {
                    if(result !=null){
                        JSONObject obj = new JSONObject(result);
                        int dataCode = obj.getInt("code");
                        if (dataCode == 0){
                            mSendCodeView.sendCodeSuccess(SDKStatusCode.SUCCESS,result);
                        }else{
                            mSendCodeView.sendCodeFailed(SDKStatusCode.FAILURE,result);
                        }
                    }else {
                        mSendCodeView.sendCodeFailed(SDKStatusCode.CHECK_NET_NOT,HttpUrlConstants.NET_NO_LINKING);
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestFailure(String request, IOException e) {
                mSendCodeView.sendCodeFailed(SDKStatusCode.FAILURE,request);
            }

            @Override
            public void requestNoConnect(String msg, String data) {
                mSendCodeView.sendCodeFailed(SDKStatusCode.CHECK_NET_NOT,HttpUrlConstants.NET_NO_LINKING);
            }
        });
    }



}
