package com.proxy.activity;



import java.util.HashMap;
import java.util.Map;

import com.game.sdkproxy.R;
import com.proxy.OpenSDK;
import com.proxy.Data;
import com.proxy.sdk.channel.SdkChannel;
import com.proxy.util.LogUtil;
import com.proxy.util.Md5Util;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class ActivationCodeAc extends Activity {
	
	public EditText  jihuoEdt = null;
	private     String  m_appKey="EwPjaV9sNgnFRdLWu2rJOXbGQ7ykI34H";
	private     String  m_gameId="test";
	private     String  m_gameName="test";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mcpr_acv);
//		jihuoEdt = (EditText)findViewById(R.id.jihuo_input_edt);
//		setRequestedOrientation(1);
		
	}

	

	public void Onsubmit(View vt){
				
		String jihuostr = jihuoEdt.getText().toString();

		Data  knData = Data.getInstance();
		String uid = knData.getGameUser().getUid();
		String gameName = knData.getGameInfo().getGameName();
		String channel = knData.getGameInfo().getChannel();
		String server_id = Integer.toString(knData.getGameUser().getServerId());
		String cdkey = jihuostr;
		String vip = knData.getGameUser().getVipLevel();
		String level = Integer.toString(knData.getGameUser().getUserLevel());
		
		vip = "10";
		//jihuostr = "46BD247A6C35173D";
		
		Map<String , Object > data = new HashMap<String, Object>();
		data.put("m","giftpack");
		data.put("a","useCode");
		data.put("uid",uid);
		data.put("game",gameName);
		data.put("channel",channel);
		data.put("zone","1");
		data.put("server_id",server_id);
		data.put("cdkey",jihuostr);
		data.put("vip",vip);
		data.put("level",level);
		
		String open_id =  knData.getUser().getOpenId();
		
		String app_id = "1004";
		if(app_id.isEmpty()){
			app_id = "1004";
		}
		
		open_id="dc4f54a890c11841e3fa3c68baf4e7f0";
		data.put("app_id",app_id);
		data.put("open_id",open_id);
		data.put("send","1");
		String sign = "a=useCode"+"&app_id="+app_id+"&cdkey="+jihuostr
		+"&channel="+channel+"&game="+gameName+"&level="+level
		+"&m=giftpack"+"&open_id="+open_id+"&send=1"
		+"&server_id="+server_id
		+"&uid="+uid+"&vip="+vip+"&zone=1"+"&app_secret=490f001617b0315ab9149786776ce5c4";
		
		String signMd5 = Md5Util.getMd5(sign);
		data.put("sign",signMd5.toLowerCase());
		
		SdkChannel.getInstance().pushActivation(this,data);
		
	}
	
	
	
	
	
	public void Onclose(View vt){
		
		this.finish();
	
	}
	
	public void OninPut(View vt){
	
		jihuoEdt.setText("");
	
	}

}
