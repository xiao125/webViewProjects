package com.proxy.bean;

import java.util.Map;
import com.proxy.Constants;
import com.proxy.Data;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;

public class GameInfo {

	private String gameName;
	private String appKey;
	private String gameId;
	private String channel;
	private String platform; //平台号，如：android，android1 等
	private static String adChannel;
	private int screenOrientation = 0;
	private String gid;

	private String adChannelTxt;

	/**
	 *
	 * @param gameName							游戏名称
	 * @param appKey							AppKey
	 * @param gameId							游戏ID
	 * @param screenOrientation					横竖屏   			KnConstants.LANDSCAPE：横屏  ， KnConstants.PORTRAIT	竖屏
	 */
	public GameInfo(String gameName, String appKey, String gameId,
					int screenOrientation) {
		super();
		this.gameName = gameName;
		this.appKey = appKey;
		this.gameId = gameId;
		this.screenOrientation = screenOrientation;
	}

	public String getAdChannel() {
		LogUtil.e("ad_channel信息:"+adChannel);
		return adChannel;
	}
	public void setAdChannel(String adChannel) {
		LogUtil.e("ad_channel="+adChannel);
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
	public int getScreenOrientation() {
		return screenOrientation;
	}
	public void setScreenOrientation(int screenOrientation) {
		this.screenOrientation =
				(screenOrientation == ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE ? ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
	}
	public String getGameName() {
		return gameName;
	}
	public void setGameName(String gameName) {
		this.gameName = gameName;
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

		adChannel = Util.getAdchannle(null);
		Map<String, String> json= Util.readHttpData(Data.getInstance().getApplicationContex());
		//获取本地asset目录下adChannel.txt中的参数
		String result = Util.getAssetsFileContent(Data.getInstance().getApplicationContex(),"SDKFile/adChannel.png");
		String game = Util.getJsonStringByName(result, "game");
		if(game.equals("rxcqh5")|| game.equals("cqsjh5")){ //H5 使用读取本地文件Hmcsdk.log 配置

			if(json!=null){
				adChannel = json.get("adchannel");
				LogUtil.log("读取log文件数据:"+json);
				gameInfo.setAdChannelTxt(json.toString());
				gameInfo.setAdChannel(json.get("adchannel"));
				gameInfo.setChannel(json.get("channel"));
				gameInfo.setGid("0");
				gameInfo.setPlatform(json.get("platform"));
			}else {

				ReadAdchannel(gameInfo,result);
			}
		}else{

			ReadAdchannel(gameInfo,result);
		}
	}


	//读取assets本地文件信息配置
	private static void ReadAdchannel(GameInfo gameInfo,String result){


		gameInfo.setAdChannelTxt(result);
		String channel = Util.getJsonStringByName(result, "channel");
		String gid = Util.getJsonStringByName(result, "gid");
		String domain_login = Util.getJsonStringByName(result, "domain_login");
		String domain_pay = Util.getJsonStringByName(result, "domain_pay");
		String domain_api = Util.getJsonStringByName(result, "domain_api");

		if( !TextUtils.isEmpty(domain_login) ){
			Constants.LOGIN = "http://"+domain_login+"/api/login_check.php";
		}
		if( !TextUtils.isEmpty(domain_pay) ){
			Constants.APPLY_ORDER = "http://"+domain_pay+"/api/apply_order.php";
		}
		if( !TextUtils.isEmpty(domain_api) ){
			Constants.ENTER_GAME = "http://"+domain_api+"/api/open_platform/datacenter/sendlv.php";
		}
		LogUtil.e("adChannel:"+adChannel);
		gameInfo.setAdChannel(adChannel);
		gameInfo.setChannel(channel);
		gameInfo.setGid(gid);

		if(adChannel.equals("14000471")||adChannel.equals("22000623")){
			//女皇与宰相 聚宝盆Platform
			gameInfo.setPlatform("android2");
		}else if (adChannel.equals("2302066")) {
			gameInfo.setPlatform("android3"); //景迅—安卓—传世OL（中网游）

		}else if (adChannel.equals("24020113")) {
			gameInfo.setPlatform("android2"); //微游汇—安卓—决斩沙城

		}else if (adChannel.equals("2402070")) {
			gameInfo.setPlatform("android1"); //奇点—安卓—传世OL

		}else if (adChannel.equals("24020910")) {
			gameInfo.setPlatform("android1");//爱上游戏—安卓—重庆传奇

		}else if (adChannel.equals("24020125")) {
			gameInfo.setPlatform("android1"); //奇天官网—安卓—血战苍穹

		}else if (adChannel.equals("24020138")) {
			gameInfo.setPlatform("android2"); //奇天官网1—安卓—皇族霸业

		}else if (adChannel.equals("240201050")) {
			gameInfo.setPlatform("android1"); //齐毅1—安卓—战狼霸业

		}else if (adChannel.equals("24020142")) {
			gameInfo.setPlatform("android1"); //优象YX—安卓—皇族霸业

		}else if (adChannel.equals("2802006")) {
			gameInfo.setPlatform("android1"); //奇天OPPO—安卓—皇族霸业

		}else if (adChannel.equals("2000000")) {
			gameInfo.setPlatform("android"); //

		}else if (adChannel.equals("220300188")) {
			gameInfo.setPlatform("android5"); //奇天官网—盛世传奇—皇族霸业

		}else if (adChannel.equals("220300190")) {
			gameInfo.setPlatform("android1"); //逗游吧—皇族霸业_专服

		}else if (adChannel.equals("2802012")) {
			gameInfo.setPlatform("android3"); //奇天官网—皇族霸业_专服

		}else if (adChannel.equals("28020109")) {
			//天宇游—安卓—贪狼传奇
			gameInfo.setPlatform("android1");

		}else if (adChannel.equals("24020119")) {
			gameInfo.setPlatform("android1"); //皇族霸业_机锋

		}else if (adChannel.equals("28020112")) {
			gameInfo.setPlatform("android1"); //贪狼传奇-乐嗨嗨

		}else if (adChannel.equals("28020133")) {
			gameInfo.setPlatform("android1"); //皇族盛世-云峰

		}else if (adChannel.equals("28020136")) {
			gameInfo.setPlatform("android1"); //龙图-游戏fan

		}else if (adChannel.equals("28020138")) {
			gameInfo.setPlatform("android1"); //屠龙霸业-TT语音

		}else if (adChannel.equals("28020141")) {
			gameInfo.setPlatform("android1"); //龙图-果盘

		}else if (adChannel.equals("28020140")) {
			gameInfo.setPlatform("android2"); //屠龙霸业-果盘

		}else if (adChannel.equals("28020142")) {
			gameInfo.setPlatform("android2"); //龙图-奇点

		}else if (adChannel.equals("28020143")) {
			gameInfo.setPlatform("android2"); //龙图-天宇游

		}
		else if (adChannel.equals("290200113")) {
			gameInfo.setPlatform("android1"); //OPPO-红颜霸业

		}
		else if (adChannel.equals("290200114")) {
			gameInfo.setPlatform("android2"); //OPPO-烈火斩

		}else if (adChannel.equals("280201300")) {
			gameInfo.setPlatform("android6"); //奇天黑白包，登录是奇天，兼容老包登录是百度的包

		}else if (adChannel.equals("28020151")) {
			gameInfo.setPlatform("android1"); //热血大唐替换包，皇族霸业

		}else if (adChannel.equals("290200121")) {
			gameInfo.setPlatform("android1"); //帝霸天下，奇天官网sdk——烈焰重生

		}else if (adChannel.equals("28020152")) {
			gameInfo.setPlatform("android2"); //众神征战 替换包， 皇族霸业

		}else if (adChannel.equals("28020153")) {
			gameInfo.setPlatform("android1"); //战旗， 皇族霸业

		}

		else{
			gameInfo.setPlatform("android");
		}



	}



}
