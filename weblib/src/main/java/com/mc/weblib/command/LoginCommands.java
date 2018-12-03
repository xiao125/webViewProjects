package com.mc.weblib.command;

import android.content.Context;

import com.mc.weblib.interfaces.Command;
import com.mc.weblib.interfaces.ResultBack;
import com.mc.weblib.utils.WebConstants;

import java.util.Map;

/**
 * 登录
 */

public class LoginCommands extends Commands {


    public LoginCommands() {
        registerCommands();
    }

    @Override
    int getCommandLevel() {
        return WebConstants.LEVEL_LOGIN;
    }

    void registerCommands() {
        registerCommand(LoginCommand);
    }

    /**
     *
     */
    private final Command LoginCommand = new Command() {
        @Override
        public String name() {
            return "login";
        }

        @Override
        public void exec(Context context, Map params, ResultBack resultBack) {
            //初始化成功返回
            resultBack.onResult(WebConstants.LEVEL_LOGIN, this.name(), params);

        }
    };

}