package com.game.sdk.mvp.presenter;

import android.content.Context;

import com.game.sdk.base.BasePresenter;
import com.game.sdk.mvp.view.BindCellView;
import com.game.sdk.mvp.view.SendCodeView;

import java.util.List;

/**
 * 绑定手机
 */

public interface BindCellPresenter extends BasePresenter<BindCellView>{
    void BindCellPhone(String username,String phone,String code,Context context);
   /* void SendCode(String phone,Context context);*/
}
