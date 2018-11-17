package com.game.sdk.mvp.presenter;

import android.content.Context;

import com.game.sdk.base.BasePresenter;
import com.game.sdk.mvp.view.BindCellView;
import com.game.sdk.mvp.view.SendCodeView;

public interface SendCodePresenter extends BasePresenter<SendCodeView> {
    void SendCode(String phone,Context context);
}
