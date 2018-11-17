package com.proxy.util;

import android.app.Activity;
import android.view.animation.AccelerateDecelerateInterpolator;

//			关于视屏和截屏的中间件工具实现

public class ScreenVideoTools {
	
	private static ScreenVideoTools  instance = null ;
	private static Activity          activity = null ;
	
	public static ScreenVideoTools getInstance(){
		if(instance==null){
			instance = new ScreenVideoTools();
		}
		return instance ;
	}
	
	private ScreenVideoTools(){
		
	}
	
	public void init( final Activity  act ){
		
		activity = act ;
		
		//			创建浮标
		
	}
	
	public void openTools(){
		
		//	开启浮标
		
	}
	
	public void closeTools(){
		
		//	关闭浮标
		
	}
	
	

}
