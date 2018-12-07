package com.proxy.bean;

public class User {
	
	private String userName;
	
	private String openId;
	private String sid;
	private String sign;
	private String extenInfo ;
	
	private String extraInfo1;
	private String extraInfo2;
	public String getExtraInfo1() {
		return extraInfo1;
	}

	public void setExtraInfo1(String extraInfo1) {
		this.extraInfo1 = extraInfo1;
	}

	public String getExtraInfo2() {
		return extraInfo2;
	}

	public void setExtraInfo2(String extraInfo2) {
		this.extraInfo2 = extraInfo2;
	}
	
	
	
	public String getExtenInfo() {
		return extenInfo;
	}
	public void setExtenInfo(String extenInfo) {
		this.extenInfo = extenInfo;
	}
	private int isIncompany = 0;
	
	private boolean isLogin = false;
	
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public boolean isLogin() {
		return isLogin;
	}
	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public int isIncompany() {
		return isIncompany;
	}
	public void setIsIncompany(int isIncompany) {
		this.isIncompany = isIncompany;
	}
	
	
	
}
