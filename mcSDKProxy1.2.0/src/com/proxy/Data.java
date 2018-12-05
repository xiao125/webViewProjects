package com.proxy;

import android.app.Activity;
import android.content.Context;

import com.proxy.bean.Channel;
import com.proxy.bean.GameInfo;
import com.proxy.bean.GameUser;
import com.proxy.bean.User;
import com.proxy.util.LogUtil;

public class Data {
	
	private static Data instance;
	
	private User knUser;
	private GameUser gameUser;
	private Channel sdkInfo;
	private GameInfo gameInfo;
	
	private Context applicationContex;
	private Activity gameActivity;
	
	private boolean newMode = false ; 
	private boolean deBugMode = false ;
	private String  inviteCode = null ;
	

	public String getInviteCode(){
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	public boolean isNewMode(){
		return newMode;
	}

	public void setNewMode(boolean newMode) {
		this.newMode = newMode;
	}

	public static Data getInstance(){
		
		if(instance == null)
		{
			instance = new Data();
		}
		return instance;
	}

	public User getUser() {
		return knUser;
	}

	public void setUser(User knUser) {
		this.knUser = knUser;
	}

	public GameUser getGameUser() {
		return gameUser;
	}

	public void setGameUser(GameUser gameUser) {
		this.gameUser = gameUser;
	}

	public Channel getSdkInfo() {
		return sdkInfo;
	}

	public void setChannelInfo(Channel sdkInfo) {
		this.sdkInfo = sdkInfo;
	}

	public GameInfo getGameInfo() {
		return gameInfo;
	}

	public void setGameInfo(GameInfo gameInfo) {
		this.gameInfo = gameInfo;
		GameInfo.initGameInfo(gameInfo);
	}
	
	
	public Context getApplicationContex() {
		return applicationContex;
	}

	public void setApplicationContex(Context applicationContex) {
		this.applicationContex = applicationContex;
	}

	public Activity getGameActivity() {
		return gameActivity;
	}

	public void setGameActivity(Activity gameActivity) {
		this.gameActivity = gameActivity;
		setApplicationContex(gameActivity.getApplicationContext());
	}

	public boolean isDeBugMode() {
		return this.deBugMode;
	}

	public void setDeBugMode(boolean deBugMode) {
		this.deBugMode = deBugMode;
	}
	
	
	
}
