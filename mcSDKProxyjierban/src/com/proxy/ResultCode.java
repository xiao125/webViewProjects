package com.proxy;

public class ResultCode {
	public static final int SUCCESS = 0;
	public static final int FAIL =1;
	public static final int INIT_SUCCESS = 1000;
	public static final int INIT_FAIL	= -1;
	public static final int LOGIN_SUCCESS = 2000;
	public static final int LOGIN_FAIL = 2001;
	public static final int ENTERGAME_SUCCESS = 3000;
	public static final int ENTERGAME_FAIL = 3001;
	public static final int APPLY_ORDER_SUCCESS = 4000;
	public static final int APPLY_ORDER_FAIL = 4001;
	public static final int LOGOUT = 5000; //退出回调
	public static final int XF_LOGOUT = 6000; //悬浮窗注销退出回调
	public static final int UNKNOW	= 2;
	public static final int NET_DISCONNET = 401;
}
