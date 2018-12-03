package com.mc.weblib.command;

import android.content.Context;

import com.mc.weblib.interfaces.Command;
import com.mc.weblib.interfaces.ResultBack;
import com.mc.weblib.utils.WebConstants;

import java.util.Map;

/**
 * Created by Administrator on 2018/12/1 0001.
 */

public class RoleCommands extends Commands {


    public RoleCommands() {
        registerCommands();
    }

    @Override
    int getCommandLevel() {
        return WebConstants.LEVEL_ROLE;
    }

    void registerCommands() {
        registerCommand(RoleCommand);
    }

    /**
     *
     */
    private final Command RoleCommand = new Command() {
        @Override
        public String name() {
            return "role";
        }

        @Override
        public void exec(Context context, Map params, ResultBack resultBack) {
            //初始化成功返回
            resultBack.onResult(WebConstants.LEVEL_ROLE, this.name(), params);
        }
    };

}
