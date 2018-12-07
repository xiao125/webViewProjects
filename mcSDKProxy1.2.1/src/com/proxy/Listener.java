package com.proxy;

import com.proxy.listener.ExitListener;
import com.proxy.listener.InitListener;
import com.proxy.listener.InvitationListener;
import com.proxy.listener.LoginListener;
import com.proxy.listener.LogoutListener;
import com.proxy.listener.PayListener;
import com.proxy.listener.PushActivationListener;
import com.proxy.listener.PushDataListener;
import com.proxy.listener.RoleReportListener;
import com.proxy.listener.WeixinListener;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;

public class Listener {
	
	private static Listener instance = null;
	
	private ExitListener	exitListener;
	private InitListener	initListener;
	private LoginListener	loginListener;
	private LogoutListener	logoutListener;
	private PayListener		payListener;
	private PushDataListener pushDataListener;
	private RoleReportListener roleReportListener;
	private PushActivationListener  pushActivationListener;
	private InvitationListener  invivationListener ;
	private WeixinListener      weixinListener ;
	
	
	
	public InvitationListener getInvivationListener() {
		return invivationListener;
	}

	public void setInvivationListener(InvitationListener invivationListener) {
		this.invivationListener = invivationListener;
	}

	public static Listener getInstance(){
		
		if(instance == null){
			instance = new Listener();
		}
		return instance;
	}

	public ExitListener getExitListener() {
		return exitListener;
	}

	//游戏退出回调
	public void setExitListener(ExitListener exitListener) {
		
		this.exitListener = exitListener;
	}

	//游戏初始化监听
	public InitListener getInitListener() {
		return initListener;
	}

	public void setInitListener(InitListener initListener) {
		Util.writeErrorLog("setInitListener");
		this.initListener = initListener;
	}

	
	public LoginListener getLoginListener() {
		return loginListener;
	}

	public void setLoginListener(LoginListener loginListener) {
		this.loginListener = loginListener;
	}

	//设置游戏登出回调
	public LogoutListener getLogoutListener() {
		return logoutListener;
	}

	//游戏登出回调
	public void setLogoutListener(LogoutListener logoutListener) {
		this.logoutListener = logoutListener;
	}

	public PayListener getPayListener() {
		return payListener;
	}

	public void setPayListener(PayListener payListener) {
		this.payListener = payListener;
	}
	
	public void setPushDataListener(PushDataListener pushDataListener){
		this.pushDataListener = pushDataListener;
	}
	
	public PushDataListener  getPushDataListener(){
		return pushDataListener;
	}
	
	public void setPushActivationListenr(PushActivationListener pushActivationListener){
		this.pushActivationListener = pushActivationListener;
	}
	
	public PushActivationListener getPushActivationListener(){
		return pushActivationListener;
	}

	public WeixinListener getWeixinListener() {
		return weixinListener;
	}

	public void setWeixinListener(WeixinListener weixinListener) {
		this.weixinListener = weixinListener;
	}

	public RoleReportListener getRoleReportListener() {
		return roleReportListener;
	}

	public void setRoleReportListener(RoleReportListener roleReportListener) {
		this.roleReportListener = roleReportListener;
	}
}
