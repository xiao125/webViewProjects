package com.mc.weblib.command;

import android.content.Context;
import android.text.TextUtils;
import com.mc.weblib.utils.WebConstants;
import com.mc.weblib.interfaces.AidlError;
import com.mc.weblib.interfaces.Command;
import com.mc.weblib.interfaces.ResultBack;
import com.mc.weblib.view.X5WebView;

import java.util.HashMap;
import java.util.Map;

public class AccountLevelCommands extends Commands {


    public AccountLevelCommands() {
        registerCommands();
    }

    void registerCommands() {
        registerCommand(appDataProviderCommand);
    }

    @Override
    int getCommandLevel() {
        return WebConstants.LEVEL_ACCOUNT;
    }

    // 获取native data
    private final Command appDataProviderCommand = new Command() {
        @Override
        public String name() {
            return "appDataProvider";
        }

        @Override
        public void exec(Context context, Map params, ResultBack resultBack) {
            try {
                String callbackName = "";
                if (params.get("type") == null) {
                    AidlError aidlError = new AidlError(WebConstants.ERRORCODE.ERROR_PARAM, WebConstants.ERRORMESSAGE.ERROR_PARAM);
                    resultBack.onResult(WebConstants.FAILED, this.name(), aidlError);
                    return;
                }
                if (params.get(WebConstants.WEB2NATIVE_CALLBACk) != null) {
                    callbackName = params.get(WebConstants.WEB2NATIVE_CALLBACk).toString();
                }
                String type = params.get("type").toString();
                HashMap map = new HashMap();
                switch (type) {
                    case "account":
                        map.put("accountId", "test123456");
                        map.put("accountName", "xud");
                        break;
                }
                if (!TextUtils.isEmpty(callbackName)) {
                    map.put(WebConstants.NATIVE2WEB_CALLBACK, callbackName);
                }
                resultBack.onResult(WebConstants.SUCCESS, this.name(), map);
            } catch (Exception e) {
                e.printStackTrace();
                AidlError aidlError = new AidlError(WebConstants.ERRORCODE.ERROR_PARAM, WebConstants.ERRORMESSAGE.ERROR_PARAM);
                resultBack.onResult(WebConstants.FAILED, this.name(), aidlError);
            }
        }


    };
}
