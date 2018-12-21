package com.proxy.bean;

/**	成员变量：</br>
 *	coinName			货币名称</br>
 *	coinRate			货币对人民币的比例</br>
 *	orderNo				游戏的订单号</br>
 *	price				商品单价 以  分为单位</br>
 *	productName			商品名称，</br>
 *	productId			商品ID，没有可不传</br>
 *	desc				商品描述</br>
 *	extraInfo			透传给 cp服务器的字段</br>
 */
public class KnPayInfo {
	
	private String coinName;
	private double coinRate;
	private String productName;
	private double price;
	private String orderNo;
	private String productId;
	private String desc;
	private String extraInfo;
	
	
	
	
	public String getCoinName() {
		return coinName;
	}
	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}
	public double getCoinRate() {
		return coinRate;
	}
	public void setCoinRate(double coinRate) {
		this.coinRate = coinRate;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getExtraInfo() {
		return extraInfo;
	}
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
}
