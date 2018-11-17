package com.game.sdk.mvp.presenter;

import android.content.Context;

import com.game.sdk.base.BasePresenter;
import com.game.sdk.mvp.view.QueryBindView;

/**
 *
 */

public interface QueryBindPresenter extends BasePresenter<QueryBindView> {
    void queryBindAccont(String username,Context context);
}
