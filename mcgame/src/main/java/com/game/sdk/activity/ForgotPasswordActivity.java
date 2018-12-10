package com.game.sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.game.sdk.base.SdkBaseActivity;
import com.game.sdk.config.SDKStatusCode;
import com.game.sdk.mvp.Imp.ForgotPasswordPresenterImp;
import com.game.sdk.mvp.Imp.QueryBindPresenterImp;
import com.game.sdk.mvp.view.ForgotPasswordView;
import com.game.sdk.mvp.view.QueryBindView;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.Util;
import com.game.sdkproxy.R;
/**
 * 忘记密码选择找回密码方式
 * Created
 */

public class ForgotPasswordActivity extends SdkBaseActivity implements ForgotPasswordView,QueryBindView {

    private ForgotPasswordPresenterImp forgotPasswordPresenterImp;
    private QueryBindPresenterImp queryBindPresenterImp;
    private Activity activity;
    private EditText m_phone;
    private ImageView m_close;
    private Button m_zh_qd,m_phone_ks_code;
    private CheckBox m_cb_phone;
    private TextView mphone;
    private FrameLayout m_frameLayout;
    private Boolean iscb=true;

    @Override
    public int getLayoutId() {
        return R.layout.mc_forgot_password_layout;
    }

    @Override
    public void initViews() {
        initView();
    }

    @Override
    public void initListener() {
        initLinerter();
    }

    @Override
    public void initData() {
        forgotPasswordPresenterImp = new ForgotPasswordPresenterImp();
        forgotPasswordPresenterImp.attachView(this);
        queryBindPresenterImp = new QueryBindPresenterImp();
        queryBindPresenterImp.attachView(this);
    }

    @Override
    public void processClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this ;
    }



    private void initLinerter() {

        m_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this,AutoLoginActivity.class);
                 activity.startActivity(intent);
                activity.finish();
                activity=null;
                if (activity!=null){
                    activity.finish();
                    activity=null;
                }
            }
        });

        //手机号找回
        m_cb_phone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    iscb=true;
                    mphone.setTextColor(getResources().getColor(R.color.mc_Kn_Username));
                  //  memail.setTextColor(getResources().getColor(R.color.kn_selecte_log));
                   // m_cb_email.setChecked(false);
                    m_cb_phone.setChecked(true);
                    m_zh_qd.setEnabled(true);
                    m_zh_qd.setBackgroundColor(getResources().getColor(R.color.mc_Kn_Username));
                }else {
                    mphone.setTextColor(getResources().getColor(R.color.mc_kn_selecte_log));
                    m_zh_qd.setEnabled(false);
                    m_zh_qd.setBackgroundColor(getResources().getColor(R.color.mc_kn_text));
                }
            }
        });

        //邮箱找回
      /*  m_cb_email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){

                    iscb=false;
                    memail.setTextColor(getResources().getColor(R.color.Kn_Username));
                    mphone.setTextColor(getResources().getColor(R.color.kn_selecte_log));
                    m_cb_phone.setChecked(false);
                    m_cb_email.setChecked(true);
                }
            }
        });*/

        //确定
        m_zh_qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始验证是否绑定手机号
                String phone= m_phone.getText().toString().trim();
                //查询账号是否绑定手机号
                queryBindPresenterImp.queryBindAccont(phone,activity);
            }
        });

        //查询是否注册了该账号
        m_phone_ks_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAccountBindParams(activity,m_phone);
            }
        });

    }


    //判断是否是手机号或账号
    private void checkAccountBindParams(Activity context, EditText mUsername) {

        String username =  m_phone.getText().toString().trim(); //账号或手机号
        if (ismobile(activity, username)) return;
        //KnLog.log("判断是否是手机="+ismobile(activity, username));
        if (!Util.NameLength(username)){
            Util.ShowTips(context,context.getResources().getString(R.string.mc_tips_4) );
            return ;
        }
        if (!Util.isAccordName(username)){
            Util.ShowTips(context, context.getResources().getString(R.string.mc_tips_3));
            return ;
        }

        //KnLog.log("判断是否是手机="+ismobile(activity, username));
        LoadingDialog.show(activity, "正在验证手机账号中...", true);
        //查询账号是否存在
        forgotPasswordPresenterImp.GetUsername(username,activity);
    }


    private boolean ismobile(Activity context, String username) {
        if(!Util.isMobileNO(username)) { //如果不是手机号
            //Util.ShowTips(m_activity, getResources().getString(R.string.tips_57)); //如果不是手机号
            //手机号或者账号不能为空
           if (!Util.isName(context,username)){
               return true;
           }
        }

        return false;
    }


    private void initView() {
        m_close = (ImageView) findViewById(R.id.select_forgot_close);
        m_phone = (EditText) findViewById(R.id.phone_ks_va); //手机号或账号
        m_phone_ks_code = (Button) findViewById(R.id.phone_ks_code_va); //验证手机号或账号是否存在
        m_zh_qd= (Button) findViewById(R.id.zh_qd);
        m_cb_phone= (CheckBox) findViewById(R.id.zh_phone);
        mphone = (TextView) findViewById(R.id.tv);
        m_frameLayout= (FrameLayout) findViewById(R.id.zh_view); //显示找回密码view
    }


    @Override
    public void showAppInfo(String msg, String data) {

    }


    @Override
    public void queryBindSuccess(int code, String data) {
        LoadingDialog.dismiss();

        switch (code){
            case SDKStatusCode.QUERY_BIND_SUCCESS:
                //手机验证码更改密码
                Intent intent1 = new Intent(ForgotPasswordActivity.this,PasswordUpdateActivity.class);
                intent1.putExtra("phone",data);
                startActivity(intent1);
                activity.finish();
                break;
            case SDKStatusCode.QUERY_BIND_NOT:
                Util.ShowTips(activity,"尚未绑定手机号");
                m_frameLayout.setVisibility(View.GONE);
                break;
            default:
                Util.ShowTips(activity,data);
                m_frameLayout.setVisibility(View.GONE);
                break;
        }



    }

    @Override
    public void queryBindFailed(int code, String data) {
        LoadingDialog.dismiss();
        switch (code){
            case SDKStatusCode.QUERY_BIND_NOT:
                Util.ShowTips(activity,"尚未绑定手机号");
                m_frameLayout.setVisibility(View.GONE);
                break;
                default:
                    Util.ShowTips(activity,data);
                    m_frameLayout.setVisibility(View.GONE);
                    break;
        }


    }

    @Override
    public void getUsernameSuccess(int code, String data) {
        LoadingDialog.dismiss();
        switch (code){
            case SDKStatusCode.SUCCESS:
                //显示view
                m_frameLayout.setVisibility(View.VISIBLE);
                break;
            case SDKStatusCode.FAILURE:
                Util.ShowTips(activity,data);
                break;
            default:
                break;
        }
    }

    @Override
    public void getUsernameFailed(int code, String data) {
        LoadingDialog.dismiss();
        Util.ShowTips(activity,data);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            return true;//不执行父类点击事件
        }
        KnLog.log("====屏蔽返回键1");
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        forgotPasswordPresenterImp.detachView();
        queryBindPresenterImp.detachView();
    }
}
