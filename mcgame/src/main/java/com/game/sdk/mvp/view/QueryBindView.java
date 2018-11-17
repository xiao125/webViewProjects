package com.game.sdk.mvp.view;

import com.game.sdk.base.BaseView;

/**
 * 查询是否绑定手机
 */

public interface QueryBindView extends BaseView {
    void queryBindSuccess(int code,String data) ;
    void queryBindFailed(int code,String data) ;
}
