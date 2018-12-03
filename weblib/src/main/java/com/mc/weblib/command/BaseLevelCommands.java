package com.mc.weblib.command;

import android.content.Context;
import com.mc.weblib.utils.WebConstants;
import com.mc.weblib.interfaces.Command;
import com.mc.weblib.interfaces.ResultBack;
import java.util.Map;


public class BaseLevelCommands extends Commands {


    public BaseLevelCommands() {
        registerCommands();
    }

    @Override
    int getCommandLevel() {
        return WebConstants.LEVEL_BASE;
    }

    void registerCommands() {
        registerCommand(pageRouterCommand);
    }

    /**
     * 初始化MC中间件
     */
    private final Command pageRouterCommand = new Command() {
        @Override
        public String name() {
            return "Activate";
        }
        @Override
        public void exec(Context context, Map params, ResultBack resultBack) {
            //初始化成功返回
            resultBack.onResult(WebConstants.LEVEL_BASE, this.name(), params);
        }
    };

}
