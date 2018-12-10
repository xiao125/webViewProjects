package com.game.sdk.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.game.sdk.GameSDK;
import com.game.sdk.base.SdkBaseActivity;
import com.game.sdk.call.Delegate;
import com.game.sdk.config.SDKStatusCode;
import com.game.sdk.mvp.Imp.LoginPresenterImp;
import com.game.sdk.mvp.Imp.QueryBindPresenterImp;
import com.game.sdk.mvp.Imp.RegistPresenterImp;
import com.game.sdk.mvp.Imp.SendCodePresenterImp;
import com.game.sdk.mvp.model.LoginBean;
import com.game.sdk.mvp.view.LoginView;
import com.game.sdk.mvp.view.QueryBindView;
import com.game.sdk.mvp.view.RegistView;
import com.game.sdk.mvp.view.SendCodeView;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.TodayTimeUtils;
import com.game.sdk.util.Util;
import com.game.sdkproxy.R;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 快速注册
 */
public class FastLoginActivity extends SdkBaseActivity implements RegistView,LoginView,QueryBindView,SendCodeView {

    private RegistPresenterImp registPresenterImp;
    private LoginPresenterImp loginPresenterImp;
    private QueryBindPresenterImp queryBindPresenterImp;
    private SendCodePresenterImp sendCodePresenterImp;

