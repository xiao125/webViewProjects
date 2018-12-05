package com.proxy;

public final class Constants {
		
	  //请求超时时间  单位毫秒
	  public static final int CONNECT_TIMEOUT = 15000;
	  
	  public static final int READ_TIMEOUT = 25000;
	  
	  public static final String CSIPW = "oms.cqsj.sdo.com:8080";
	  public static final String CSIPN = "omstest.cqsj.sdo.com:8080";
//	  public static final String CSIP = "115.182.4.32";
	  public static final String CENTER_CSIPW = "center.cqsj.sdo.com:8080";
	  public static final String CENTER_CSIPN = "centertest.cqsj.sdo.com:8080";
//	  public static final String CENTER_CSIP = "115.182.4.31";
	  public static final String JXWY_URL = "oms.uu66.com";
	  public static final String OMD_URL = "pay.u7game.cn";
	  public static final String OMD_URL2 = "oms.u7game.cn";
	  
	  public static final String Url = Data.getInstance().getGameInfo().getGameId()=="cqsj"?(Integer.parseInt(Data.getInstance().getGameInfo().getAdChannel())<=9999?CSIPN:CSIPW):OMD_URL2;
	  
	  public static final String yqmUrl = Data.getInstance().getGameInfo().getGameId()=="cqsj"?(Integer.parseInt(Data.getInstance().getGameInfo().getAdChannel())<=9999?CENTER_CSIPN:CENTER_CSIPW):"gameapi.szkuniu.com";
	  public static final String versionUprl = Data.getInstance().getGameInfo().getGameId()=="cqsj"?"gameversion.cqsj.sdo.com:8080":"gameversion.szkuniu.com";

	  public static  String LOGIN = "http://"+Url+"/api/login_check.php";								//登陆url
	  public static  String APPLY_ORDER = "http://"+Url+"/api/apply_order.php";							//请求订单url
	  public static  String ENTER_GAME = "http://"+Url+"/api/open_platform/datacenter/sendlv.php";		//发送等级url
	  public static  String PUSH_DATA = "http://"+Url+"/api/record_activate.php";						//游戏开始激活设备
	  public static  String ACTIVATION = "http://"+yqmUrl+"/api.php" ;  									//游戏邀请码数据请求地址
	  public static  String SDKUPDATEURL = "http://"+versionUprl+"/api.php";								//版本强更
	  public static  String wxPayURL = "http://oms.szkuniu.com/api/order_transfor.php";       //微信订单转换接口
	  public static  String PAYDATAURL = "http://pay.u7game.cn/api/open_platform/channel/yunding/send_pay.php";
	  public static  String ACTIVATIONS = "http://"+Url+"/api/cdk_active.php" ;  //请求激活设备
	  public static  String ISACTIVATIONS = "http://"+Url+"/api/is_imei_active.php" ; //验证设备是否被激活
	  public static  String GETHTMLURL = "http://oms.u7game.cn/api/get_h5_url.php"; //热血传奇获取打包测试url
	  public static  String HEARTBRAT = "http://111.230.170.150:21888/heartbeat"; //在线心跳包协议
	  public static  String CANCEL="http://oms.u7game.cn/api/open_platform/datacenter/cancel.php";//注销接口
	  public static  String HTMLURL="http://oms.u7game.cn/api/get_h5_url.php";//获取H5URL接口

	  
	  public static final int LANDSCAPE = 0;							//横屏		
	  public static final int PORTRAIT = 1;								//竖屏
	  
	  public static final String USER_ID = "userId";						//用户ID
	  public static final String SERVER_ID = "serverId";					//用户所属服务器ID
	  public static final String USER_LEVEL = "userLv";						//用户等级
	  public static final String SERVER_NAME = "serverName";				//服务器名称
	  public static final String ROLE_NAME = "roleName";					//角色名称
	  public static final String ROLE_CREATE_TIME = "roleCTime";			//创建角色时间
	  public static final String VIP_LEVEL = "vipLevel";					//VIP等级
	  public static final String IS_NEW_ROLE = "isNewRole";					//是否是新角色
	  public static final String ROLE_ID = "roleId";						//角色ID
	  public static final String FACTION_NAME = "factionName";				//帮派名称
	  public static final String EXPEND_INFO = "extraInfo";					//扩展字段
	  public static final String SCENE_ID = "scene_id";						//场景ID
	  public static final String BALANCE = "balance";						//游戏币余额
	  public static final String USER_ACCOUT_TYPE = "accout_type";			//玩家账号类型
	  public static final String USER_SEX  = "sex" ;						//玩家性别
	  public static final String USER_AGE  = "age"; 						//玩家年龄
	  
	  
	  
	  
}
