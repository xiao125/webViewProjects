(function urlParams() {
	var name, value;
	var str = location.href; //取得整个地址栏
	var num = str.indexOf("?")
	str = str.substr(num + 1);
	var arr = str.split("&"); //各个参数放到数组里
	for (var i = 0; i < arr.length; i++) {
		num = arr[i].indexOf("=");
		if (num > 0) {
			name = arr[i].substring(0, num);
			value = arr[i].substr(num + 1);
			window[name] = value;
		}
	}
})();



var CQGAME = {};

try{
	CQGAME.storage = localStorage.CQGAME ? JSON.parse(localStorage.CQGAME) : {};
	if(!CQGAME.storage) CQGAME.storage = {};
} catch(e) {
	CQGAME.storage = {};
}

CQGAME.env = CQGAME_ENV;
CQGAME.channel = window.serverChannel || CQGAME_CHANNEL;

CQGAME.appid = 1025;
CQGAME.game = "cqsjh5";
CQGAME.channelid = 1028;
CQGAME.adChannel = 1;
// CQGAME.channel = "inside";
CQGAME.openid = null;
CQGAME.sid = null;
CQGAME.uid = "";	// 要进服之后才会有这个值
CQGAME.server_id = 0;
CQGAME.server_name = "";
CQGAME.serverList = {};
CQGAME.imei = "";
CQGAME.hasLogin = false;
CQGAME.gameObj = null;
CQGAME.statlogurls = [];
CQGAME.unique_id = window.S_TIME + "-" + Math.floor(Math.random() * 10000);
CQGAME.clientVersion = "trunk";		// 不同服，客户端版本

// load
CQGAME.loadstatus = {};
CQGAME.preloaded = 0;
CQGAME.loaded = 0;
CQGAME.max = 0;
CQGAME.preloadlist = [
	"common/libs/all_v2.libs.min.js",
	"common/libs/sproto.min.js"
];

if(CQGAME.env == "develop") {	
	CQGAME.preloadlist = [
		"libs/min/zlib.min.js",
		"libs/max/laya.core.js",
		"libs/max/laya.webgl.js",
		"libs/max/laya.ani.js",
		"libs/max/laya.filter.js",
		"libs/max/laya.ui.js",
		"libs/max/laya.html.js",
		"libs/min/utils.min.js",
		"libs/min/pool.min.js",
		// "../js/min/clipboard.min.js",
		// "libs/min/Tween1.min.js",
		// "libs/max/vconsole.js",
		// "libs/max/laya.debugtool.js",
		// "libs/min/eruda.min.js",
		
		"common/libs/sproto.min.js"
	];
}

CQGAME.reslist = [
	"js/game.js"
];

// 修改本地存储
CQGAME.setStorage = function(key , value) {
	CQGAME.storage[key] = value;
	localStorage.CQGAME = JSON.stringify(CQGAME.storage);
}



CQGAME.init = function() {
	window.GAME_RES_PATH = "";

	CQGAME.openid = null;
	CQGAME.sid = null;
	CQGAME.server_id = 0;
	CQGAME.server_name = "";
	CQGAME.serverList = {};
	CQGAME.hasLogin = false;
	CQGAME.gameObj = null;

	if(CQGAME.sdk) {
		CQGAME.sdk.init();
	}

	// 显示登录界面
	CQGAME.show();
}


// 初始界面
CQGAME.show = function() {
	CQGAME.hasLogin = false;

	if(CQGAME.sdk) {
		// sdk.new
		return;
	}

	CQGAME.showLoginAccountView();
}



// 显示账号界面
CQGAME.showLoginAccountView = function() {
	if(CQGAME.storage.openid) CQGAME.openid = CQGAME.storage.openid;

	Zepto("#login_account_div , #btn_start_game").show();
	Zepto("#choose_server_div , #btn_notice").hide();

	Zepto("#btn_startgame").attr("src" , "common/images/btn_login.png");
	Zepto("#start_game_div").show();

	Zepto("#login_openid").text(CQGAME.openid);
}