    private Activity m_activity = null ;
    private ImageView select_close,m_phone_ks_close;
    private Button user_register,phone_register,phone_ks_code,kn_user_zc;
    private LinearLayout user_layout,phone_layout;
    private TextView masscount;
    private EditText ks_user,kn_password,phone_ks_register,phone_ks_register_code,phone_ks_register_password;
    private boolean isVISIBLE=false;
    private String newSdk="1";
    public  static   String    m_userName ;
    public  static   String    m_passWord ;
    public String m_phone;
    public String m_pw;
    public String randName;
    private  boolean isCountDown=false; //倒计时标识
    public static final String allChar = "0123456789";
    private Boolean iscb=false;
    private String lastTime; //退出日期
    private String todayTime;//当前日期
    private String lastName; //最后退出名字
    private  String Spname; //存入sp中的key名
    private Timer m_timer ;
    private int mCount = 90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = this ;

    }

    @Override
    public int getLayoutId() {
        return R.layout.mc_fast_login;
    }

    @Override
    public void initViews() {
        initView();
    }

    @Override
    public void initListener() {

        //进入快速界面，默认用户名注册按钮不可点击
        user_register.setEnabled(false);
        initLinster();
    }

    @Override
    public void initData() {
        registPresenterImp =  new RegistPresenterImp();
        registPresenterImp.attachView(this);
        loginPresenterImp = new LoginPresenterImp();
        loginPresenterImp.attachView(this);
        queryBindPresenterImp = new QueryBindPresenterImp();
        queryBindPresenterImp.attachView(this);
        sendCodePresenterImp = new SendCodePresenterImp();
        sendCodePresenterImp.attachView(this);
        RandName();
    }

    @Override
    public void processClick(View v) {


    }


    //获取是否提醒信息
    private void remind(String name){
        Spname = name;
        lastTime =String.valueOf(TodayTimeUtils.LastTime(m_activity));
        lastName = String.valueOf(TodayTimeUtils.LastName(m_activity,name));
        todayTime = TodayTimeUtils.TodayTime();
        KnLog.log("==========lastTime========"+lastTime+"  ============lastName="+lastName);
    }

    //退出保存提醒信息
    private void exitsave(String spname,String extname){
        TodayTimeUtils.saveExitTime(m_activity);
        TodayTimeUtils.saveExitName(m_activity,spname,extname);
    }

    private void TomastUser(){
        Util.ShowTips(m_activity,m_userName+",已登录成功");
    }

    private void initLinster() {
        KSUser();
        phone_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_register.setBackgroundColor(getResources().getColor(R.color.mc_kn_text));
                phone_register.setBackgroundColor(getResources().getColor(R.color.mc_Kn_Username));
                user_register.setEnabled(true);
                user_layout.setVisibility(View.INVISIBLE); //隐藏
                phone_layout.setVisibility(View.VISIBLE);//显示
                isVISIBLE=true;
                phone_ks_code.setVisibility(View.VISIBLE); //显示倒计时
                KnLog.log("手机注册。。。。。，isVISIBLE="+isVISIBLE);
            }
        });

        user_register.setOnClickListener(new View.OnClickListener() { //用户名注册
            @Override
            public void onClick(View view) {
                //随机参数一组数字
                user_register.setBackgroundColor(getResources().getColor(R.color.mc_Kn_Username));
                phone_register.setBackgroundColor(getResources().getColor(R.color.mc_kn_text));
                user_layout.setVisibility(View.VISIBLE);//显示
                phone_layout.setVisibility(View.INVISIBLE); //隐藏
                isVISIBLE=false;
                //如果用户注册界面显示时且手机注册界面倒计时正在进行时，隐藏倒计时
                if(isCountDown && (user_layout.getVisibility()==View.VISIBLE)){
                    phone_ks_code.setVisibility(View.INVISIBLE);
                }
                KnLog.log("用户名注册。。。。。，isVISIBLE="+isVISIBLE);
            }
        });

        kn_user_zc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_layout.getVisibility()==View.VISIBLE){
                    KnLog.log("开始用户名注册");
                    //用户名注册
                    UserRegister();
                }else if ( phone_layout.getVisibility()==View.VISIBLE){
                    KnLog.log("开始手机注册");
                    //手机注册
                    MobileRegister();
                }
            }
        });

        //验证码倒计时
        phone_ks_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cell_Num = phone_ks_register.getText().toString().trim();
                if (isPhone(cell_Num)){
                    return;
                }else {
                    sendcod();
                }
            }
        });

        select_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ks_user.setText("");
            }
        });

        m_phone_ks_close.setOnClickListener(new View.OnClickListener() { //清除验证码
            @Override
            public void onClick(View view) {
                phone_ks_register_code.setText("");
            }
        });

        masscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 跳转保存账号界面
                Intent intent = new Intent(FastLoginActivity.this, AutoLoginActivity.class);
                intent.putExtra("selectLogin", "selectLogin");
                startActivity(intent);
                finishActivity();

            }
        });
    }

    private  void RandName(){
        //获取随机有户名
        SimpleDateFormat formatter  =  new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate =  new Date(System.currentTimeMillis());
        String   time  =   formatter.format(curDate);
        KnLog.log("获取当前时间戳:"+time);
        registPresenterImp.RandUserNam(time);
    }

    //随机生成一组字符串
    public void  generateMixString(int length)
    {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++)
        {
            sb.append(allChar.charAt(random.nextInt(allChar.length())));
        }
        kn_password.setText(sb.toString()); //默认填写密码
    }


    //判断手机号是否正确
    private boolean isPhone(String phone) {
        if(TextUtils.isEmpty(phone)){
            Util.ShowTips(m_activity,getResources().getString(R.string.mc_tips_58));

            return true;
        }
        if(!Util.isMobileNO(phone)){
            Util.ShowTips(m_activity,getResources().getString(R.string.mc_tips_57));
            return true;
        }
        if(!Util.isNetWorkAvailable(getApplicationContext())){
            Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());

            return true;
        }
        return false;
    }

    //输入用户名与密码监听
    private void KSUser(){

        ks_user.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v ) {
                ks_user.setCursorVisible(true);
            }
        } );

        ks_user.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v , int actionId, KeyEvent event ) {
                if(EditorInfo.IME_ACTION_DONE==actionId){ // 按下完成按钮
                    ks_user .clearFocus(); //清除光标，也就是失去焦点
                    kn_password.requestFocus();
                    ks_user.setCursorVisible(false); //让EditText不出现光标
                }
                return false;
            }
        } );

        kn_password.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                kn_password.setCursorVisible(true);
            }
        } );


        kn_password.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v , int actionId , KeyEvent event ) {
                if(EditorInfo.IME_ACTION_DONE==actionId){
                    kn_password.clearFocus();
                    ks_user.clearFocus();
                    kn_password.requestFocus();
                    kn_password.setCursorVisible(false);
                    Util.hideEditTextWindow(m_activity, kn_password);
                    Util.hideEditTextWindow(m_activity, ks_user);
                    //显示密码
                    kn_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                return false;
            }
        } );
    }

    private void initView() {
        user_register = (Button) findViewById(R.id.kn_selecte_user_register); //用户名注册
        ks_user= (EditText) findViewById(R.id.ks_user); //用户名
        kn_password = (EditText) findViewById(R.id.kn_password); //用户名密码
        select_close = (ImageView) findViewById(R.id.select_close); //清除账号
        phone_register = (Button) findViewById(R.id.kn_selecte_phone_register);//手机注册
        phone_ks_register= (EditText) findViewById(R.id.phone_ks_register); //手机号
        phone_ks_register_code= (EditText) findViewById(R.id.phone_ks_register_code); //获取到的验证码
        phone_ks_register_password= (EditText) findViewById(R.id.phone_ks_register_password);//输入的密码
        phone_ks_code = (Button) findViewById(R.id.phone_ks_code); //验证码
        m_phone_ks_close = (ImageView) findViewById(R.id.phone_ks_close); //清除验证码
        user_layout = (LinearLayout) findViewById(R.id.user_register_layout); //用户名注册view
        phone_layout = (LinearLayout) findViewById(R.id.phone_register_layout); //手机号注册view
        kn_user_zc = (Button) findViewById(R.id.kn_user_zc); //注册按钮
        masscount =(TextView)findViewById(R.id.yy_username); //已有账号
    }

    /**
     * 发送验证码倒计时
     */
    private void countdownTimer(){
        phone_ks_code.setEnabled(false);
        m_timer =  new Timer();
       // final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCount--;
                        phone_ks_code.setText(String.valueOf(mCount)+"秒");
                        phone_ks_code.setBackgroundColor(getResources().getColor(R.color.mc_kn_text));
                        isCountDown = true;
                        if (mCount<=0){
                            phone_ks_code.setText("重新发送");
                            phone_ks_code.setBackgroundColor(getResources().getColor(R.color.mc_Kn_Username));
                            phone_ks_code.setEnabled(true);
                            m_timer.cancel();
                        }
                    }
                });
            }
        };
        m_timer.schedule(task,1000,1000);
    }


    /**
     * 发送验证码
     */

    private void sendcod(){
        String cell_Num = phone_ks_register.getText().toString().trim();
        if (isPhone(cell_Num)){
            return;
        }else {
            LoadingDialog.show(m_activity, "获取验证码中...", true);
            sendCodePresenterImp.SendCode(cell_Num,m_activity);
        }

    }


    //判断手机号是否正确
    private  void isphone( String cell_Num){

        if(TextUtils.isEmpty(cell_Num)){
            Util.ShowTips(m_activity,getResources().getString(R.string.mc_tips_58));
            return ;
        }
        if(!Util.isMobileNO(cell_Num)){
            Util.ShowTips(m_activity,getResources().getString(R.string.mc_tips_57));
            return ;
        }
        if(!Util.isNetWorkAvailable(getApplicationContext())){
            Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());
            return ;
        }
    }

    //用户名与密码，注册
    private void UserRegister(){
        Util.hideEditTextWindow(this,kn_password);
        checkRegisterParams(m_activity,ks_user,kn_password);
    }

    //手机号注册
    private void MobileRegister(){
        Util.hideEditTextWindow(this,phone_ks_register_password);
        checkRegisterParams(m_activity,phone_ks_register,phone_ks_register_password,phone_ks_register_code);
    }

    //判断手机号，验证码，密码
    private void checkRegisterParams(Activity context, EditText phone, EditText passWordEt,EditText code) {
        String userphone = phone.getText().toString().trim(); //手机号
        String security_code = code.getText().toString().trim();//验证码
        String password = passWordEt.getText().toString().trim();//密码

        if (!Util.isUserPhone(context,userphone)){
            return;
        }

        if (!Util.isUserCode(context,security_code)){
            return;
        }

        if (!Util.isUserPassword(context,password)){
            return;
        }

       // String pw = Md5Util.getMd5(password);
        m_phone = userphone ;
        m_pw = password ;
        LoadingDialog.show(m_activity, "注册中...",true);
        //手机注册
        registPresenterImp.RegistPhone(userphone,password,security_code,m_activity);
    }


    //判断用户名与密码输入格式
    private void checkRegisterParams(Activity context, EditText userNameEt, EditText passWordEt) {
        //注意：判断顺序
        String username = userNameEt.getText().toString();
        if(!Util.isName(context,username)){
            return;
        }
        String password = passWordEt.getText().toString();
        if (!Util.isUserPassword(context,password)){
            return;
        }
     //  String  pw = Md5Util.getMd5(password);
        m_userName = username ;
        m_passWord = password ;
        KnLog.log("用户名注册的密码="+password);
        LoadingDialog.show(m_activity, "注册中...",true);
        registPresenterImp.RegistAccount(username,password,m_activity);

    }

    @Override
    public void showAppInfo(String msg, String data) {

    }

    @Override
    public void registAccountSuccess(int code, String data) {
        LoadingDialog.dismiss();
        switch (code){
            case SDKStatusCode.SUCCESS:
                remind(m_userName);
                //账号登录
                LoginBean bean = new LoginBean(m_userName , m_passWord );
                loginPresenterImp.login(bean,m_activity);
                break;
            case SDKStatusCode.FAILURE:
                Util.ShowTips(m_activity,data);
                break;
            default:
                break;
        }
    }

    @Override
    public void registAccountFailed(int code, String data) {
        LoadingDialog.dismiss();
        Util.ShowTips(m_activity,data);
    }

    @Override
    public void registMobileSuccess(int code, String data) {
        LoadingDialog.dismiss();
        switch (code){
            case SDKStatusCode.SUCCESS:
                //添加手机账号
                DBHelper.getInstance().insertOrUpdateUser( m_phone ,m_pw );
                Util.ShowTips(FastLoginActivity.this, getResources().getString(R.string.mc_tips_15) );
                GameSDK.instance.login(FastLoginActivity.this); //跳转到免密码登录
                break;
            case SDKStatusCode.FAILURE:
                Util.ShowTips(m_activity,data);
                break;
            default:
                break;
        }

    }

    @Override
    public void registMobileFailed(int code, String data) {
        LoadingDialog.dismiss();
        Util.ShowTips(m_activity,data);
    }

    @Override
    public void sendCodeSuccess(int code, String data) {
        LoadingDialog.dismiss();
        switch (code) {
            case SDKStatusCode.SUCCESS:
                countdownTimer();
                break;
            case SDKStatusCode.FAILURE:
                Util.ShowTips(m_activity,data);
                break;
            default:
                break;
        }
    }

    @Override
    public void sendCodeFailed(int code, String data) {
        LoadingDialog.dismiss();
        Util.ShowTips(m_activity,data);
    }

    @Override
    public void loginSuccess(String code, String data) {
        LoadingDialog.dismiss();
        //登录成功之后就保存账号密码
       // DBHelper.getInstance().insertOrUpdateUser( m_userName , m_passWord );
        Delegate.listener.callback( SDKStatusCode.SUCCESS,data);

        //是否首次登陆
        String[] usernames = DBHelper.getInstance().findAllUserName();
        KnLog.log("是否首次注册登录"+usernames.length);
        //	数据库中获取用户数据量
        if (usernames.length != 0) {
            DBHelper.getInstance().insertOrUpdateUser( m_userName , m_passWord );
            //查询账号是否绑定手机号
            queryBindPresenterImp.queryBindAccont(m_userName,m_activity);
        }else {
            DBHelper.getInstance().insertOrUpdateUser( m_userName , m_passWord );
            finishActivity();
        }

    }

    @Override
    public void loginFailed(String code, String data) {
        LoadingDialog.dismiss();
        try {
            KnLog.log("登录失败:"+data);
            JSONObject obj = new JSONObject(data);
            int dataCode = obj.getInt("code");
            String reason = obj.getString("reason");
            Util.ShowTips(m_activity,reason);
            Delegate.listener.callback(SDKStatusCode.FAILURE,data);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryBindSuccess(int code, String data) {
        switch (code) {
            case SDKStatusCode.SUCCESS:
                TomastUser();
                finishActivity();
                break;
            case SDKStatusCode.QUERY_BIND_NOT:
                if (lastTime.equals(todayTime) && lastName.equals(m_userName)) { //如果两个时间段相等
                    //KnLog.log("今天不提醒,今天日期"+todayTime+" 最后保存日期:"+lastTime+" 现在登录的账号:"+m_userName+" 最后保存的账号:"+lastName);
                    TomastUser();
                    finishActivity();
                } else {

                    Util.SetDialogs(m_activity,m_userName,m_passWord,Spname);

                  /*  LayoutInflater inflater = LayoutInflater.from(m_activity);
                    View v = inflater.inflate(R.layout.mc_bind_mobile_dialog_ts, null); //绑定手机
                    LinearLayout layout = (LinearLayout) v.findViewById(R.id.visit_dialog);
                    final AlertDialog dia = new AlertDialog.Builder(m_activity).create();
                    Button bind = (Button) v.findViewById(R.id.visit_bind_account); //下次再说
                    Button cont = (Button) v.findViewById(R.id.visit_continue);//立刻绑定
                    TextView ts = (TextView) v.findViewById(R.id.ts);
                    ImageView close = (ImageView) v.findViewById(R.id.mc_da_lose);//关闭
                    CheckBox mcheckBox = (CheckBox) v.findViewById(R.id.mc_tx);//选择今日不提醒
                    if (m_activity != null) {
                        dia.show();
                        dia.setContentView(v);
                    }
                    cont.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DBHelper.getInstance().insertOrUpdateUser( m_userName , m_passWord );
                            Intent intent = new Intent(m_activity, BindCellActivity.class);
                            intent.putExtra("userName", m_userName);
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
                                if (iscb) {
                                    //保存勾选后的日期
									  *//*  saveExitTime(getTime());
										saveExiName(username);*//*
                                    exitsave(Spname, m_userName);
                                    TomastUser();
                                    dia.dismiss();
                                    finishActivity();
                                } else {
                                    TomastUser();
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
                            if (isChecked) {
                                iscb = true;
                            } else {
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

    @Override
    public void randUserNameSuccess(int code, String data) {
        randName = data;
        ks_user.setText(randName); //显示用户名
        generateMixString(6);//密码客户端随机生成
    }

    @Override
    public void randUserNameFailed(int code, String data) {

    }

    private void finishActivity(){
        camcel();
        finish();
        m_activity=null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registPresenterImp.detachView();
        loginPresenterImp.detachView();
        queryBindPresenterImp.detachView();
        sendCodePresenterImp.detachView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
            return true;//不执行父类点击事件
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

    //关闭倒计时
    protected void camcel(){
        if(m_timer!=null){
            m_timer.cancel();
        }
    }
}
