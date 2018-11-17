package com.game.sdk.mvp.presenter;

import android.content.Context;

import com.game.sdk.base.BasePresenter;
import com.game.sdk.mvp.model.LoginBean;
import com.game.sdk.mvp.view.LoginView;

/**
 * 登录Presenter
 */

public interface LoginPresenter extends BasePresenter<LoginView> {
    void login(LoginBean user , Context context) ;
}