CQGAME.changeOpenid = function() {
	var openid = prompt("账号");
	if(openid && openid.length > 0 && !openid.match(/[a-zA-Z0-9_\.]+/g)) {
		alert("账号不合法，只允许字母数字和下划线");
		return;
	}
	CQGAME.openid = openid != null && openid != "" ? openid : CQGAME.openid;

	Zepto("#login_openid").text(CQGAME.openid);
}


// 开始游戏
CQGAME.startgame = function() {
	if(CQGAME.hasLogin && CQGAME.server_id && CQGAME.server_id > 0 && CQGAME.openid && CQGAME.openid.length > 0) {
		// 这里设置获取该服对应的客户端版本路径
		// window.GAME_RES_PATH = "";
		var server_info = CQGAME.serverList[CQGAME.server_id];
		CQGAME.server_name = server_info["name"];
		if(server_info && server_info["clientVersion"] && server_info["clientVersion"].length > 0) {
			CQGAME.clientVersion = server_info["clientVersion"];
		}

		// 设置游戏内资源路径
		if(CQGAME.env == "release" || CQGAME.env == "preview") {
			var path = window.location.href.substring(0 , window.location.href.lastIndexOf("/") + 1);
			window.GAME_RES_PATH = path + CQGAME.clientVersion + "/";
		}

		// 兼容最老的版本
		if(CQGAME.clientVersion == "v2") {
			CQGAME.reslist.unshift("libs/min/sproto.min.js");
		}

		console.log("GAME_RES_PATH: " + window.GAME_RES_PATH);

		// 进入游戏
		CQGAME.setStorage("server_id" , CQGAME.server_id);

		// 开始loading
		CQGAME.loading();

		CQGAME.statlog("show_loadingview");

		document.title = CQGAME.server_name + " | " + document.title;
	} else {
		if(CQGAME.sdk) {
			if(!CQGAME.openid || CQGAME.openid == "" || !CQGAME.sid || CQGAME.sid == "") {
				console.error("NO OPENID OR SID");
				window.location.reload();
			}
		} else {
			if(!CQGAME.openid || CQGAME.openid == "") {
				alert("没有填写账号");
				return;
			}

			CQGAME.setStorage("openid" , CQGAME.openid);
		}

		CQGAME.hasLogin = true;

		Zepto("#login_account_div").hide();
		Zepto("#choose_server_div").show();
		Zepto("#start_game_div").show();
		Zepto("#btn_startgame").attr("src" , "common/images/btn_start.png");

		// 拉服务器列表
		CQGAME.getServerList();
		CQGAME.getNotice();
		// CQGAME.loading();

		CQGAME.showLoginBtnEffect();

		CQGAME.statlog("show_loginview");

		// 开始预加载脚本
		setTimeout(CQGAME.preLoadScript , 1);
	}
}

