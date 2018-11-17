package com.game.sdk.mvp.view;

import com.game.sdk.base.BaseView;

/**
 * 忘记密码
 */

public interface ForgotPasswordView  extends BaseView {

    void getUsernameSuccess(int code,String data) ;
    void getUsernameFailed(int code,String data) ;

}
