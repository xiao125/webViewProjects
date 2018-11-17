package com.game.sdk.mvp.presenter;

import android.content.Context;

import com.game.sdk.base.BasePresenter;
import com.game.sdk.mvp.model.LoginBean;
import com.game.sdk.mvp.view.RegistView;

/**
 * 注册账号
 */

public interface RegistPresenter extends BasePresenter<RegistView> {
    void  RegistAccount(String username,String pasword,Context context);
    void  RegistPhone(String phone,String pasword,String code,Context context);
    void RandUserNam(String time);
}
