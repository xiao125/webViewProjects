package com.game.sdk.mvp.Imp;
import android.content.Context;
import android.text.TextUtils;

import com.game.sdk.GameSDK;
import com.game.sdk.bean.Data;
import com.game.sdk.bean.UserInfo;
import com.game.sdk.config.ConstData;
import com.game.sdk.config.HttpUrlConstants;
import com.game.sdk.config.SDKStatusCode;
import com.game.sdk.mvp.model.LoginBean;
import com.game.sdk.mvp.presenter.LoginPresenter;
import com.game.sdk.mvp.view.LoginView;
import com.game.sdk.service.HttpService;
import com.game.sdk.tools.HttpRequestUtil;
import org.json.JSONObject;
import java.io.IOException;

/**
 *  登录逻辑类 请求---响应判断---
 * 通过MVPLoginView将结果回调出去给View
 */

public class LoginPresenterImp implements LoginPresenter {

    private String userName;
    private String passWord;
    private  LoginView mLoginView;

    @Override
    public void attachView(LoginView loginView) {
        this.mLoginView = loginView;
    }

    @Override
    public void detachView() {
        this.mLoginView = null;
    }

    //开始进行登录逻辑
    @Override
    public void login(LoginBean user, Context context) {
        userName = user.getUserName().toString().trim();
        passWord = user.getPassWord().toString().trim();
        if ((!TextUtils.isEmpty(userName)) && (!TextUtils.isEmpty(passWord))) {
            loginMethod(context,userName,passWord );
        } else {
            mLoginView.showAppInfo("","帐号或密码输入为空");
        }
    }


    private void loginMethod(Context context,String userName,String passWord){

        HttpService.doLogin(context, userName, passWord, new HttpRequestUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {

                try {
                    if(result !=null){
                        JSONObject obj = new JSONObject(result);
                        int dataCode = obj.getInt("code");
                        if (dataCode == 0){
                            String  open_id = obj.getString("open_id");
                            UserInfo userInfo = new UserInfo();
                            userInfo.setOpenId(open_id);
                            GameSDK.getInstance().setUserInfo(userInfo);

                            mLoginView.loginSuccess(ConstData.LOGIN_SUCCESS,result);
                        }else {
                            mLoginView.loginFailed(ConstData.LOGIN_FAILURE,result);
                        }
                    }else {
                        mLoginView.loginFailed(ConstData.LOGIN_FAILURE,result);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestFailure(String request, IOException e) {
                mLoginView.loginFailed(ConstData.LOGIN_FAILURE,request);
            }

            @Override
            public void requestNoConnect(String msg, String data) {
                mLoginView.loginFailed(HttpUrlConstants.NET_NO_LINKING,HttpUrlConstants.NET_NO_LINKING);
            }
        });
    }




}
