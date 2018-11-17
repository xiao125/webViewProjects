package com.game.sdk.mvp.view;

import com.game.sdk.base.BaseView;

/**
 * 注册账号
 */

public interface RegistView extends BaseView {

    void registAccountSuccess(int code,String data) ;
    void registAccountFailed(int code,String data) ;
    void registMobileSuccess(int code,String data) ;
    void registMobileFailed(int code,String data) ;
    void randUserNameSuccess(int code,String data) ;
    void randUserNameFailed(int code,String data) ;
}
