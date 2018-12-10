package com.game.sdk.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.game.sdk.GameSDK;
import com.game.sdk.util.DBHelper;
import com.game.sdkproxy.R;

public class SelecteLoginActivity extends Activity implements OnClickListener {
private ImageButton account_login,ks_login;
private ImageView imageView;
private Activity activity;
private String username;
private boolean isFirstLogin=false;

	//声明一个SharedPreferences对象和一个Editor对象
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;

	private  String ExtrsId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
/*
		if(GameSDK.getInstance().ismScreenSensor()){

		}else{
			setRequestedOrientation(GameSDK.getInstance().getmOrientation());
		}*/

		setContentView(R.layout.mc_activity_selecte_login);
		activity=this;

		ExtrsId = getIntent().getStringExtra("logout");

		initView();

		//创建sp存储
		preferences = getSharedPreferences("visit",MODE_PRIVATE);
		editor= preferences.edit();

		String[] usernames = DBHelper.getInstance().findAllUserName(); //数据库查询是否有注册过的账号
		if (usernames.length == 0) {
			/*imageView.setBackgroundResource(R.drawable.auto_login_cancel);*/
			isFirstLogin=true;
		}



	}


	private void initView(){


		account_login=(ImageButton) findViewById(R.id.select_login_account_login); //已有账号
		imageView=(ImageView) findViewById(R.id.select_log_close); //返回
		ks_login = (ImageButton) findViewById(R.id.select_login_ks_login); //快速登录

		imageView.setOnClickListener(this);
		account_login.setOnClickListener(this);
		ks_login.setOnClickListener(this);


	}


	@Override
	public void onClick(View v) {

		int id = v.getId();
		if(id == R.id.select_log_close){


			if (isFirstLogin==true) {
				if(activity!=null){
					activity.finish();
					activity = null ;
				}
			}
			else{
				Intent intent=new Intent(activity, AutoLoginActivity.class); //账号登录
				startActivity(intent);
				activity.finish();
				activity=null;
			}


		}else if ( id ==R.id.select_login_ks_login){

			Intent intent=new Intent(SelecteLoginActivity.this, FastLoginActivity.class);
			intent.putExtra("logout",ExtrsId);
			startActivity(intent);
			if (activity!=null) {
				activity.finish();
			}

		}else if (id == R.id.select_login_account_login){

			/*Intent intent=new Intent(SelecteLoginActivity.this, FirstLoginActivity.class);
			intent.putExtra("logout",ExtrsId);
			startActivity(intent);
			if (activity!=null) {
				activity.finish();
			}*/

		}


	}
}
