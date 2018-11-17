package com.game.sdk.mvp.presenter;

import android.content.Context;

import com.game.sdk.base.BasePresenter;
import com.game.sdk.mvp.view.BindCellView;
import com.game.sdk.mvp.view.ForgotPasswordView;

/**
 * 修改密码
 */

public interface ForgotPasswordPresenter extends BasePresenter<ForgotPasswordView> {
    void  GetUsername(String username, Context context);
}
