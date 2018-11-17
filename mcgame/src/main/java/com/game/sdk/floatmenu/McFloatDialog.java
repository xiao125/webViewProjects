package com.game.sdk.floatmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.game.sdk.floatmenu.customfloat.BaseFloatDailog;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public class McFloatDialog extends BaseFloatDailog {


    protected McFloatDialog(Context context) {
        super(context);
    }

    @Override
    protected View getLeftView(LayoutInflater inflater, View.OnTouchListener touchListener) {

        if (getContext() !=null){

        }




        return null;
    }

    @Override
    protected View getRightView(LayoutInflater inflater, View.OnTouchListener touchListener) {
        return null;
    }

    @Override
    protected View getLogoView(LayoutInflater inflater) {
        return null;
    }

    @Override
    protected void resetLogoViewSize(int hintLocation, View logoView) {

    }

    @Override
    protected void dragingLogoViewOffset(View logoView, boolean isDraging, boolean isResetPosition, float offset) {

    }

    @Override
    protected void shrinkLeftLogoView(View logoView) {

    }

    @Override
    protected void shrinkRightLogoView(View logoView) {

    }

    @Override
    protected void leftViewOpened(View leftView) {

    }

    @Override
    protected void rightViewOpened(View rightView) {

    }

    @Override
    protected void leftOrRightViewClosed(View logoView) {

    }

    @Override
    protected void onDestoryed() {

    }
}
