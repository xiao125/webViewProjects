package com.game.sdk.mvp.view;

import com.game.sdk.base.BaseView;

public interface SendCodeView extends BaseView{
    void sendCodeSuccess(int code,String data) ;
    void sendCodeFailed(int code,String data) ;
}
