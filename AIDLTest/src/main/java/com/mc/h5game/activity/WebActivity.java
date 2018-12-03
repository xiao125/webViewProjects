package com.mc.h5game.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import com.cs.mc.R;
import com.mc.weblib.BaseWebviewFragment;
import com.mc.weblib.CommonWebFragment;
import com.mc.weblib.interfaces.ICallBack;
import com.mc.weblib.utils.AndroidBug5497Workaround;
import com.mc.weblib.utils.WebConstants;
import com.proxy.OpenSDK;
import com.proxy.util.LogUtil;




public class WebActivity extends FragmentActivity {

   private String url;
   BaseWebviewFragment webviewFragment;
   public  OpenSDK m_proxy;
   private boolean isInit;
   public static void start(Context context,String url,int testLevel){
       Intent intent = new Intent(context, WebActivity.class);
       intent.putExtra(WebConstants.INTENT_TAG_URL, url);
       intent.putExtra("level", testLevel);
       if (context instanceof Service) { //context对象是否来自Service
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       }
       context.startActivity(intent);
   }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_web);
        url = getIntent().getStringExtra(WebConstants.INTENT_TAG_URL);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        int level = getIntent().getIntExtra("level",WebConstants.LEVEL_BASE);
        webviewFragment = null;
        if(level == WebConstants.LEVEL_BASE){
            webviewFragment = CommonWebFragment.newInstance(url);
        }
        transaction.replace(R.id.web_view_fragment,webviewFragment).commit();
    }



    /* @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
           if(webviewFragment !=null && webviewFragment instanceof  BaseWebviewFragment){
               boolean flag = webviewFragment.onKeyDown(keyCode,event);
               if(flag){
                   return flag;
               }
           }
            return super.onKeyDown(keyCode, event);
        }
    */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) { // 防止按音量键调用退出（减小键监听）
            // LogUtil.log("按了音量减");
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) { // //防止按音量键调用退出（音量增加键监听）
            // LogUtil.log("按了音量加");
        } else {
            webviewFragment.sendMessage(new ICallBack() {
                @Override
                public void get_message_from_Fragment(boolean init, OpenSDK openSDK) {
                    LogUtil.log("获取到Fragment发送的消息：");
                    isInit = init;
                    m_proxy = openSDK;
                }
            });
            LogUtil.log("退出游戏2");
            if (isInit) {
                if (m_proxy.hasThirdPartyExit()) {
                    m_proxy.onThirdPartyExit();
                    return true;
                } else {
                    LogUtil.log("退出游戏3");
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            WebActivity.this);
                    builder.setTitle("游戏");
                    builder.setMessage("真的忍心退出游戏么？");
                    builder.setPositiveButton("忍痛退出",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    m_proxy.Quit();
                                    dialog.dismiss();
                                    System.exit(0);
                                }
                            });
                    builder.setNegativeButton("手误点错",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });

                    builder.create().show();
                    return true;
                }
            } else {
                LogUtil.log("退出游戏4");
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        WebActivity.this);
                builder.setTitle("游戏");
                builder.setMessage("真的忍心退出游戏么？");
                builder.setPositiveButton("忍痛退出",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                System.exit(0);
                            }
                        });
                builder.setNegativeButton("手误点错",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });

                builder.create().show();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