// 服务器列表
CQGAME.getServerList = function() {
	Zepto("#selected_server").html('<span style="font-size:0.8rem">服务器列表加载中...</span>');
	Zepto("#btn_xuanqu").hide();

	var url = "//data.u7game.cn/api.php?m=servers&game=" + CQGAME.game + "&channel=" + CQGAME.channel + "&channelid=" + CQGAME.adChannel + "&openid=" + CQGAME.openid + "&r=" + window.S_TIME;
	console.info(url);
	Zepto.ajax({
		url : url,
		type : 'get',
		dataType : 'jsonp',
		jsonp : "callback",
		jsonpCallback : "jsonpCallback_ServerList",
		headers : {'Accept-Encoding' : 'gzip,deflate'},
		cache:true,
		success : function(data) {
			Zepto("#selected_server").html("");
			Zepto("#btn_xuanqu").show();

			// 服务器分组
			CQGAME.serverGroupList = {};

			var servers = data.servers;
			for(var i in servers) {
				// {"server_id":100,"port":8888,"name":"名字","id":1,"status":1,"ip":"xx.xx.xx.xx"}
				var server_info = servers[i];
				CQGAME.serverList[server_info.server_id] = server_info;

				// 分组
				var type = "index";
				if(server_info.name.indexOf("预发布") == 0) type = "preview";
				else if(server_info.name.indexOf("测试 -") == 0) type = "test";
				else if(server_info.name.indexOf("私服 -") == 0) {
					type = "private";
					// CQGAME.serverList[server_info.server_id]["clientVersion"] = "beta5.0_pre";
				}
				else if(server_info.name.match(/.+? - [0-9]+服/)) type = "develop";

				if(!CQGAME.serverGroupList[type]) {
					CQGAME.serverGroupList[type] = [];

					if(type != "index") Zepto(".cate").append('<b id="server_' + type + '" data="' + type + '">' + type +'</b>');
				}
				CQGAME.serverGroupList[type].push(server_info);
			}

			// 点击事件
			Zepto(".cate > b").click(function() {
				CQGAME.showServerList(Zepto(this).attr("data"));
			});
			CQGAME.showServerList("index");

			// 显示默认服务器
			var default_server_id = 0;
			if(CQGAME.storage.server_id > 0 && CQGAME.serverList[CQGAME.storage.server_id]) {
				default_server_id = CQGAME.storage.server_id;
				console.log("select last server: " + default_server_id);
			} else {
				for(var i in CQGAME.serverList) {
					if(CQGAME.serverList[i].status == 1) {
						default_server_id = i;
						console.log("select default server: " + default_server_id);
						break;
					}
				}
			}

			if(default_server_id > 0) {
				CQGAME.chooseServer(default_server_id);

				// 第一次打开app，自动进服
				var first_login = CQGAME.storage.first_login;
				if(CQGAME.env != "develop" && !first_login) {
					CQGAME.setStorage("first_login" , 1);
					CQGAME.startgame();
				}
			}
		},
		error : function() {
			console.error("获取服务器列表失败");
			Zepto("#selected_server").html('<span style="font-size:0.8rem">获取服务器列表失败</span>');
		}
	});
}

CQGAME.showServerList = function(type) {
	if(!type) type = "index";

	Zepto(".cate > b").removeClass("cur");
	Zepto("#server_" + type).addClass("cur");

	var list = CQGAME.serverGroupList[type] || [];

	// 按开服时间和server_id排序
	var sorted_serverList = [];
	sorted_serverList = list.sort(function(a , b) {
		if(a.startTime > b.startTime) return -1;
		else if(a.startTime < b.startTime) return 1;
		else {
			if(a.server_id > b.server_id) return -1;
			else if(a.server_id < b.server_id) return 1;
			else return 0;
		}
	});

	Zepto("#serverList").html("");
	for(var i = 0 ; i < sorted_serverList.length; i++) {
		var server_id = sorted_serverList[i].server_id;
		// {"server_id":100,"port":8888,"name":"名字","id":1,"status":1,"ip":"xx.xx.xx.xx"}
		var server_info = CQGAME.serverList[server_id];
		var status_image = "new.png";
		if(server_info.status == 1) status_image = "rec.png";
		else if(server_info.status == 2) status_image = "hot.png";
		else if(server_info.status == 3) status_image = "full.png";
		else if(server_info.status == 4) status_image = "maintain.png";
		Zepto("#serverList").append('<b server_id="' + server_info.server_id + '"><img src="common/images/' + status_image + '" class="statusImg">' + server_info.name + '</b>');
	}
	Zepto("#serverList > b").click(function() {
		CQGAME.chooseServer(Zepto(this).attr("server_id"));
	});
}

CQGAME.showMyServers = function() {
	Zepto(".cate > b").removeClass("cur");
	Zepto("#server_my").addClass("cur");

	Zepto("#serverList").html("");
}


// 选服
CQGAME.chooseServer = function(server_id) {
	if(!CQGAME.serverList[server_id]) {
		alert("服务器不存在");
		return;
	}
	
	CQGAME.server_id = server_id;	

	var server_info = CQGAME.serverList[server_id];
	Zepto("#selected_server").text(server_info.name);
	Zepto("#btn_xuanqu_close").click();
}


