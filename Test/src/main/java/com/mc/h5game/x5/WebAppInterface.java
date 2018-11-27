package com.mc.h5game.x5;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.webkit.JavascriptInterface;
import com.mc.h5game.util.ThreadPoolUtil;
import com.proxy.Constants;
import com.proxy.bean.KnPayInfo;
import com.proxy.util.DeviceUtil;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Webview和Js交互
 */

public class WebAppInterface {

    Context mcontext;
    private String m_platform;
    private String m_appKey ;
    private String m_gameId ;
    private String m_gameName ;
    private int m_screenOrientation = 1;
    private X5WebView mwebView;

    public WebAppInterface(Context c, String gameName, String appKey, int screenOrientation, X5WebView webView){
        this.mcontext =c;
        this.m_gameName = gameName;
        this.m_appKey = appKey;
        this.m_screenOrientation =  screenOrientation;
        this.mwebView = webView;
    }

    public interface wvClientClickListener {
         void wvLogin();
         void wvActivate();
         void wvPay(KnPayInfo knPayInfo);
         void wvRoleReport(Map<String, Object> data);
         void wvU7SystemInfo();
    }

    private wvClientClickListener wvEnventPro = null;
    public void setWvClientClickListener(wvClientClickListener listener) {
        wvEnventPro = listener;
    }

    /**
     * 初始化
     */
    @JavascriptInterface
    public void activate() {
        wvEnventPro.wvActivate();
    }

    /**
     * 登录
     */
    @JavascriptInterface
    public void login() {
        wvEnventPro.wvLogin();
    }

