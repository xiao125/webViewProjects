package com.game.sdk.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.game.sdk.util.KnLog;

/**
 *
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        KnLog.log("开始广播逻辑");
        String userName = intent.getStringExtra("userName");
        String passWord = intent.getStringExtra("passWord");
        String spName = intent.getStringExtra("spName");
       //启动service
       Intent i = new Intent(context, RemindService.class);
       i.putExtra("userName",userName);
       i.putExtra("passWord",passWord);
       i.putExtra("spName",spName);
       context.startService(i);
    }


}