// 公告
CQGAME.getNotice = function() {
	Zepto("#btn_xuanqu").hide();

	var url = "//data.u7game.cn/api.php";
	var params = "a=index&app_id=" + CQGAME.appid + "&channel=" + CQGAME.adChannel + "&game=" + CQGAME.game + "&m=notice&r=" + window.S_TIME + "&serverid=1";
	var sign = hex_md5(params + "&app_secret=e39608427a998d9fd7d2b938c21f5abd");
	url = url + "?" + params + "&sign=" + sign;
	console.info(url);

	Zepto.ajax({
		url : url,
		type : 'get',
		dataType : 'jsonp',
		jsonp : "callback",
		jsonpCallback : "jsonpCallback_Notice",
		headers : {'Accept-Encoding' : 'gzip,deflate'},
		cache:true,
		success : function(data) {
			var notice = data.notice;
			if(!notice) return;

			Zepto("#btn_notice").show();

			var html = "";
			for(var i = 0 ; i < notice.length ; i++) {
					html += '<p>' + notice[i].title + '</p>';
					html += '<p>' + notice[i].content.join('<p></p>') + '</p>';
			}

			Zepto("#notice_content").html(html);

			

			// 判断是否需要自动弹开
			var now = Date.now();
			var last_notice_time = CQGAME.storage.notice_time;
			if(!last_notice_time || (now - last_notice_time >= 3600 * 1000)) {
				CQGAME.setStorage('notice_time' , now);

				// 第一次不弹
				
				if(last_notice_time) {
					Zepto("#btn_notice").click();
				}
			}

			console.info("Notice Diff Time: " + (now - last_notice_time));
		},
		error : function() {
			console.error("获取公告失败");
		}
	});
}


// 加载
CQGAME.loading = function() {
	// 清除登录按钮特效
	if(CQGAME.loginEffectTimer) {
		clearInterval(CQGAME.loginEffectTimer);
		CQGAME.loginEffectTimer = null;
	}

	Zepto(".noticeBox , .serverBox , .btnbox , #btn_notice").hide();
	Zepto("#loadWrap").show();

	CQGAME.max = CQGAME.reslist.length;
	CQGAME.loaded = 0;

	CQGAME.loadVersion();
	CQGAME.showLoadingBar();
}

// 预加载脚本
CQGAME.preLoadScript = function() {
	Zepto("#loading_status").text("加载库文件......");

	// CQGAME.setLoadingBar(CQGAME.loaded + CQGAME.preloaded , CQGAME.reslist.length + CQGAME.preloadlist.length);

	// 修改文件版本号
	var url = CQGAME.preloadlist[CQGAME.preloaded];

	// 挨个加载文件
	Zepto.getScript(url , function() {
		CQGAME.preloaded++;
		// CQGAME.setLoadingBar(CQGAME.loaded + CQGAME.preloaded , CQGAME.reslist.length + CQGAME.preloadlist.length);
		
		if(CQGAME.preloaded >= CQGAME.preloadlist.length) {
			CQGAME.loadstatus["preload_finish"] = true;

			CQGAME.statlog("preload_finish");

			// 判断是否已经选服进入游戏
			if(CQGAME.loadstatus["load_begin"]) {
				CQGAME.loadVersion();
			}
			return;
		}

		CQGAME.preLoadScript();
	} , function() {
		console.error("load script error.");
	});

	console.log("preload script " + CQGAME.preloadlist[CQGAME.preloaded]);
}

// 加载游戏脚本
CQGAME.loadScript = function() {
	Zepto("#loading_status").text("加载游戏脚本....");
	// CQGAME.setLoadingBar(CQGAME.loaded + CQGAME.preloaded , CQGAME.reslist.length + CQGAME.preloadlist.length);

	// 修改文件版本号
	var url = CQGAME.reslist[CQGAME.loaded];
	if(SCRIPT_VERSIONS && SCRIPT_VERSIONS[url]) {
		var ver = SCRIPT_VERSIONS[url];
		if(url.indexOf("js/") == 0) {
			url = ver + "/" + url;
		} else {
			url = url + "?v=" + ver;
		}

		// url = window.GAME_RES_PATH + ver + "/" + url;
	}

	// 挨个加载文件
	url = window.GAME_RES_PATH + url;
	Zepto.getScript(url , function() {
		CQGAME.loaded++;
		CQGAME.setLoadingBar(CQGAME.loaded + CQGAME.preloaded , CQGAME.reslist.length + CQGAME.preloadlist.length);

		if(CQGAME.loaded >= CQGAME.max) {
			CQGAME.loadComplete();
			return;
		}

		CQGAME.loadScript();
	} , function() {
		console.error("load script error.");
	});

	console.log("load script " + CQGAME.reslist[CQGAME.loaded]);
}

