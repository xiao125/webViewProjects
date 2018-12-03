// 萌创SDK
CQGAME.sdk = {};
CQGAME.sdk.sdk = new u7gameSdk();
CQGAME.sdk.init = function() {
	// CQGAME.sdk.hasReportRole = false;
	try {
		var data = JSON.stringify({"x": -20,"y":12});
		CQGAME.sdk.sdk.activate(data);
	} catch(e) {
		alert(e);
	}
};

// 支付
CQGAME.sdk.pay = function(data) {
	try {
		data.serverId = CQGAME.server_id;
		var json = JSON.stringify(data);
		console.log("PAY DATA: " + json);
		
		CQGAME.sdk.sdk.pay(json);
	} catch(e) {
		alert(e);
	}
};

// 数据上报
CQGAME.sdk.report = function(data) {
	data.serverId = CQGAME.server_id;
	CQGAME.sdk.sdk.roleReport(JSON.stringify(data));
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
	// alert(info.code);
	if(info.code == 0) {
		// 初始化成功 
		// alert(CQGAME.sdk.sdk.ad_channel);
		if(CQGAME.sdk.sdk.ad_channel && CQGAME.sdk.sdk.ad_channel.length > 0) {
			CQGAME.adChannel = CQGAME.sdk.sdk.ad_channel;
		}

		console.info("初始化成功, adChannel: " + CQGAME.adChannel);
	} else {
		console.error("SDK 初始化失败");
		alert("初始化失败");
	}
}

// 登录回调
function checkLoginCallback(data){
	var info = JSON.parse(data);
	// console.error("LOGIN: " + data);
	if(info.code == 0) {
		console.log('SDK LOGIN SUCCESS, OPENID ' + info.open_id);
		// 登录成功
		CQGAME.openid = info.open_id;
		CQGAME.sid = info.sid;

		CQGAME.startgame();

		CQGAME.sdk.hasReportRole = false;
	} else {
		alert("登录失败");
	}
}

// 登录回调
function logoutCallback(data){
	window.location.reload();
}


// 数据上报回调
function roleReportCallback(data){
	var info = JSON.parse(data);
	// console.error(info);
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