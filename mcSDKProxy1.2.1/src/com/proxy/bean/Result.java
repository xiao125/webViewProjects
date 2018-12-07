package com.proxy.bean;

import org.json.JSONObject;

public class Result {
	
	private int code;
	private String reason;
	
	public Result(int code, String reason) {
		super();
		this.code = code;
		this.reason = reason;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	@Override
	public String toString(){
		
		JSONObject obj = new JSONObject();
		
		try {
			obj.put("code" , this.code);
			obj.put("reason", this.reason);
		} catch (Exception e) {
		
		}
		return obj.toString();
	}
	
}