CQGAME.loadVersion = function() {
	CQGAME.loadstatus["load_begin"] = true;
	

	// 判断 preload 是否完成
	if(!CQGAME.loadstatus["preload_finish"]) return;

	Zepto("#loading_status").text("加载版本文件....");
	var url = window.GAME_RES_PATH + "ver.js?r=" + window.S_TIME;
	Zepto.getScript(url , function() {
		if(SCRIPT_VERSIONS['VER'] && SCRIPT_VERSIONS['TIME']) {
			Zepto("#version_div").text('VER: ' + CQGAME.clientVersion + '/' + SCRIPT_VERSIONS['VER'] + ' , ' + SCRIPT_VERSIONS['TIME']);
		}

		CQGAME.loadScript();
	} , function() {
		console.error("load ver.js error.");
	});

	console.info("load ver.js");
}

CQGAME.loadComplete = function() {
	Zepto('#cg_bg').hide();
	// Zepto('#main').hide();
	Zepto('body').css('background-image' , "none");

	// 开始游戏脚本
	require(['src/GameMain'] , function(args) {
		CQGAME.gameObj = new args.GameMain();
		CQGAME.gameObj.loadInitResources();
	});

	CQGAME.statlog("loading_gamejs_finish");
}

// 平滑进度条
CQGAME.showLoadingBar = function() {
	CQGAME.loadingShowRate = 0;
	CQGAME.loadingCur = 0;
	CQGAME.loadingMax = 10000;
	CQGAME.loadingExceedTimes = -1;

	var time = 5000;
	var power = CQGAME.loadingMax / time;
	var interval = 33;
	CQGAME._loadingTimer = setInterval(function() {
		var rate = (CQGAME.loadingCur / CQGAME.loadingMax) * 100;
		rate = Math.round(rate * Math.pow(10 , 2)) / Math.pow(10 , 2);
		CQGAME.showLoadingProgress(rate);

		CQGAME.loadingShowRate = rate;

		CQGAME.loadingCur = CQGAME.loadingCur + (power * interval);
		if(CQGAME.loadingCur >= CQGAME.loadingMax) {
			// clearInterval(CQGAME._loadingTimer);0
			CQGAME.loadingCur = CQGAME.loadingMax;

			// 停留3秒，如果还没进游戏，则从0开始重新显示进度条
			CQGAME.loadingExceedTimes++;
			if(CQGAME.loadingExceedTimes >= (3000 / interval)) {
				CQGAME.loadingCur = 0;
				CQGAME.loadingExceedTimes = 0;
			}
		}
	} , interval);
}

CQGAME.setLoadingBar = function(cur , max) {
	// 最多显示到75%
	max = max + 1;
	
	var rate = (cur / max) * 100;
	rate = Math.round(rate * Math.pow(10 , 2)) / Math.pow(10 , 2);
	// Zepto("#load_progress_bar").width(rate + "%");
	// Zepto("#load_progress_num").text(rate);

	if(rate > CQGAME.loadingShowRate && CQGAME.loadingExceedTimes == -1) {
		CQGAME.loadingShowRate = rate;
		CQGAME.loadingCur = Math.floor(CQGAME.loadingMax * CQGAME.loadingShowRate / 100);
		CQGAME.showLoadingProgress(CQGAME.loadingShowRate);
	}

	CQGAME.loadingPreRate = rate;

	// console.error("rate: " + rate + " , max: " + max + " , cur: " + cur);
}

