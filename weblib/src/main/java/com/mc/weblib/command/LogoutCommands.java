package com.mc.weblib.command;

import android.content.Context;

import com.mc.weblib.interfaces.Command;
import com.mc.weblib.interfaces.ResultBack;
import com.mc.weblib.utils.WebConstants;

import java.util.Map;

/**
 * 注销
 */

public class LogoutCommands extends Commands {


    public LogoutCommands() {
        registerCommands();
    }

    @Override
    int getCommandLevel() {
        return WebConstants.LEVEL_LOGOUT;
    }

    void registerCommands() {
        registerCommand(LogoutCommand);
    }

    /**
     *
     */
    private final Command LogoutCommand = new Command() {
        @Override
        public String name() {
            return "logout";
        }

        @Override
        public void exec(Context context, Map params, ResultBack resultBack) {
            //初始化成功返回
            resultBack.onResult(WebConstants.LEVEL_LOGOUT, this.name(), params);
        }
    };

}
