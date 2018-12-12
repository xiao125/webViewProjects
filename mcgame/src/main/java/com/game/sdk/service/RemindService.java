package com.game.sdk.service;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import com.game.sdk.activity.BindCellActivity;
import com.game.sdk.bean.Data;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.TodayTimeUtils;
import com.game.sdkproxy.R;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 提示绑定手机Service
 */

public class RemindService extends Service {

    private Boolean iscb=false;
    private AlertDialog dia = null;
    private  Message msg;
    private  Bundle bundle;
    private  String userName;
    private  String  passWord;
    private  String  spName;
    private Timer mtimer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        KnLog.log("开始Service");
        userName = intent.getStringExtra("userName");
        passWord = intent.getStringExtra("passWord");
        spName = intent.getStringExtra("spName");
        onRemind(userName,passWord,spName);
        return super.onStartCommand(intent, flags, startId);
    }


  private Handler mHandler = new Handler(new Handler.Callback() {
      @Override
      public boolean handleMessage(Message msg) {

          switch (msg.what){
              case 1:
                  userName =   msg.getData().getString("userName");
                  passWord =   msg.getData().getString("passWord");
                  spName = msg.getData().getString("spName");
                  if(dia == null){
                      LayoutInflater inflater = LayoutInflater.from(Data.getInstance().getGameActivity());
                      View v = inflater.inflate(R.layout.mc_bind_mobile_dialog_ts, null); //绑定手机
                      dia = new AlertDialog.Builder(Data.getInstance().getGameActivity()).create();
                      Button bind = (Button) v.findViewById(R.id.visit_bind_account); //下次再说
                      Button cont = (Button) v.findViewById(R.id.visit_continue);//立刻绑定
                      ImageView close = (ImageView) v.findViewById(R.id.mc_da_lose);//关闭
                      CheckBox mcheckBox = (CheckBox) v.findViewById(R.id.mc_tx);//选择今日不提醒
                      dia.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                      dia.show();
                      dia.setContentView(v);

                      cont.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              DBHelper.getInstance().insertOrUpdateUser(userName, passWord);
                              Intent intent = new Intent(Data.getInstance().getGameActivity(), BindCellActivity.class);
                              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                              intent.putExtra("userName", userName);
                              startActivity(intent);
                              dia.dismiss();
                          }
                      });

                      //稍后绑定
                      bind.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              if (iscb) {
                                  exitsave(spName, userName);
                                  dia.dismiss();
                                  stopSelf(); //关闭service
                              } else {
                                  dia.dismiss();
                                  dia=null;
                              }
                          }
                      });

                      //关闭AlertDialog
                      close.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              dia.dismiss();
                              dia=null;
                             // stopSelf(); //关闭service
                          }
                      });

                      //选择提醒
                      mcheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                          @Override
                          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                              if (isChecked) {
                                  iscb = true;
                              } else {
                                  iscb = false;
                              }
                          }
                      });
                  }
                  break;

          }
          return false;
      }
  });


    //退出保存提醒信息
    private void exitsave(String spname,String extname){
        TodayTimeUtils.saveExitTime(Data.getInstance().getGameActivity());
        TodayTimeUtils.saveExitName(Data.getInstance().getGameActivity(),spname,extname);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KnLog.log("关闭service");
        if(mtimer!=null){
            mtimer.cancel();
        }
        if(dia!=null){
            dia.dismiss();
        }

    }


   class ReamindRunnable implements  Runnable{
       String username;
       String pasword;
       String spname;
       public  ReamindRunnable(String username,String pasword,String spname){
         //在这里把数据传进来
         this.username = username;
         this.pasword = pasword;
         this.spname = spname;
       }
       @Override
       public void run() {
           msg = new Message();
           msg.what=1;
           bundle = new Bundle();
           bundle.putString("userName",username);
           bundle.putString("passWord",pasword);
           bundle.putString("spName",spname);
           msg.setData(bundle);
           mHandler.sendMessage(msg);
       }
   }


   //计时器
   private void  onRemind(final String name, final String password, final String spname){
       if(mtimer!=null){
           mtimer.cancel();
       }
       mtimer=new Timer();
       mtimer.schedule(new TimerTask() {
           @Override
           public void run() {
               new Thread(new ReamindRunnable(name,password,spname)).start();
           }
       },10*60*1000,10*60*1000);//每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行

   }

}