CQGAME.setGameLoadingBar = function(cur , max) {
	Zepto("#loading_status").text("加载游戏资源....");

	var rate = (cur / max) * 100;
	rate = Math.round(rate * Math.pow(10 , 2)) / Math.pow(10 , 2);
	// Zepto("#load_progress_bar").width(rate + "%");
	// Zepto("#load_progress_num").text(rate);

	// 从上一段的开始
	rate = CQGAME.loadingPreRate + (100 - CQGAME.loadingPreRate) * (rate / 100);
	if(rate > CQGAME.loadingShowRate && CQGAME.loadingExceedTimes == -1) {
		CQGAME.loadingShowRate = rate;
		CQGAME.loadingCur = Math.floor(CQGAME.loadingMax * CQGAME.loadingShowRate / 100);
		CQGAME.showLoadingProgress(CQGAME.loadingShowRate);
	}

	// console.error("rate2: " + rate + " , max: " + max + " , cur: " + cur);

	if(cur == max) {
		Zepto("#loading_status").text("加载完成，进入游戏");

		if(CQGAME._loadingTimer) {
			clearInterval(CQGAME._loadingTimer);
		}
	
		CQGAME.showLoadingProgress(100);
		
		setTimeout(function() {
			CQGAME.loadAllComplete();
		} , 50);
	}
}

CQGAME.showLoadingProgress = function(rate) {
	Zepto("#load_progress_bar").width(rate + "%");
	Zepto("#load_progress_num").text(rate.toFixed(2));
}

CQGAME.loadAllComplete = function() {
	Zepto('#cg_bg').hide();
	Zepto('#main').hide();
	Zepto('body').css('background-image' , "none");

	var server_info = CQGAME.serverList[CQGAME.server_id];
	var openid = CQGAME.openid;
	var sid = CQGAME.sid || "";

	CQGAME.statlog("loading_all_finish");
	
	CQGAME.gameObj.enterGame(openid , sid , server_info.ip , server_info.port , server_info.server_id);

	// 为了节省GPU，删除页面的main节点
	setTimeout(function() {
		Zepto("#main").remove();
	} , 5000);
}


// 登录按钮特效
CQGAME.showLoginBtnEffect = function() {
	var index = 0;
	CQGAME.loginEffectTimer = setInterval(function() {
		var pos = Zepto("#btn_startgame").position();
		var btn_width = Zepto("#btn_startgame").width();
		Zepto("#btn_startgame_effect").attr('src' , "common/images/btn_effect/" + index + ".png").css("width" , "60%");
		Zepto("#btn_startgame_effect").css({
			top: pos.top,
			left: pos.left - (Zepto("#btn_startgame_effect").width() - btn_width) / 2
		});

		index++;
		if(index > 10) index = 0;
	} , 80);	
}

// 埋点
CQGAME.statlog = function(point , report , used_time) {
	if(!(CQGAME.env == "release" || CQGAME.env == "preview")) return;

	if(report === false) {
		CQGAME.statlogurls.push({point:point , used_time:used_time});
		return;
	}

	if(CQGAME.statlogurls.length > 0) {
		for(var i = 0 ; i < CQGAME.statlogurls.length ; i++) {
			CQGAME._statlog(CQGAME.statlogurls[i].point , CQGAME.statlogurls[i].used_time);
		}
		CQGAME.statlogurls = [];
	}

	CQGAME._statlog(point , used_time);
}

CQGAME._statlog = function(point , used_time) {
	var time = Date.now();
	var openid = CQGAME.openid || "";
	var server_id = CQGAME.server_id || 0;
	var imei = CQGAME.imei || "";
	var uid = CQGAME.uid || 0;
	if(used_time == undefined) {
		used_time = time - window.S_TIME;
	}

	var url = "//data.u7game.cn/api.php";
	var params_array = [
		["a" , "record"],
		["ad_channel" , CQGAME.adChannel],
		["app_id" , CQGAME.appid],
		["game" , CQGAME.game],
		["imei" , imei],
		["m" , "statlog"],
		["openid" , openid],
		["point" , point],
		["server_id" , server_id],
		["time" , time],
		["ua" , window.navigator.userAgent],
		["uid" , uid],
		["unique_id" , CQGAME.unique_id],
		["used_time" , used_time],
	];
	var params = [];
	var sign_url = [];
	for(var i = 0 ; i < params_array.length ; i++) {
		params.push(params_array[i][0] + "=" + encodeURIComponent(params_array[i][1]));
		sign_url.push(params_array[i][0] + "=" + params_array[i][1]);
	}

	var sign = hex_md5(sign_url.join("&") + "&app_secret=e39608427a998d9fd7d2b938c21f5abd");
	url = url + "?" + params.join("&") + "&sign=" + sign;

	Zepto("body").append('<img src="' + url + '" style="display:none" onerror="Zepto(this).remove()" />');
	console.log("GAMESTAT: " + url);
}

