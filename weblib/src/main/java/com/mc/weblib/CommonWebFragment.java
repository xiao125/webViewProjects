package com.mc.weblib;

import android.app.Activity;
import android.os.Bundle;

import com.mc.weblib.interfaces.ICallBack;
import com.mc.weblib.utils.SystemInfoUtil;
import com.mc.weblib.utils.WebConstants;
import com.proxy.OpenSDK;
import com.proxy.util.LogUtil;

/**
 * 主WebFragment
 */

public class CommonWebFragment extends BaseWebviewFragment{

    public static CommonWebFragment newInstance(String url) {
        CommonWebFragment fragment = new CommonWebFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(WebConstants.INTENT_TAG_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_common_webview;
    }

}
