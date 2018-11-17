package com.game.sdk.util;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.game.sdkproxy.R;
public class LoadingDialog {
	
	public static Dialog loadingDialog;
	
	
	/**
	 * 得到自定义的progressDialog
	 * 
	 * @param
	 * @param msg
	 *            显示内容
	 * @param cancelable
	 *            是否可以取消dialog
	 * @return
	 */
	public static Dialog show( Activity activity , String msg,
			boolean cancelable) {
		
		dismiss();
		
		if(null==activity){
			
			return null;
			
		}else{
			
			LayoutInflater inflater = LayoutInflater.from(activity);
			View v = inflater.inflate(R.layout.mc_loading_dialog, null);
			LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);

			ImageView spaceshipImage = (ImageView) v.findViewById(R.id.loading_img);
			TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);
			// 加载动画
			Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
					activity, R.anim.mc_loading_animation);
			// 使用ImageView显示动画
			spaceshipImage.startAnimation(hyperspaceJumpAnimation);
			tipTextView.setText(msg);// 设置加载信息

			loadingDialog = new Dialog(activity, R.style.mc_loading_dialog);

			loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
			
			if(null==loadingDialog){
				
				return null;
				
			}else{
				//	判断当前activity是否运行
				if(!activity.isFinishing()){
					loadingDialog.show();
					loadingDialog.setCancelable(cancelable);
					return loadingDialog;
				}else{
					//
					return null ; 
				}
				
			}
		
		}

	}
	
	
	public static void dismiss(){
		
		if(null==loadingDialog){
			
		}else{
		
			loadingDialog.dismiss();
			loadingDialog = null;
			
		}
	}
	
	
}
