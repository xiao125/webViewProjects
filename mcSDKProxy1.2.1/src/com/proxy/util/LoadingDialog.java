package com.proxy.util;


import com.game.sdkproxy.R;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class LoadingDialog {
	
	public static Dialog loadingDialog;
	
	
	/**
	 * 得到自定义的progressDialog
	 * 
	 * @param context
	 * @param msg
	 *            显示内容
	 * @param cancelable
	 *            是否可以取消dialog
	 * @return
	 */
	public static Dialog show(Context context, String msg,
			boolean cancelable) {
		
		dismiss();
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.mcpr_loading_dialog, null);
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);

		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.loading_img);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.mcpr_loading_animation);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		tipTextView.setText(msg);// 设置加载信息

		loadingDialog = new Dialog(context, R.style.loading_dialog);

		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		loadingDialog.show();
		loadingDialog.setCancelable(cancelable);
		return loadingDialog;

	}
	
	
	public static void dismiss(){
		if(loadingDialog != null )
		{
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}
	
	
}
