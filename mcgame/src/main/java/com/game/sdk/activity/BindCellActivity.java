package com.game.sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.game.sdk.base.SdkBaseActivity;
import com.game.sdk.config.SDKStatusCode;
import com.game.sdk.mvp.Imp.BindCellPresenterImp;
import com.game.sdk.mvp.Imp.SendCodePresenterImp;
import com.game.sdk.mvp.view.BindCellView;
import com.game.sdk.mvp.view.SendCodeView;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.Util;
import com.game.sdkproxy.R;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 手机号绑定账号
 */
public class BindCellActivity  extends SdkBaseActivity implements BindCellView,SendCodeView {

	private BindCellPresenterImp bindCellPresenterImp;
	private SendCodePresenterImp sendCodePresenterImp;

	private Activity m_activity = null ;
	private EditText m_cellNum_et = null ;
	private EditText m_security_code__et = null ;
	private ImageView m_phone_ks_close,m_select_login_close,m_get_security_back;
	private String  m_userNames = null ;
	private Button   m_get_security_codeBtn,m_get_security_submit;
	private Timer    m_timer = null ;
	//private int      m_time  = 60 ;
	private int mCount = 90;

	private Message  m_msg = null ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = this ;

	}

	private void initView() {
		m_cellNum_et = (EditText)findViewById(R.id.cellnumber__et); //手机号
		m_security_code__et = (EditText)findViewById(R.id.security_code__et); //请输入验证码
		m_phone_ks_close = (ImageView) findViewById(R.id.phone_ks_close); //清除验证码
		m_select_login_close = (ImageView) findViewById(R.id.select_login_close); //关闭
		m_get_security_codeBtn= (Button) findViewById(R.id.get_security_code); //验证码
		m_get_security_back= (ImageView) findViewById(R.id.get_security_back); //返回
		m_get_security_submit = (Button) findViewById(R.id.get_security_submit); //确定

	}

	@Override
	public int getLayoutId() {
		return R.layout.mc_bind_cell;
	}

	@Override
	public void initViews() {
		initView();
		Intent intent = getIntent();
		m_userNames   = intent.getStringExtra("userName");

	}

	@Override
	public void initListener() {
		m_get_security_back.setOnClickListener(this);
		m_get_security_codeBtn.setOnClickListener(this);
		m_get_security_submit.setOnClickListener(this);
		m_phone_ks_close.setOnClickListener(this);
		m_select_login_close.setOnClickListener(this);
	}

	@Override
	public void initData() {
		bindCellPresenterImp = new BindCellPresenterImp();
		bindCellPresenterImp.attachView(this);
		sendCodePresenterImp = new SendCodePresenterImp();
		sendCodePresenterImp.attachView(this);
	}

	@Override
	public void processClick(View v) {
		int id = v.getId();
		if(id==R.id.get_security_back){ //返回
			camcel();
			m_activity.finish();
			m_activity = null ;

		}else if(id==R.id.get_security_code){ //验证码
			String cell_num = m_cellNum_et.getText().toString().trim(); //手机号
			if (isphone(cell_num)) return;
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());
				return ;
			}
			LoadingDialog.show(m_activity, "获取验证码中...", true);
			sendCodePresenterImp.SendCode(cell_num,m_activity);

		}else if(id==R.id.get_security_submit){ //获取到验证码，下一步
			// 成功
			String cell_num = m_cellNum_et.getText().toString().trim(); //手机号
			String security_code = m_security_code__et.getText().toString().trim(); //验证码
			if (isphone(cell_num)) return;
			if (!Util.isUserCode(m_activity,security_code)){
				return;
			}
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());
				return ;
			}

			LoadingDialog.show(m_activity, "绑定中...", true);
			bindCellPresenterImp.BindCellPhone(m_userNames,cell_num,security_code,m_activity);

		}else if (id==R.id.phone_ks_close){ //清除验证码
			m_security_code__et.setText("");

		}else if (id == R.id.select_login_close){ //关闭
			camcel();
			m_activity.finish();
			m_activity = null ;
		}

	}

	@Override
	public void showAppInfo(String msg, String data) {

	}

	@Override
	public void BindSuccess(int code, String data) {
		LoadingDialog.dismiss();
		switch (code) {
			case SDKStatusCode.SUCCESS:
				Util.ShowTips(m_activity,data);
				m_activity.finish();
				m_activity = null ;
				break;
			case SDKStatusCode.FAILURE:
				Util.ShowTips(m_activity,data);
				break;
			default:
				break;
		}
	}

	@Override
	public void BindFailed(int code, String data) {
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
	protected void onDestroy() {
		super.onDestroy();
		bindCellPresenterImp.detachView();
		sendCodePresenterImp.detachView();
	}

	//验证是否是手机
	private boolean isphone(String cell_num) {
		if (!Util.isUserPhone(m_activity,cell_num)){
			return true;
		}
		return false;
	}



	/*private Handler m_handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(10001==msg.what){
				m_get_security_codeBtn.setClickable(true);
				m_get_security_codeBtn.setText(R.string.mc_tips_48);
			}
			else if(10000==msg.what){
				String text =+m_time+"秒";

				m_get_security_codeBtn.setText(text);
			}
		}
	};*/


	private void countdownTimer(){
		m_get_security_codeBtn.setEnabled(false);
		m_timer =  new Timer();
		// final Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mCount--;
						m_get_security_codeBtn.setText(String.valueOf(mCount)+"秒");
						m_get_security_codeBtn.setBackgroundColor(getResources().getColor(R.color.mc_kn_text));
						if (mCount<=0){
							m_get_security_codeBtn.setText("重新发送");
							m_get_security_codeBtn.setBackgroundColor(getResources().getColor(R.color.mc_Kn_Username));
							m_get_security_codeBtn.setEnabled(true);
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
