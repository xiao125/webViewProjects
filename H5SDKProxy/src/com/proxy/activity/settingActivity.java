package com.proxy.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.game.sdkproxy.R;
import com.proxy.OpenSDK;
import com.proxy.Data;
import com.proxy.bean.FuncButton;

public class settingActivity extends Activity{
	
	public Button settingItemView(final FuncButton btn){
		Button button = new Button(this);
		button.setBackgroundResource(R.drawable.pmc_setting_btn);
		button.setText(btn.getName());
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				btn.getOnClickListener().onClick();
				
				if(btn.isClosePage()){
					settingActivity.this.finish();
				}
				
			}
		});
		
		LinearLayout.LayoutParams btn_params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		btn_params.gravity = Gravity.LEFT | Gravity.CENTER_HORIZONTAL;  
		btn_params.setMargins(0, 20, 0, 0);
		button.setLayoutParams(btn_params);
		
		return button;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRequestedOrientation(Data.getInstance().getGameInfo().getScreenOrientation());
		
		//mask
		View mask = new View(this);
		mask.setBackgroundColor(Color.parseColor("#000000"));
		mask.setAlpha(0.5f);
		
		FrameLayout.LayoutParams mask_params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mask_params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;  
		
		mask.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				settingActivity.this.finish();
			}
		});
		
		this.addContentView(mask, mask_params);
		
		
		//layout
		FuncButton btns[] = OpenSDK.getInstance().getSettingItems();
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;  
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(1);
		
		for (int i = 0; i < btns.length; i++) {
			Button view = settingItemView(btns[i]);
			layout.addView(view);
		}
		
		this.addContentView(layout, params);
	}
}
