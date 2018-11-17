package com.game.sdk.mvp.presenter;

import android.content.Context;

import com.game.sdk.base.BasePresenter;
import com.game.sdk.mvp.view.PasswordUpdateView;

/**
 * 修改密码
 */

public interface PasswordUpdatePresenter extends BasePresenter<PasswordUpdateView> {
    void ModifyPasword(String phone,String code,String pass,String conpas,Context context);

}
