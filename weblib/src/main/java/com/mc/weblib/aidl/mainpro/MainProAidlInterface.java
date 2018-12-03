package com.mc.weblib.aidl.mainpro;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.mc.weblib.IWebAidlCallback;
import com.mc.weblib.IWebAidlInterface;
import com.mc.weblib.command.CommandsManager;
import com.mc.weblib.interfaces.ResultBack;

import java.util.Map;

/**
 */

public class MainProAidlInterface extends IWebAidlInterface.Stub {

    private Context context;

    public MainProAidlInterface(Context context) {
        this.context = context;
    }

    @Override
    public void handleWebAction(int level, String actionName, String jsonParams, IWebAidlCallback callback) throws RemoteException {
        int pid = android.os.Process.myPid();
        Log.d("webli" , String.format("MainProAidlInterface: 进程ID（%d）， WebView请求（%s）, 参数 （%s）", pid, actionName, jsonParams));

        try {
            handleRemoteAction(level,actionName, new Gson().fromJson(jsonParams, Map.class),callback);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleRemoteAction(int level,final String actionName,Map param,final IWebAidlCallback callback ) throws Exception{
        CommandsManager.getInstance().findAndExecNonUICommand(context, level, actionName, param, new ResultBack() {
            @Override
            public void onResult(int status, String action, Object result) {
                try {
                    if(callback !=null){
                        callback.onResult(status,actionName, new Gson().toJson(result));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

}
