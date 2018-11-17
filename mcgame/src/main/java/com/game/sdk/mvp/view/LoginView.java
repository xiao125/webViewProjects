package com.game.sdk.mvp.view;
import com.game.sdk.base.BaseView;

/**
 * 登录
 */

public interface  LoginView extends BaseView {
    void loginSuccess(String msg,String data) ;
    void loginFailed(String msg, String data) ;
}
