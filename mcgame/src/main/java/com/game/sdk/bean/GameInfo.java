package com.game.sdk.bean;

import com.game.sdk.util.KnLog;
import com.game.sdk.util.Util;

public class GameInfo {
	
	private String appKey;
	private String gameName;
	private String gameId;
	//private String channel = "u7game";
	private String channel;
	private String platform = "android";
	private String adChannel;
	private String gid = "0";
	private String regKey = "kuniu@!#2014";
	private int orientation =0;
	private String adChannelTxt ="0";
	private String apiUrl ="oms.u7game.cn";

	public GameInfo(){
	}

	public GameInfo(String gameName, String appKey, String gameId,
					int screenOrientation) {
		super();
		this.gameName = gameName;
		this.appKey = appKey;
		this.gameId = gameId;
		this.orientation = screenOrientation;
	}


	
	public GameInfo(String appKey, String gameId, String channel,
			String platform, String adChannel, int orientation , String adChannelTxt) {
		super();
		this.appKey = appKey;
		this.gameId = gameId;
		this.channel = channel;
		this.platform = platform;
		this.adChannel = adChannel;
		this.orientation = orientation;
		this.adChannelTxt = adChannelTxt;
	}

	public String getApiUrl() {
		return apiUrl;
	}
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	public int getOrientation() {
		return orientation;
	}
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}
	public String getAdChannel() {
		return adChannel;
	}
	public void setAdChannel(String adChannel) {
		this.adChannel = adChannel;
	}
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getRegKey() {
		return regKey;
	}
	public void setRegKey(String regKey) {
		this.regKey = regKey;
	}
	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getAdChannelTxt() {
		return adChannelTxt;
	}

	public void setAdChannelTxt(String adChannelTxt) {
		this.adChannelTxt = adChannelTxt;
	}


	public static void initGameInfo(GameInfo gameInfo){
		//获取本地asset目录下adChannel.txt中的参数
		String result = Util.getAssetsFileContent(Data.getInstance().getApplicationContex(),"SDKFile/adChannel.png");
		gameInfo.setAdChannelTxt(result);
		KnLog.log("获取文件参数："+result);
//		String adChadChannelannel="2200040";
		String adChannel = Util.getAdchannle(null);
		String channel = Util.getJsonStringByName(result, "channel");
//		String channel="kaopu";
		String gid = Util.getJsonStringByName(result, "gid");
		KnLog.e("adChannel:"+adChannel);
		gameInfo.setAdChannel(adChannel);
		gameInfo.setChannel(channel);
		gameInfo.setGid(gid);
		 if (adChannel.equals("2802012")) {
			gameInfo.setPlatform("android3"); //奇天官网—皇族霸业_专服
		}
		else{
			gameInfo.setPlatform("android");
		}
	}


}
