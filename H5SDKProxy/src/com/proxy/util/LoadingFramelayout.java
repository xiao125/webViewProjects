package com.proxy.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.game.sdkproxy.R;

/**
 * Framelayout来切换布局
 */

public class LoadingFramelayout extends FrameLayout {

    public static Dialog loadingDialog;
    private ImageView mIvLoading;
    private Button mBtnReTry;
    private LinearLayout mLoadingView;
    private AnimationDrawable mAnimationDrawable;
    private OnReloadListener mOnReloadListener;

    //构造方法，在我们需要网络加载页面是调用，第二个传入的一个布局
    public LoadingFramelayout(Context context, @LayoutRes  int res) {
        super(context);
        LayoutInflater mInflater = LayoutInflater.from(context);
        mInflater.inflate(res,this);
        View rootView = mInflater.inflate(R.layout.pmc_no_network,this);
        mLoadingView = (LinearLayout) rootView.findViewById(R.id.load_view); //布局最外层 LinearLayout
        mIvLoading = (ImageView) rootView.findViewById(R.id.iv_loading);
        mBtnReTry = (Button) rootView.findViewById(R.id.btn_retry);
        mBtnReTry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnReloadListener.onReload();
            }
        });
        init(context);
    }
    // 构造方法，在我们需要网络加载页面是调用，第二个传入的一个View
    public LoadingFramelayout(Context context,View view) {
        super(context);
        addView(view);
        LayoutInflater mInflater = LayoutInflater.from(context);
        View rootView = mInflater.inflate(R.layout.pmc_no_network,this);
        mLoadingView = (LinearLayout) rootView.findViewById(R.id.load_view);
        mIvLoading = (ImageView) rootView.findViewById(R.id.iv_loading);
        mBtnReTry = (Button) rootView.findViewById(R.id.btn_retry);
        mBtnReTry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnReloadListener.onReload();
            }
        });
        init(context);
    }
    //这里是一个动画效果，我们这里采用6张不同加载时候的图片
    private void init(Context context) {
       /* mAnimationDrawable = new AnimationDrawable();
        mAnimationDrawable.setOneShot(false);
        mAnimationDrawable.addFrame(ContextCompat.getDrawable(context,R.drawable.pmc_loading_dialog),100);
        mAnimationDrawable.addFrame(ContextCompat.getDrawable(context,R.drawable.pmc_loading_dialog),100);
        mAnimationDrawable.addFrame(ContextCompat.getDrawable(context,R.drawable.pmc_loading_dialog),100);
        mAnimationDrawable.addFrame(ContextCompat.getDrawable(context,R.drawable.pmc_loading_dialog),100);
        mAnimationDrawable.addFrame(ContextCompat.getDrawable(context,R.drawable.pmc_loading_dialog),100);
        mAnimationDrawable.addFrame(ContextCompat.getDrawable(context,R.drawable.pmc_loading_dialog),100);
        mIvLoading.setImageDrawable(mAnimationDrawable);
        mIvLoading.post(new Runnable() {
            @Override
            public void run() {
                mAnimationDrawable.start();
            }
        });*/

        stopLoading();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.pmc_loading_dialog, null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.loading_img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.pmc_loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText("拼命加载游戏中.....");// 设置加载信息

        loadingDialog = new Dialog(context, R.style.loading_dialog);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        loadingDialog.show();
        loadingDialog.setCancelable(false); //不可以用“返回键”取消

    }
    //这一个用于完成加载的调用方法
    public void completeLoading(){
        stopLoading();
        post(new Runnable() {
            @Override
            public void run() {
                mLoadingView.setVisibility(GONE);
            }
        });
    }
    //成功和失败后停着加载，将动画stop
    private void stopLoading() {
        //((AnimationDrawable)mIvLoading.getDrawable()).stop();
        if(loadingDialog != null )
        {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
    //在加载失败后，点击加载再次加载的时候调用
    public void startLoading(final Context context){
        post(new Runnable() {
            @Override
            public void run() {
                mBtnReTry.setVisibility(GONE);
                //mIvLoading.setImageDrawable(mAnimationDrawable);
               // ((AnimationDrawable)mIvLoading.getDrawable()).start();
                init(context);
            }
        });
    }
    //加载失败后，停着动画，将失败页面和重新加载按钮Visible出来
    public void loadingFailed(){
        stopLoading();
        post(new Runnable() {
            @Override
            public void run() {
                mIvLoading.setImageResource(R.drawable.pmc_erro);
                mBtnReTry.setVisibility(VISIBLE);
            }
        });
    }
    //在重新加载的时候用来回调重新加载的方法
    public void setOnReloadListener(OnReloadListener onReloadListener) {
        mOnReloadListener = onReloadListener;
    }
    //实现接口，重新加载用作回调
    public interface OnReloadListener {
        void onReload();
    }



}
