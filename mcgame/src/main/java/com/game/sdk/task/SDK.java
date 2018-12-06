package com.game.sdk.task;

import android.text.TextUtils;

import com.game.sdk.GameSDK;
import com.game.sdk.bean.Data;
import com.game.sdk.util.Util;
public class SDK {

	public static final int CONNECT_TIMEOUT = 15000;
	public static final int READ_TIMEOUT = 25000;
	//public static final String OMD_URL = "pay.u7game.cn";
	//public static final String OMD_URL = "oms.u7game.cn";
	public static final String OMD_URL =  GameSDK.getInstance().getGameInfo().getApiUrl();

	public static String LOGIN_URL = "http://"+OMD_URL+"/api/login_check.php"; //登录
	public static String REG_URL = "http://"+OMD_URL+"/api/user_reg.php";//注册
	public static String APPLY_ORDER_URL = "http://omspay.szkuniu.com/api/apply_order.php";
	public static String CHANGE_PWD_URL = "http://"+OMD_URL+"/security.php"; //用旧密码更新新密码
	public static String GET_RESURITY_CODE_URL = "http://"+OMD_URL+"/api/send_rand_code.php";//获取手机验证码
	public static String BIND_MOBILE_URL = "http://"+OMD_URL+"/api/bind_mobile.php"; //绑定手机请求
	public static String UPDATE_PASSWORD_URL = "http://"+OMD_URL+"/api/find_pwd.php"; //根据手机验证码更改新密码
	public static String GET_ACCOUNT_URL = "http://"+OMD_URL+"/api/find_user_name.php"; //找回帐号接口
	public static String VISITOR_REG = "http://"+OMD_URL+"/api/visitor_reg.php";//游客登录
	public static String VISITOR_ACCOUNT_BIND = "http://"+OMD_URL+"/api/bind_username.php"; //游客绑定账号
	public static String QUERY_ACCOUNT_BIND = "http://"+OMD_URL+"/api/is_bind_mobile.php"; //查询账号是否绑定手机号
	public static String QUERY_MSI_BIND = "http://"+OMD_URL+"/api/is_bind_username.php"; //查询是否绑定账号
	public static String REG_MOBILE="http://"+OMD_URL+"/api/mobile_reg.php"; //手机注册
	public static String GET_USER_NAME="http://"+OMD_URL+"/api/get_user_name.php"; //验证账号是否存在
	public static String VISITOR_BIND_MOBILE="http://"+OMD_URL+"/api/visitor_bind_mobile.php"; //游客绑定手机号
	public static String RAND_USER_NAME="http://"+OMD_URL+"/api/rand_user_name.php"; //随机分配用户名接口
    public static String RECORD_ACTIVATE="http://"+OMD_URL+"/api/record_activate.php";//上报设备激活接口
	public static String ENTER_GAME="http://"+OMD_URL+"/api/sendlv.php";//上报游戏信息
	public static String CANCEL="http://"+OMD_URL+"/api/open_platform/datacenter/cancel.php";//注销接口

	public static final int LANDSCAPE = 0;
	public static final int PORTRAIT = 1;

	public static final int REQUESTCODE_REG = 1;
	public static final int REQUESTCODE_CHANGEPWD = 2;
	public static  final int UPDATE_PASSWORD=3;

	public static void changeConfig(String result) {

		String domain_login = Util
				.getJsonStringByName(result, "domain_login");
		String domain_pay = Util.getJsonStringByName(result, "domain_pay");

		if (!TextUtils.isEmpty(domain_login)) {
			LOGIN_URL = "http://" + domain_login + "/api/login_check.php";
			REG_URL = "http://"+domain_login+"/api/user_reg.php";
			CHANGE_PWD_URL = "http://"+domain_login+"/api/security.php";
		}
		if (!TextUtils.isEmpty(domain_pay)) {
			APPLY_ORDER_URL = "http://" + domain_pay + "/api/apply_order.php";
		}

	}

}
