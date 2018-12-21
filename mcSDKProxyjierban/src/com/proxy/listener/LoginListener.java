package com.proxy.listener;

import com.proxy.bean.User;


public interface LoginListener{
	
	public void onSuccess(User user);
	
	public void onFail(String result);
}

