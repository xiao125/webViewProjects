package com.proxy.callback;

/**
 * 全局回调
 */

public interface SdkCallbackListener<T> {
    void callback(int code, T response);
}
