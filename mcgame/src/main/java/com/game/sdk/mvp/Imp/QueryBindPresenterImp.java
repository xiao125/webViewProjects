package com.game.sdk.mvp.Imp;

import android.content.Context;

import com.game.sdk.config.HttpUrlConstants;
import com.game.sdk.config.SDKStatusCode;
import com.game.sdk.mvp.presenter.QueryBindPresenter;
import com.game.sdk.mvp.view.QueryBindView;
import com.game.sdk.service.HttpService;
import com.game.sdk.tools.HttpRequestUtil;

import org.json.JSONObject;

import java.io.IOException;

/**
 *查询是否绑定手机逻辑类 请求---响应判断---
 * 通过QueryBindView将结果回调出去给View
 */

public class QueryBindPresenterImp implements QueryBindPresenter {

    private QueryBindView mQueryBindView;

    @Override
    public void attachView(QueryBindView queryBindView) {
        this.mQueryBindView = queryBindView;
    }

    @Override
    public void detachView() {
        this.mQueryBindView = null;
    }

    @Override
    public void queryBindAccont(String username, Context context) {
        //查询账号是否绑定手机号
        HttpService.queryBindAccont(context,username,new HttpRequestUtil.DataCallBack(){
            @Override
            public void requestSuccess(String result) throws Exception {
                try {
                    if(result !=null){
                        JSONObject obj = new JSONObject(result);
                        int dataCode = obj.getInt("code");
                        if (dataCode == 0){
                            String model = obj.getString("mobile");
                            mQueryBindView.queryBindSuccess(SDKStatusCode.QUERY_BIND_SUCCESS,model);
                        }else if (dataCode == -1){
                            mQueryBindView.queryBindSuccess(SDKStatusCode.QUERY_BIND_NOT,result);
                        }
                    }else {
                        mQueryBindView.queryBindFailed(SDKStatusCode.FAILURE,result);
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void requestFailure(String request, IOException e) {
                mQueryBindView.queryBindFailed(SDKStatusCode.FAILURE,request);
            }

            @Override
            public void requestNoConnect(String msg, String data) {
                mQueryBindView.queryBindFailed(SDKStatusCode.CHECK_NET_NOT, HttpUrlConstants.NET_NO_LINKING);
            }
        });

    }
}
