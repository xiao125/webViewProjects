package com.mc.weblib.command;

import android.app.Activity;
import android.content.Context;
import com.mc.weblib.utils.WebConstants;
import com.mc.weblib.interfaces.AidlError;
import com.mc.weblib.interfaces.Command;
import com.mc.weblib.interfaces.ResultBack;
import com.proxy.OpenSDK;
import com.proxy.ResultCode;
import com.proxy.bean.GameInfo;
import com.proxy.callback.SdkCallbackListener;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class CommandsManager {

    private static CommandsManager instance;
    private UIDependencyCommands uiDependencyCommands;
    private BaseLevelCommands baseLevelCommands;
   // private AccountLevelCommands accountLevelCommands;
    private LoginCommands loginCommands;
    private RoleCommands roleCommands;
    private PayCommands payCommands;
    private LogoutCommands logoutCommands;

    private CommandsManager(){
        uiDependencyCommands = new UIDependencyCommands();
        baseLevelCommands = new BaseLevelCommands();
        loginCommands = new LoginCommands();
        roleCommands = new RoleCommands();
        payCommands = new PayCommands();
        logoutCommands = new LogoutCommands();
       // accountLevelCommands = new AccountLevelCommands();
    }


    public static CommandsManager getInstance(){
        if(instance == null){
            synchronized (CommandsManager.class){
                instance = new CommandsManager();
            }
        }
        return instance;
    }

    /**
     * 动态注册command
     * 应用场景：其他模块在使用WebView的时候，需要增加特定的command，动态加进来
     */
    public void registerCommand(int commandLevel, Command command) {
        switch (commandLevel) {
            case WebConstants.LEVEL_UI:
                break;
            case WebConstants.LEVEL_BASE:
                baseLevelCommands.registerCommand(command);
                break;
            case WebConstants.LEVEL_LOGIN:
                loginCommands.registerCommand(command);
                break;
            case WebConstants.LEVEL_ROLE:
                roleCommands.registerCommand(command);
                break;
            case WebConstants.LEVEL_PAY:
                payCommands.registerCommand(command);
                break;
            case WebConstants.LEVEL_LOGOUT:
                logoutCommands.registerCommand(command);
                break;
        }

    }


    /**
     * 非UI Command 的执行
     */
    public void findAndExecNonUICommand(Context context, int level, String action, Map params, ResultBack resultBack){
        boolean methodFlag = false;
        switch (level){
            case WebConstants.LEVEL_BASE:
                if(baseLevelCommands.getCommands().get(action) !=null){
                    methodFlag = true;
                    baseLevelCommands.getCommands().get(action).exec(context,params,resultBack);
                }
                break;
            case WebConstants.LEVEL_LOGIN:
                if(loginCommands.getCommands().get(action) !=null){
                    methodFlag = true;
                    loginCommands.getCommands().get(action).exec(context,params,resultBack);
                }
                break;

            case WebConstants.LEVEL_ROLE:
                if(roleCommands.getCommands().get(action) !=null){
                    methodFlag = true;
                    roleCommands.getCommands().get(action).exec(context,params,resultBack);
                }
                break;
            case WebConstants.LEVEL_PAY:
                if(payCommands.getCommands().get(action) !=null){
                    methodFlag = true;
                    payCommands.getCommands().get(action).exec(context,params,resultBack);
                }
                break;

            case WebConstants.LEVEL_LOGOUT:
                if(logoutCommands.getCommands().get(action) !=null){
                    methodFlag = true;
                    logoutCommands.getCommands().get(action).exec(context,params,resultBack);
                }
                break;
        }

        if(!methodFlag){
            AidlError aidlError = new AidlError(WebConstants.ERRORCODE.NO_METHOD,WebConstants.ERRORMESSAGE.NO_METHOD);
            resultBack.onResult(WebConstants.FAILED,action,aidlError);
        }
    }

    /**
     * UI  Command的执行
     */
    public void findAndExecUICommnad(Context context, int level, String action, Map params, ResultBack resultBack) {
        if(loginCommands.getCommands().get(action) !=null){
            loginCommands.getCommands().get(action).exec(context,params,resultBack);
        }
    }

    public boolean checkHitUICommand(int level,String action){
        return uiDependencyCommands.getCommands().get(action) !=null;
    }



}