    /**
     * 支付
     * @param
     */
    @JavascriptInterface
    public void pay(String payContent) {
        try {
            JSONObject jsonObject = new JSONObject(payContent);
            String OrderNo = jsonObject.getString("extra_info")!=null?jsonObject.getString("extra_info"):""; // 游戏订单号
            String Price = jsonObject.getString("price")!=null?jsonObject.getString("price"):""; // 商品价格
            String productName = jsonObject.getString("productName")!=null?jsonObject.getString("productName"):"钻石"; // 商品名称
            String CoinName = jsonObject.getString("coinName")!=null?jsonObject.getString("coinName"):"";//货币名称
            String CoinRate = jsonObject.getString("coinRate")!=null?jsonObject.getString("coinRate"):"";//游戏货币的比率
            String ProductId = jsonObject.getString("productId")!=null?jsonObject.getString("productId"):""; //商品id
            String Desc = jsonObject.getString("desc")!=null?jsonObject.getString("desc"):"购买钻石"; //商品描述

            final KnPayInfo payInfo = new KnPayInfo();
            payInfo.setProductName(productName); // 商品名称
            payInfo.setCoinName(CoinName); // 货币名称 如:元宝
            payInfo.setCoinRate(Integer.valueOf(CoinRate)); // 游戏货币的比率
            // 如:1元=10元宝
            // 就传10
            payInfo.setPrice(Double.valueOf(Price) * 100); // 商品价格 分
            payInfo.setProductId(ProductId); // 商品Id，没有填“1"
            payInfo.setOrderNo(OrderNo); // 订单号
            payInfo.setDesc(Desc);
            payInfo.setExtraInfo(OrderNo); //透传默认订单号
            wvEnventPro.wvPay(payInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /** 上报接口
     * js传递过来的参数={"senceType":"1","userId":"1","serverId":"3","userLv":"4",
     * "serverName"
     * :"战","roleName":"xiao","vipLevel":"0","roleCTime":"roleCTime"}
     * @param roleReportContent
     */

    @JavascriptInterface
    public void roleReport(final String roleReportContent) {
       // roleDate = roleReportContent;
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                ((Activity)mcontext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(roleReportContent);
                            LogUtil.log("js传递过来的参数=" + jsonObject.toString());
                            String userId = jsonObject.getString("userId")!=null?jsonObject.getString("userId"):"";// 用户id
                            String serverId = jsonObject.getString("serverId")!=null?jsonObject.getString("serverId"):""; // 服务器Id
                            String gameLv = jsonObject.getString("lv")!=null?jsonObject.getString("lv"):""; // 游戏等级
                            String serverName = jsonObject.getString("serverName")!=null?jsonObject.getString("serverName"):""; // 玩家所在服区名称
                            String roleName = jsonObject.getString("roleName")!=null?jsonObject.getString("roleName"):""; // 游戏角色名称
                            String roleCTime = jsonObject.getString("roleCTime")!=null?jsonObject.getString("roleCTime"):""; // 游戏角色创建时间（时间戳）
                            String vipLevel = jsonObject.getString("vipLevel")!=null?jsonObject.getString("vipLevel"):""; // 玩家VIP等级
                            String user_sex = jsonObject.getString("user_sex")!=null?jsonObject.getString("user_sex"):""; // 玩家性别
                            String user_age = jsonObject.getString("user_age")!=null?jsonObject.getString("user_age"):""; // 玩家年龄
                            String factionName = jsonObject.getString("factionName")!=null?jsonObject.getString("factionName"):""; // 用户所在帮派名称
                            String senceType = jsonObject.getString("senceType")!=null?jsonObject.getString("senceType"):""; // /场景ID(值为1则是进入游戏场景，值为2则是创建角色场景，值为4则是提升等级场景)
                            String diamondLeft = jsonObject.getString("diamondLeft")!=null?jsonObject.getString("diamondLeft"):""; // 玩家货币余额
                            String extraInfo = jsonObject.getString("extraInfo")!=null?jsonObject.getString("extraInfo"):""; // 玩家信息拓展字段

                            Map<String, Object> data = new HashMap<String, Object>();
                            data.put(Constants.USER_ID, userId); // 游戏玩家ID
                            data.put(Constants.SERVER_ID, serverId); // 游戏玩家所在的服务器ID
                            data.put(Constants.USER_LEVEL, gameLv); // 游戏玩家等级
                            data.put(Constants.ROLE_ID, userId); // 角色ID

                            // int senceType =1; //场景ID
                            // String extraInfo = ""; //玩家信息拓展字段
                            // String vipLevell ="0"; //玩家VIP等级
                            // String factionName=""; //用户所在帮派名称
                            // 场景ID;//(值为1则是进入游戏场景，值为2则是创建角色场景，值为4则是提升等级场景)
                            // String diamondLeft = diamondLeft; //玩家货币余额
                            data.put(Constants.EXPEND_INFO, extraInfo); // 扩展字段
                            data.put(Constants.SERVER_NAME, serverName); // 所在服务器名称
                            data.put(Constants.ROLE_NAME, roleName);// 角色名称
                            data.put(Constants.VIP_LEVEL, vipLevel); // VIP等级
                            data.put(Constants.FACTION_NAME, factionName);// 帮派名称
                            data.put(Constants.SCENE_ID, senceType); // 场景ID
                            data.put(Constants.ROLE_CREATE_TIME, roleCTime);// 角色创建时间
                            data.put(Constants.BALANCE, diamondLeft); // 剩余货币
                            data.put(Constants.IS_NEW_ROLE,
                                    Integer.valueOf(senceType) == 2 ? true : false); // 是否是新角色
                            data.put(Constants.USER_ACCOUT_TYPE, "1"); // 玩家账号类型账号类型，0:未知用于来源1:游戏自身注册用户2:新浪微博用户3:QQ用户4:腾讯微博用户5:91用户(String)
                            data.put(Constants.USER_SEX, user_sex); // 玩家性别，0:未知性别1:男性2:女性；(String)
                            data.put(Constants.USER_AGE, user_age); // 玩家年龄；(String)
                            wvEnventPro.wvRoleReport(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    /**
     * 获取android手机设备相关信息
     */
    @JavascriptInterface
    public void u7SystemInfo() {
        String DisplayMetrics = Util.ImageGalleryAdapter(mcontext);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("network", DeviceUtil.getNetWorkType()); //当前网络类型
            jsonObject.put("resolution",DisplayMetrics); //当前手机分辨率
            jsonObject.put("system",Util.getSystemVersion()); //手机系统版本
            jsonObject.put("memory",Util.getTotalMemorySize()); //手机内存大小
            jsonObject.put("imei",DeviceUtil.getDeviceId()); //手机imei
            LogUtil.log("读取到的手机信息：" + jsonObject.toString());
            u7gameSystemInfo(jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    // js接口（返回android手机设备相关信息）
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void u7gameSystemInfo(final String json) {

        if(mwebView!=null){
            mwebView.post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    final int version = Build.VERSION.SDK_INT;
                    if (version < 19) {
                        // 调用js初始化回调
                        mwebView.loadUrl("javascript:u7gameSystemInfo('" + json+ "')");
                    } else { // 该方法在 Android 4.4 版本才可使用，
                        // 调用js初始化回调
                        // 调用js初始化回调
                        mwebView.evaluateJavascript("javascript:u7gameSystemInfo('" + json
                                        + "')",
                                new com.tencent.smtt.sdk.ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        // TODO Auto-generated method stub
                                        LogUtil.log("上报手机信息，返回："+value);
                                    }
                                });
                    }
                }
            });
        }
    }


    // 调用js接口（初始化成功回调方法）
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void activateCallback() {
        LogUtil.log("调用js,初始化回调");
        mwebView.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                final int version = Build.VERSION.SDK_INT;
                if (version < 19) {
                    // 调用js初始化回调
                    mwebView.loadUrl("javascript:activateCallback('"+ getJson().toString() + "')");

                } else { // 该方法在 Android 4.4 版本才可使用，
                    // 调用js初始化回调
                    mwebView.evaluateJavascript("javascript:activateCallback('"+ getJson().toString() + "')",
                            new com.tencent.smtt.sdk.ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {

                                }
                            });
                }
            }
        });
    }

    // 调用js接口（登录回调方法）
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void loginCallback(final String openId, final String sid) {
        LogUtil.log("调用js,登录回调");
        mwebView.post(new Runnable() {
            @Override
            public void run() {
                final int version = Build.VERSION.SDK_INT;
                if (version < 19) {
                    // 调用js初始化回调
                    mwebView.loadUrl("javascript:loginCallback('" + getJson(openId,sid) + "')");
                } else { // 该方法在 Android 4.4 版本才可使用，
                    // 调用js初始化回调
                    mwebView.evaluateJavascript("javascript:loginCallback('"
                                    + getJson(openId,sid) + "')",
                            new com.tencent.smtt.sdk.ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {

                                }
                            });
                }
            }
        });
    }


    // 调用js接口（上报回调方法）,目前不需要此方法
    public void roleDataCallback(final String data) {
        LogUtil.log("调用js,上报回调");
        mwebView.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                final int version = Build.VERSION.SDK_INT;
                if (version < 19) {
                    mwebView.loadUrl("javascript:roleReportCallback('" + data + "')");
                } else { // 该方法在 Android 4.4 版本才可使用，
                    //主线程调用（ java.lang.IllegalStateException: Calling View methods on another thread than the UI thread.））
                    // 调用js初始化回调
                    mwebView.evaluateJavascript(
                            "javascript:roleReportCallback('" + data+"')",
                            new com.tencent.smtt.sdk.ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                }
                            });
                }
            }
        });
    }

    // js接口（游戏回到登录界面）
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void logoutCallback() {
        LogUtil.log("调用js,登出回调");
        mwebView.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                final int version = Build.VERSION.SDK_INT;
                if (version < 19) {
                    // 调用js初始化回调
                    mwebView.loadUrl("javascript:logoutCallback()");
                } else { // 该方法在 Android 4.4 版本才可使用，

                    mwebView.clearHistory(); // 清除
                    // 调用js初始化回调
                    mwebView.evaluateJavascript("javascript:logoutCallback()",
                            new com.tencent.smtt.sdk.ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    // TODO Auto-generated method stub
                                    LogUtil.log("注销sdk游戏账号11111111");
                                }
                            });
                }
            }
        });
    }

    // js接口（支付回调）
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void payCallback() {
        LogUtil.log("调用js,支付回调");
        mwebView.post(new Runnable() {
            @Override
            public void run() {
                final int version = Build.VERSION.SDK_INT;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("reason", "支付成功");
                    jsonObject.put("code", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (version < 19) {
                    // 调用js支付回调
                    mwebView.loadUrl("javascript:payCallback('" + jsonObject.toString()
                            + "')");
                } else { // 该方法在 Android 4.4 版本才可使用，
                    // 调用js初始化回调
                    mwebView.evaluateJavascript(
                            "javascript:payCallback('" + jsonObject.toString() + "')",
                            new com.tencent.smtt.sdk.ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    // TODO Auto-generated method stub
                                }
                            });
                }
            }
        });
    }


    // sdk登录成功返回的对象数据
    @SuppressLint("NewApi")
    private JSONObject getJson(String openId,String sid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("open_id",openId);
            jsonObject.put("sid", sid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.log("返回json数据："+jsonObject);
        return jsonObject;
    }

    // 初始化成功返回的对象数据
    private JSONObject getJson() {
        Map<String, String> json = Util.readHttpData(mcontext);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("reason", "初始化成功");
            jsonObject.put("code", 0);
            jsonObject.put("ad_channel",json.get("adchannel"));
            jsonObject.put("channel",json.get("channel"));
            jsonObject.put("imei",DeviceUtil.getDeviceId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


}
