// 萌创Android SDK
CQGAME.sdk = {};
CQGAME.sdk.init = function() {
	// CQGAME.sdk.hasReportRole = false;
	MCBridge.activate();
};

// 支付
CQGAME.sdk.pay = function(data) {
	data.serverId = CQGAME.server_id;
	data.desc = data.desc ? data.desc : "充值";
	var json = JSON.stringify(data);
	console.log("PAY DATA: " + json);
	MCBridge.pay(json);
};

// 数据上报
CQGAME.sdk.report = function(data) {
	data.serverId = CQGAME.server_id;
	data.serverName = CQGAME.server_name;
	MCBridge.roleReport(JSON.stringify(data));
};

// 上报进服
CQGAME.sdk.reportRole = function(data) {
	// if(CQGAME.sdk.hasReportRole) return;

	// CQGAME.sdk.hasReportRole = true;
	CQGAME.sdk.report(data);
}

// 初始化回调
function activateCallback(data) {
	var info = JSON.parse(data);
	// {"reason":"初始化成功","code":0,"ad_channel":"20180928","channel":"u7game","imei":"867305031304947"}
	if(info.code == 0) {
		// 初始化成功 
		if(info.ad_channel && info.ad_channel.length > 0) {
			CQGAME.adChannel = info.ad_channel;
		}

		if(info.imei && info.imei.length > 0) {
			CQGAME.imei = info.imei;
		}

		console.info("初始化成功, adChannel: " + CQGAME.adChannel);

		MCBridge.login();
	} else {
		console.error("SDK 初始化失败");
		alert("初始化失败");
	}
}

// 登录回调
function loginCallback(data){
	var info = JSON.parse(data);
	console.log('SDK LOGIN SUCCESS, OPENID ' + info.open_id);
	// 登录成功
	CQGAME.openid = info.open_id;
	CQGAME.sid = info.sid;

	CQGAME.startgame();

	CQGAME.sdk.hasReportRole = false;
}

// 登录回调
function logoutCallback(data){
	window.location.reload();
}

// 数据上报回调
function roleReportCallback(data){
	var info = JSON.parse(data);
	// console.error("REPORT INFO: " + data);
	if(info.code == 0) {

	} else {
		console.error("SDK 上报失败");
	}
}
// 支付回调
function payCallback(data){
	var info = JSON.parse(data);
	// console.error(info);
	if(info.code == 0) {

	} else {
		console.error("SDK 支付失败");
		alert("支付失败");
	}
}