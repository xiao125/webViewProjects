package com.game.sdk.callback;

/**
 * 全局回调
 */

public interface SdkCallbackListener<T> {
    void callback(int code, T response);
}
