package com.game.sdk.mvp.view;

import com.game.sdk.base.BaseView;

/**
 * 绑定手机
 */

public interface BindCellView extends BaseView {
    void BindSuccess(int code,String data) ;
    void BindFailed(int code,String data) ;
}
