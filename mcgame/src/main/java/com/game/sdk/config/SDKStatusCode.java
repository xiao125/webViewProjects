package com.game.sdk.config;

/**
 *
 * 全局状态码:
 */

public final class SDKStatusCode {
    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;
    public static final int QUERY_BIND_SUCCESS = 0; //已绑定
    public static final int QUERY_BIND_NOT = -1; //未绑定
    public static final int CHECK_NET_NOT =404;
    public static final int OTHER = 2;
    public static final int PAY_SUCCESS = 200;
    public static final int PAY_FAILURE = 404;
    public static final int PAY_OTHER = 500;
}
