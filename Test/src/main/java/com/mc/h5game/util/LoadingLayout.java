package com.mc.h5game.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.LayoutRes;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.proxy.util.LogUtil;
import com.rxcqh5.cs.mc.R;

/**
 *
 */

public class LoadingLayout extends FrameLayout {

    private ImageView mIvLoading;
    private Button mBtnReTry;
    private RelativeLayout mLoadingView;
    private ProgressBar progesss;
    private TextView progesssValue;
    private AnimationDrawable mAnimationDrawable;
    private LoadingFramelayout.OnReloadListener mOnReloadListener;

    //构造方法，在我们需要网络加载页面是调用，第二个传入的一个布局
    public LoadingLayout(Context context) {
        super(context);
        LayoutInflater mInflater = LayoutInflater.from(context);
        View rootView =  mInflater.inflate(R.layout.loading_layout,this);
        LogUtil.log("加载进度条");
        mLoadingView = (RelativeLayout) rootView.findViewById(R.id.load_view); //布局最外层 LinearLayout
        progesss = (ProgressBar)rootView.findViewById(R.id.progesss1);

        progesss.setProgress(66);
        /*progesssValue.post(new Runnable() {
            @Override
            public void run() {
                DisplayMetrics dm = getResources().getDisplayMetrics();
                //  int w = getWindowManager().getDefaultDisplay().getWidth();
                int w = dm.widthPixels;
                Log.e("w=====", "" + w);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) progesssValue.getLayoutParams();
                int pro = progesss.getProgress();
                int tW = progesssValue.getWidth();
                if (w * pro / 100 + tW * 0.3 > w) {
                    params.leftMargin = (int) (w - tW * 1.1);
                } else if (w * pro / 100 < tW * 0.7) {
                    params.leftMargin = 0;
                } else {
                    params.leftMargin = (int) (w * pro / 100 - tW * 0.7);
                }
                progesssValue.setLayoutParams(params);
            }
        });*/

       // init(context);
    }

    //这里是一个动画效果，我们这里采用6张不同加载时候的图片
    private void init(final Context context) {

        // ImageView spaceshipImage = (ImageView) v.findViewById(R.id.loading_img);
        // TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);
        // 加载动画
        /*Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.pmc_loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText("拼命加载游戏中.....");// 设置加载信息

        loadingDialog = new Dialog(context, R.style.loading_dialog);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        loadingDialog.show();
        loadingDialog.setCancelable(false); //不可以用“返回键”取消*/

    }





    //这一个用于完成加载的调用方法
    public void completeLoading(){
        post(new Runnable() {
            @Override
            public void run() {
                mLoadingView.setVisibility(GONE);
            }
        });
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
        post(new Runnable() {
            @Override
            public void run() {
                mIvLoading.setImageResource(com.game.sdkproxy.R.drawable.pmc_erro);
                mBtnReTry.setVisibility(VISIBLE);
            }
        });
    }
    //在重新加载的时候用来回调重新加载的方法
    public void setOnReloadListener(LoadingFramelayout.OnReloadListener onReloadListener) {
        mOnReloadListener = onReloadListener;
    }
    //实现接口，重新加载用作回调
    public interface OnReloadListener {
        void onReload();
    }

}
