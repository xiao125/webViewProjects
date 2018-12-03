package com.mc.weblib.command;


import android.app.Activity;
import android.content.Context;

import com.mc.weblib.interfaces.Command;
import com.proxy.OpenSDK;
import com.proxy.bean.GameInfo;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;

import java.util.HashMap;
import java.util.Map;

public abstract class Commands {

    private HashMap<String, Command> commands;

    abstract int getCommandLevel();

    public HashMap<String, Command> getCommands() {
        return commands;
    }

    public Commands() {
        commands = new HashMap<>();
    }

    protected void registerCommand(Command command) {
        commands.put(command.name(), command);
    }



}


