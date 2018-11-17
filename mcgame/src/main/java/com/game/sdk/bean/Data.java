package com.game.sdk.bean;

import android.app.Activity;
import android.content.Context;


public class Data {
	
	private static Data instance;

	private Channel sdkInfo;
	private GameUser gameUser;
	private GameInfo gameInfo;
	private Context applicationContex;
	private Activity gameActivity;
	private UserInfo userInfo;
	private boolean newMode = false ; 
	private boolean deBugMode = false ;
	private String  inviteCode = null ;

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
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

	public GameUser getGameUser() {
		return gameUser;
	}

	public void setGameUser(GameUser gameUser) {
		this.gameUser = gameUser;
	}

	public static Data getInstance(){
		
		if(instance == null)
		{
			instance = new Data();
		}
		return instance;
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
