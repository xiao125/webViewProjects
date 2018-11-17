package com.game.sdk.mvp.Imp;

import android.content.Context;

import com.game.sdk.config.HttpUrlConstants;
import com.game.sdk.config.SDKStatusCode;
import com.game.sdk.mvp.presenter.BindCellPresenter;
import com.game.sdk.mvp.presenter.SendCodePresenter;
import com.game.sdk.mvp.view.BindCellView;
import com.game.sdk.mvp.view.SendCodeView;
import com.game.sdk.service.HttpService;
import com.game.sdk.tools.HttpRequestUtil;

import org.json.JSONObject;

import java.io.IOException;

/**
 * 绑定手机逻辑类 请求---响应判断---
 * 通过BindCellView将结果回调出去给View
 */

public class BindCellPresenterImp  implements BindCellPresenter{

    private  BindCellView mBindCellView;

    @Override
    public void attachView(BindCellView bindCellView) {
         this.mBindCellView = bindCellView;
    }

    @Override
    public void detachView( ) {
        this.mBindCellView = null;
    }

    @Override
    public void BindCellPhone(String username,String phone,String code,Context context) {

        HttpService.BindMobile(context, phone, username, code, new HttpRequestUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                try {
                    if(result !=null){
                        JSONObject obj = new JSONObject(result);
                        int dataCode = obj.getInt("code");
                        String reason = obj.getString("reason");
                        if (dataCode == 0){
                            mBindCellView.BindSuccess(SDKStatusCode.SUCCESS,reason);
                        }else{
                            mBindCellView.BindFailed(SDKStatusCode.FAILURE,reason);
                        }
                    }else {
                        mBindCellView.BindFailed(SDKStatusCode.CHECK_NET_NOT,HttpUrlConstants.NET_NO_LINKING);
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestFailure(String request, IOException e) {
                mBindCellView.BindFailed(SDKStatusCode.FAILURE,request);
            }

            @Override
            public void requestNoConnect(String msg, String data) {
                mBindCellView.BindFailed(SDKStatusCode.CHECK_NET_NOT,HttpUrlConstants.NET_NO_LINKING);
            }
        });
    }
}