// ===============================
Zepto(document).ready(function() {
	window.isPC = navigator.userAgent.toLowerCase().indexOf('windows') > -1;
	window.tryCatchLayaError = !isPC ? true : false;
	console.info("is PC: " + isPC);

	window.tryCatchLayaError = true;

	// 禁止滑动
	// if(!window.isPC) {
		// Zepto("body").css({"overflow":"hidden",'position':'fixed'});
		// $(document).on('touchmove', function(e){
		// 	e.preventDefault();
		// });
	// }

	// 设置游戏主目录
	// window.GAME_RES_PATH = document.getElementById("baseurl").href;

	// 选区
	Zepto("#choose_server_div").click(function() {
		// Zepto(".serverBox").show();
		Zepto("#cg_bg").show();

		Zepto(".serverBox").css("transform","scale(0)").show().animate({
			scale: 1
		}, 100, "ease-out");
	});
	
	Zepto("#btn_xuanqu_close").click(function() {
		// Zepto(".serverBox").hide();
		Zepto("#cg_bg").hide();

		Zepto(".serverBox").animate({
			scale: 0
		}, 100, "ease-out" , function() {
			Zepto(".serverBox").hide()
		});
	});

	// 公告
	Zepto("#btn_notice").click(function() {
		if(!Zepto("#notice_banner").attr("src")) Zepto("#notice_banner").attr("src" , "common/images/notice_banner.jpg");
		Zepto("#cg_bg").show();

		Zepto(".noticeBox").css("transform","scale(0)").show().animate({
			scale: 1
		}, 100, "ease-out");
	});

	Zepto("#btn_notice_close").click(function() {
		// Zepto(".noticeBox").hide();
		Zepto("#cg_bg").hide();

		Zepto(".noticeBox").animate({
			scale: 0
		}, 100, "ease-out" , function() {
			Zepto(".noticeBox").hide()
		});
	});


	// 开始游戏
	Zepto("#btn_startgame").click(CQGAME.startgame);
	Zepto("#btn_startgame_effect").click(function(event) {
		Zepto("#btn_startgame").click();
		event.stopPropagation();
	});
	Zepto("#btn_change_openid").click(CQGAME.changeOpenid);

	// 显示界面
	CQGAME.init();

	
	if(CQGAME.env != "release") {
		Zepto("body").append('<div id="env_div" style="position: absolute; right: 0; top: 0; color: #fff; font-size:10px; z-index: 999">' + CQGAME.env + '</div>');
	}
});



// 错误上报
window.errorReporterTimer = null;
window.errorReporterTimes = 0;
window.onerror = function(msg , url , line , column , detail) {
	if(CQGAME.env == "develop") return;

	// 每30秒，最多上报5个错误
	window.errorReporterTimes++;
	if(window.errorReporterTimes > 5) {
		return;
	}

	if(window.errorReporterTimer) {
		clearTimeout(window.errorReporterTimer);
		window.errorReporterTimer = null;
	}
	setTimeout(function() {
		window.errorReporterTimes = 0;
		window.errorReporterTimer = null;
	} , 30000);

	

	// line , column 可能不存在
	var server_id = CQGAME.server_id || 0;
	var server_name = CQGAME.server_name || "";
	var openid = CQGAME.openid || "";
	var clientVersion = CQGAME.clientVersion || "";

	var query = "serverId=" + server_id + "&server_name=" + encodeURIComponent(server_name) + "&openid=" + openid + "&clientVersion=" + encodeURIComponent(clientVersion);
	query += "&msg=" + encodeURIComponent(msg) + "&stack=" + encodeURIComponent(detail) + "&times=" + window.errorReporterTimes;
	Zepto("body").append('<img src="//data.u7game.cn/h5cq/errorlog.php?' + query + '" style="display:none" onerror="Zepto(this).remove()" />');
}