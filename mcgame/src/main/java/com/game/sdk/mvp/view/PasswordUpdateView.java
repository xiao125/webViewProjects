package com.game.sdk.mvp.view;

import com.game.sdk.base.BaseView;

/**
 * 修改密码 View
 *
 */

public interface PasswordUpdateView extends BaseView {
    void modifySuccess(int code,String data) ;
    void modifyFailed(int code,String data) ;

}
