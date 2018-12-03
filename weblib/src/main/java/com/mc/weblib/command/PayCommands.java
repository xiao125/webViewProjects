package com.mc.weblib.command;

import android.content.Context;

import com.mc.weblib.interfaces.Command;
import com.mc.weblib.interfaces.ResultBack;
import com.mc.weblib.utils.WebConstants;

import java.util.Map;

/**
 * 支付
 */

public class PayCommands extends Commands {


    public PayCommands() {
        registerCommands();
    }

    @Override
    int getCommandLevel() {
        return WebConstants.LEVEL_PAY;
    }

    void registerCommands() {
        registerCommand(PayCommand);
    }

    /**
     *
     */
    private final Command PayCommand = new Command() {
        @Override
        public String name() {
            return "pay";
        }

        @Override
        public void exec(Context context, Map params, ResultBack resultBack) {
            //初始化成功返回
            resultBack.onResult(WebConstants.LEVEL_PAY, this.name(), params);
        }
    };

}
