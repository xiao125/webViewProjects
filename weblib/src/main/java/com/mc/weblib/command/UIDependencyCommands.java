package com.mc.weblib.command;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;
import com.mc.weblib.utils.WebConstants;
import com.mc.weblib.utils.WebUtils;
import com.mc.weblib.interfaces.Command;
import com.mc.weblib.interfaces.ResultBack;
import com.mc.weblib.view.X5WebView;

import java.util.List;
import java.util.Map;


public class UIDependencyCommands extends Commands {

    public UIDependencyCommands() {
        super();
        registCommands();
    }

    @Override
    int getCommandLevel() {
        return WebConstants.LEVEL_UI;
    }

    void registCommands() {
        registerCommand(showToastCommand);
        registerCommand(showDialogCommand);
    }

    /**
     * 显示Toast信息
     */
    private final Command showToastCommand = new Command() {
        @Override
        public String name() {
            return "showToast";
        }

        @Override
        public void exec(Context context, Map params, ResultBack resultBack) {
            Toast.makeText(context, String.valueOf(params.get("message")), Toast.LENGTH_SHORT).show();
        }

    };

    /**
     * 对话框显示
     */
    private final Command showDialogCommand = new Command() {
        @Override
        public String name() {
            return "Login";
        }

        @Override
        public void exec(Context context, Map params, final ResultBack resultBack) {

        }


    };
}
