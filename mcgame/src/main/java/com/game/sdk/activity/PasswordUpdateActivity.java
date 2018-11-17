package com.game.sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.game.sdk.base.SdkBaseActivity;
import com.game.sdk.mvp.Imp.PasswordUpdatePresenterImp;
import com.game.sdk.mvp.Imp.SendCodePresenterImp;
import com.game.sdk.mvp.view.PasswordUpdateView;
import com.game.sdk.mvp.view.SendCodeView;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.Util;
import com.game.sdkproxy.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 通过手机验证码重新修改密码
 */
public class PasswordUpdateActivity extends SdkBaseActivity implements PasswordUpdateView,SendCodeView {

	private PasswordUpdatePresenterImp passwordUpdatePresenterImp;
	private SendCodePresenterImp sendCodePresenterImp;
	private Activity m_activity = null ;
	private EditText m_code = null ;
	private EditText password_new = null ;
	private EditText password_new_ng = null ;
	private ImageView m_password_update_back,m_select_login_close;
	private Button m_password_update_submit,m_update_code;
	private String   va_phone = null ;
	private String newSdk="1";
	private Timer    m_timer = null ;
	private int      m_time  = 90 ;
	private Message  m_msg = null ;
	private String  qdPwd;
	private String newpassword; //输入的密码进行md5加密存入本地数据库

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = this ;
	}

	@Override
	public int getLayoutId() {
		return R.layout.mc_password_update;
	}

	@Override
	public void initViews() {
		initView();
		Intent intent = getIntent();
		va_phone = intent.getStringExtra("phone");
	}

	@Override
	public void initListener() {
		m_code.setOnClickListener(this);
		m_select_login_close.setOnClickListener(this);
		m_password_update_back.setOnClickListener(this);
		m_select_login_close.setOnClickListener(this);
		m_update_code.setOnClickListener(this);
		m_password_update_submit.setOnClickListener(this);

		//全屏，进入输入用户名
		m_code.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v ) {
				// TODO Auto-generated method stub
				m_code.setCursorVisible(true);
			}
		} );

		//输入完毕，下一步，进入输入密码
		m_code.setOnEditorActionListener( new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v , int actionId, KeyEvent event ) {
				// TODO Auto-generated method stub
				if(EditorInfo.IME_ACTION_DONE==actionId){
					m_code.clearFocus();
					password_new.requestFocus();
					m_code.setCursorVisible(false);
				}
				return false;
			}
		} );
	}

	@Override
	public void initData() {
		passwordUpdatePresenterImp = new PasswordUpdatePresenterImp();
		passwordUpdatePresenterImp.attachView(this);
		sendCodePresenterImp = new SendCodePresenterImp();
		sendCodePresenterImp.attachView(this);
	}

	@Override
	public void processClick(View v) {
		int id = v.getId();
		if(id== R.id.password_update_back){ //返回
			if (m_activity!=null){
				camcel();
				Intent intent1 = new Intent(PasswordUpdateActivity.this,ForgotPasswordActivity.class);
				startActivity(intent1);
				m_activity.finish();
			}
		}else if(id==R.id.password_update_submit){ //确定提交
			String cell_num = va_phone; //手机号
			String security_code = m_code.getText().toString().trim(); //验证码
			String newPwd  = password_new.getText().toString().trim(); //新密码
			qdPwd =password_new_ng.getText().toString().trim(); //确定密码
			if(!Util.isUserCode(m_activity,security_code)){
				return;
			}
			if(!Util.isUserPassword(m_activity,newPwd)){
				return;
			}
			if (!newPwd.equals(qdPwd)) {
				Util.ShowTips(m_activity,getResources().getString(R.string.mc_tips_65) );
				return;
			}
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());
				return ;
			}
			newpassword = newPwd;
			KnLog.log("开始更改新密码请求");
			LoadingDialog.show(m_activity, "正在请求中...", true);
			//发送更改新密码请求
			passwordUpdatePresenterImp.ModifyPasword(cell_num,security_code,newpassword,newSdk,m_activity);

		}else if(id==R.id.update_code){ //验证码
			String cell_Num = va_phone; //手机号
			if (!Util.isUserPhone(m_activity,cell_Num)){
				return;
			}
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());
				return ;
			}

			LoadingDialog.show(m_activity, "获取验证码中...", true);
			sendCodePresenterImp.SendCode(cell_Num,m_activity);

		}else if (id==R.id.passwd_select_login_close){
			/*if (m_activity!=null){
				m_activity.finish();
				m_activity=null;
			}*/
		}
	}


	private void initView() {
		m_code = (EditText) findViewById(R.id.phone_code); //验证码
		password_new = (EditText) findViewById(R.id.password_new); //新密码
		password_new_ng = (EditText) findViewById(R.id.password_new_ng); //确认密码
		m_password_update_back = (ImageView) findViewById(R.id.password_update_back);//返回
		m_select_login_close= (ImageView) findViewById(R.id.passwd_select_login_close);//关闭
		m_password_update_submit= (Button) findViewById(R.id.password_update_submit); //确定
		m_update_code= (Button) findViewById(R.id.update_code); //验证码
	}


	@Override
	public void showAppInfo(String msg, String data) {

	}

	@Override
	public void modifySuccess(int code, String data) {
		LoadingDialog.dismiss();
		Util.ShowTips(m_activity,data);
		DBHelper.getInstance().insertOrUpdateUser(data ,newpassword ); //账号密码保存本地数据库
		Intent intent1=new Intent(PasswordUpdateActivity.this, AutoLoginActivity.class);
		intent1.putExtra("userName", data);
		intent1.putExtra("password",qdPwd);
		startActivity(intent1);
		camcel();
		m_activity.finish();
		m_activity=null;

	}

	@Override
	public void modifyFailed(int code, String data) {
		LoadingDialog.dismiss();
		Util.ShowTips(m_activity,data);
	}

	@Override
	public void sendCodeSuccess(int code, String data) {
		LoadingDialog.dismiss();
		countdownTimer();
	}

	@Override
	public void sendCodeFailed(int code, String data) {
		LoadingDialog.dismiss();
		Util.ShowTips(m_activity,data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		passwordUpdatePresenterImp.detachView();
		sendCodePresenterImp.detachView();
	}

	private void countdownTimer(){
		m_update_code.setEnabled(false);
		m_timer =  new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						m_time--;
						m_update_code.setText(String.valueOf(m_time)+"秒");
						m_update_code.setBackgroundColor(getResources().getColor(R.color.mc_kn_text));
						if (m_time<=0){
							m_update_code.setText("重新发送");
							m_update_code.setBackgroundColor(getResources().getColor(R.color.mc_Kn_Username));
							m_update_code.setEnabled(true);
							m_timer.cancel();
						}
					}
				});
			}
		};
		m_timer.schedule(task,1000,1000);
	}



	//关闭倒计时
	protected void camcel(){
		if(m_timer!=null){
			m_timer.cancel();
		}
	}
}
