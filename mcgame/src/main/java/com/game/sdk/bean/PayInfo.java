package com.game.sdk.bean;


public class PayInfo{
	
	private String notifyurl;
	private String appid;
	private int waresid;
	private String exorderno;
	private double price;
	private String cpprivateinfo;
	private String productName;
	
	private String uid;
	private int serverId;
	
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public String getNotifyurl() {
		return notifyurl;
	}
	public void setNotifyurl(String notifyurl) {
		this.notifyurl = notifyurl;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public int getWaresid() {
		return waresid;
	}
	public void setWaresid(int waresid) {
		this.waresid = waresid;
	}
	public String getExorderno() {
		return exorderno;
	}
	public void setExorderno(String exorderno) {
		this.exorderno = exorderno;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getCpprivateinfo() {
		return cpprivateinfo;
	}
	public void setCpprivateinfo(String cpprivateinfo) {
		this.cpprivateinfo = cpprivateinfo;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
}
