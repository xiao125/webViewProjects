package com.game.sdk.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.game.sdk.base.SdkBaseActivity;
import com.game.sdk.call.Delegate;
import com.game.sdk.config.SDKStatusCode;
import com.game.sdk.mvp.Imp.LoginPresenterImp;
import com.game.sdk.mvp.Imp.QueryBindPresenterImp;
import com.game.sdk.mvp.model.LoginBean;
import com.game.sdk.mvp.view.LoginView;
import com.game.sdk.mvp.view.QueryBindView;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.TodayTimeUtils;
import com.game.sdk.util.Util;
import com.game.sdkproxy.R;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**自动登录
 *
 */

public class AutomaticLoginActivity extends SdkBaseActivity implements LoginView,QueryBindView {

    private LoginPresenterImp loginPresenterImp;
    private QueryBindPresenterImp queryBindPresenterImp;
    private Activity m_activity = null ;
    private static  String username =null;
    private static  String password =null;
    //声明一个SharedPreferences对象和一个Editor对象
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Boolean iscb=false;
    private String lastTime; //退出日期
    private String todayTime;//当前日期
    private String lastName; //最后退出名字

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public int getLayoutId() {
        return R.layout.mc_automaticlogin_layout;
    }

    @Override
    public void initViews() {
        m_activity = this ;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        loginPresenterImp = new LoginPresenterImp();
        loginPresenterImp.attachView(this);
        queryBindPresenterImp = new QueryBindPresenterImp();
        queryBindPresenterImp.attachView(this);

        LoadingDialog.show(m_activity,"正在登录中...",false); //开启提示自动登录中
        AutLogin();
        lastTime =String.valueOf(TodayTimeUtils.LastTime(m_activity));
        lastName = String.valueOf(TodayTimeUtils.LastName(m_activity,username));
        todayTime = TodayTimeUtils.TodayTime();
        KnLog.log("==========lastTime========"+lastName+"  ============lastName="+lastName);
    }

    @Override
    public void processClick(View v) {

    }

    //自动登录
    private void AutLogin(){
        String usernames[] = DBHelper.getInstance().findAllUserName();
        if( usernames != null && usernames.length >0 ){
            username = usernames[0]; //获得到用户名
            password = DBHelper.getInstance().findPwdByUsername(username); //密码
            KnLog.log("自动登录");
            //账号登录
            LoginBean bean = new LoginBean(username, password);
            loginPresenterImp.login(bean,m_activity);
        }
    }




    @Override
    public void showAppInfo(String msg, String data) {

    }

    @Override
    public void loginSuccess(String msg, String data) {

        //登录成功之后就保存账号密码
        Delegate.listener.callback( SDKStatusCode.SUCCESS,data);
        LoadingDialog.dismiss();//关闭
        Util.ShowTips(m_activity,username+"登录成功！");
        //查询账号是否绑定手机号
        queryBindPresenterImp.queryBindAccont(username,m_activity);
    }

    @Override
    public void loginFailed(String msg, String data) {
        LoadingDialog.dismiss();//关闭
        try {
            JSONObject obj = new JSONObject(data);
            int dataCode = obj.getInt("code");
            String reason = obj.getString("reason");
            Delegate.listener.callback(SDKStatusCode.FAILURE,data);
            Util.ShowTips(m_activity,reason);
            StartActivitys(m_activity);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryBindSuccess(int code, String data) {
        switch (code) {
            case SDKStatusCode.SUCCESS:
                Util.ShowTips(m_activity, username + ",已登录成功");
                finishActivity();
                break;
            case SDKStatusCode.QUERY_BIND_NOT:
                if (lastTime.equals(todayTime) && lastName.equals(username)) { //如果两个时间段相等
                    // KnLog.log("今天不提醒,今天日期"+todayTime+" 最后保存日期:"+lastTime+" 现在登录的账号:"+username+" 最后保存的账号:"+lastName);
                    Util.ShowTips(m_activity, username + ",已登录成功");
                    finishActivity();
                } else {

                    Util.SetDialogs(m_activity, username, password, lastName);

                  /*  LayoutInflater inflater = LayoutInflater.from(m_activity);
                    View v = inflater.inflate(R.layout.mc_bind_mobile_dialog_ts, null); //绑定手机
                    LinearLayout layout = (LinearLayout) v.findViewById(R.id.visit_dialog);
                    final AlertDialog dia = new AlertDialog.Builder(m_activity).create();
                    Button bind = (Button) v.findViewById(R.id.visit_bind_account); //下次再说
                    Button cont = (Button) v.findViewById(R.id.visit_continue);//立刻绑定
                    TextView ts = (TextView) v.findViewById(R.id.ts);
                    ImageView close = (ImageView)v.findViewById(R.id.mc_da_lose);//关闭
                    CheckBox mcheckBox = (CheckBox)v.findViewById(R.id.mc_tx);//选择今日不提醒
                    dia.show();
                    dia.setContentView(v);
                    cont.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DBHelper.getInstance().insertOrUpdateUser(username,password);
                            Intent intent = new Intent(m_activity, BindCellActivity.class);
                            intent.putExtra("userName", username);
                            startActivity(intent);
                            if (null == m_activity) {

                            } else {
                                dia.dismiss();
                                finishActivity();
                            }
                        }
                    });

                    //稍后绑定
                    bind.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null == m_activity) {
                            } else {
                                if(iscb){
                                    //保存勾选后的日期
                                    TodayTimeUtils.saveExitTime(m_activity);
                                    TodayTimeUtils.saveExitName(m_activity,username,username);
                                    dia.dismiss();
                                    finishActivity();
                                   // KnLog.log("勾选了今天不提醒,今天日期"+todayTime+" 最后保存日期:"+lastTime+" 现在登录的账号:"+username+" 最后保存的账号:"+lastName);
                                }else {
                                    dia.dismiss();
                                    finishActivity();
                                }
                            }
                        }
                    });

                    //关闭AlertDialog
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dia.dismiss();
                            finishActivity();
                        }
                    });

                    //选择提醒
                    mcheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked){
                                iscb= true;
                            }else {
                                iscb = false;
                            }
                        }
                    });*/
                }
                break;
            case SDKStatusCode.FAILURE:
                finishActivity();
                break;
        }
    }

    @Override
    public void queryBindFailed(int code, String data) {
        finishActivity();
    }

    //跳转免密码登录页面
    private  void  StartActivitys(Activity activity){
        Intent intents = new Intent(activity, AutoLoginActivity.class);
        startActivity(intents);
        activity.finish();
    }

    private void finishActivity(){
        finish();
        m_activity=null;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginPresenterImp.detachView();
        queryBindPresenterImp.detachView();
    }
}
