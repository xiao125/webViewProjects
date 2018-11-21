package com.game.sdk.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.game.sdk.activity.BindCellActivity;
import com.game.sdk.bean.Data;
import com.game.sdkproxy.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	private static long  mTime = System.currentTimeMillis() ;
	public static boolean  getAssetsFileflag = false;
	public static String[]  files = null;
	
	public static boolean  findNameInSet( final String[] set ,final String name ){
		boolean  flag = false ;
		if(0==set.length){
			
		}else{
			for(int i=0;i!=set.length;++i){
				String str = set[i];
				if(str.equals(name)){
					flag = true ;
				}
			}
		}
		return   flag ;
	}

	public static String getCurTimestamp() {
		String timeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
		return timeStamp;
	}

	public static void ShowTips(Context context, String strText) {
		if( TextUtils.isEmpty(strText) )
			return ;
		
		if(null==context){
			return ;
		}
		
		ShowTips(context, strText, Toast.LENGTH_LONG);
		
	}





	public static void ShowTips(Context context, String strText, int time) {
		
		if( TextUtils.isEmpty(strText) )
			return ;
		
		long nowTime = System.currentTimeMillis();
		if(nowTime - mTime<=1000*time){
			return ;
		}
		mTime = nowTime;
		Toast toast = Toast.makeText(context, strText, time);
		toast.show();
	}

	public static void hideEditTextWindow(Activity activity, EditText editText) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	public static String stringToUTF8(String str) {

		String result = null;

		try {
			result = new String(str.getBytes("utf-8"), "utf-8");

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	public static String unicodeToUtf8(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}
	
	public static String mapToString(Map<String, String> map)
	  {
	    if ((map == null) || (map.isEmpty()))
	    {
	      return null;
	    }

	    String string = "";

	    int index = 0;
	    for (String key : map.keySet())
	    {
	      string = string + key + "=" +  (String)map.get(key);
	      if (index != map.size() - 1)
	      {
	        string = string + "&";
	      }
	      ++index;
	    }

	    return string;
	  }
	
	public static String getJsonStringByName(String json , String name){
		
		try {
			JSONObject obj = new JSONObject(json);
			
			String retStr = obj.getString(name);
			
			if(retStr == null){
				return "";
			}
			return obj.getString(name);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getAppInfo(Activity activity) {
		JSONObject appInfo = new JSONObject();

		try {
			String pkName = activity.getPackageName();
			String versionName = activity.getPackageManager().getPackageInfo(
					pkName, 0).versionName;
			int versionCode = activity.getPackageManager().getPackageInfo(
					pkName, 0).versionCode;

			appInfo.put("packageName", pkName);
			appInfo.put("versionName", versionName);
			appInfo.put("versionCode", versionCode);

			// return pkName + "   " + versionName + "  " + versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return appInfo.toString();
	}
	
	public static Map<String, String> getSign( Map<String, String> update_params , String app_secret ){
		
		String sign = "";
		
		Map<String, String> update_paramsTmp = new TreeMap<String, String>( new Comparator<String>() {

			public int compare(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg0.compareTo(arg1);
			}
		} );
		
		for(Map.Entry<String, String> entry:update_params.entrySet()){    
			update_paramsTmp.put(entry.getKey(), entry.getValue());
		}   
		
		int    len = update_paramsTmp.size();
		int    count = 0;
		for(Map.Entry<String, String> entry:update_paramsTmp.entrySet()){
			
			count++;
			if(count==len){
				sign = sign+entry.getKey()+"="+entry.getValue();
			}else{
				sign = sign+entry.getKey()+"="+entry.getValue()+"&";
			}
			
		} 
		sign = sign+"&app_secret="+app_secret;
		KnLog.log("sign:"+sign);
		update_params.put("sign",Md5Util.getMd5(sign).toLowerCase());
		return 		update_params;
	}
	
	/*public static boolean isMobileNO(String mobiles){
		Pattern p = Pattern.compile("^(13[0-9]|14[57]|15[0-35-9]|17[6-8]|18[0-9])[0-9]{8}");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}*/

	/*public static boolean isMobileNO(String mobiles){
		Pattern p = Pattern.compile("^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}*/

	public static boolean isMobileNO(String mobiles){
		Pattern p = Pattern.compile("^.{11}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 验证手机格式
	 */
	public static boolean isMobile(String number) {
    /*
    移动：134、135、136、137、138、139、150、151、152、157(TD)、158、159、178(新)、182、184、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、170、173、177、180、181、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
		String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";// "[1]"代表下一位为数字可以是几，"[0-9]"代表可以为0-9中的一个，"[5,7,9]"表示可以是5,7,9中的任意一位,[^4]表示除4以外的任何一个,\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(number)) {
			return false;
		} else {
			//matches():字符串是否在给定的正则表达式匹配
			KnLog.log("==========手机格式============");
			return number.matches(telRegex);
		}
	}


	/**
	 * 判断账号长度
	 * @param name
	 * @return
	 */
	public static boolean NameLength(String name){
		Pattern p = Pattern.compile("^.{6,12}$");
		Matcher m = p.matcher(name);
		return m.matches();
	}

	/**
	 * 账号必须以字母开头
	 * @param name
	 * @return
	 */
	public static boolean isNameStart(String name){
		Pattern p = Pattern.compile("^[a-z|A-Z]{1}.{0,}$");
		Matcher m = p.matcher(name);
		return m.matches();
	}

	/**
	 * 判断账号是否符合
	 * @param name
	 * @return
	 */
	public static boolean isAccordName(String name){

		Pattern p = Pattern.compile("^[A-Za-z0-9_-]+$");
		Matcher m = p.matcher(name);
		return m.matches();

	}



	/**
	 * 判断密码长度
	 * @param password
	 * @return
	 */
	public static boolean PswLength(String password){

		Pattern p = Pattern.compile("^.{6,16}$");
		Matcher m = p.matcher(password);
		return m.matches();

	}



	/**
	 * 判断密码是否符合
	 * @param password
	 * @return
	 */
	public static boolean isAccordPasw(String password){

		Pattern p = Pattern.compile("^[A-Za-z0-9_-]+$");
		Matcher m = p.matcher(password);
		return m.matches();

	}




	//验证账号名
	public static boolean isName(Activity context,String name){

		if (TextUtils.isEmpty(name)) {
			Util.ShowTips(context,  context.getResources().getString(R.string.mc_tips_2) );
			return false;
		}

		if (!isNameStart(name)){
			Util.ShowTips(context, context.getResources().getString(R.string.mc_tips_1));
			return false;
		}

		if (!NameLength(name)){
			Util.ShowTips(context,context.getResources().getString(R.string.mc_tips_4) );
			return false;
		}


		if (!isAccordName(name)){
			Util.ShowTips(context, context.getResources().getString(R.string.mc_tips_3));
			return false;
		}

		if(!Util.isNetWorkAvailable(context.getApplicationContext())){
			Util.ShowTips(context,context.getResources().getString(R.string.mc_tips_34).toString());
			return false;
		}

		return true;

	}



	//验证密码
	public static boolean isUserPassword(Activity context,String password){


		if (TextUtils.isEmpty(password)) {
			Util.ShowTips(context,  context.getResources().getString(R.string.mc_tips_8) );
			return false;
		}

		if (!PswLength(password)){
			Util.ShowTips(context,context.getResources().getString(R.string.mc_tips_5) );
			return false;
		}

		if (!isAccordPasw(password)){
			Util.ShowTips(context, context.getResources().getString(R.string.mc_tips_3_1));
			return false;
		}

		if(!Util.isNetWorkAvailable(context.getApplicationContext())){
			Util.ShowTips(context.getApplicationContext(),context.getResources().getString(R.string.mc_tips_34).toString());
			return false;
		}

		return true;

	}


	//验证手机号
	public static boolean isUserPhone(Activity context,String userphone) {

		if(TextUtils.isEmpty(userphone)){
			Util.ShowTips(context,context.getResources().getString(R.string.mc_tips_58));
			return false;
		}

		if(!Util.isMobileNO(userphone)){
			Util.ShowTips(context,context.getResources().getString(R.string.mc_tips_57));
			return false;
		}

		return true;
	}

	//判断验证码
	public static boolean isUserCode(Activity context,String code) {

		if(TextUtils.isEmpty(code)){
			Util.ShowTips(context,"验证码不能为空");
			return false;
		}

		return true;
	}



	//判断用户名与密码输入格式
	public static   void checkRegisterParams(Activity context, EditText userNameEt, EditText passWordEt) {

		//注意：判断顺序
		String username = userNameEt.getText().toString();

		if(!Util.isName(context,username)){

			return;
		}

		String password = passWordEt.getText().toString();

		if (!Util.isUserPassword(context,password)){
			return;
		}


		KnLog.log("用户名注册的密码="+password);

		LoadingDialog.show(context, "注册中...",true);



	}



	public static  boolean isNetWorkAvailable( Context  context ) {
		 KnLog.log("是否网络连接++");  
		try { 
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
	        if (connectivity != null) { 
	        	NetworkInfo info = connectivity.getActiveNetworkInfo();
	            if (info != null&& info.isConnected()) {
	            	if (info.getState() == NetworkInfo.State.CONNECTED) {
	            		KnLog.log("网络连接");
	                    return true; 
	                } 
	            } 
	        } 
	    } catch (Exception e) {   
	    	e.printStackTrace();
	    }
		KnLog.log("网络非连接");
	    return false; 
	}

	//----------------------------------------------------------------------------

	//获取Assets/SDKFile下的文件
	public static void initAssetsFile(Context context){

		if (getAssetsFileflag) {

		} else {
			getAssetsFileflag = true;
			AssetManager assetManager = context.getResources().getAssets();
			try {
				files = assetManager.list("SDKFile");
			} catch (IOException e) {
				KnLog.e(e.getMessage());
			}
		}
	}

	public static String getAssetsFileContent(Context context, String filePath) {
		initAssetsFile(context);
		String result = "";
//	    KnLog.e("assert下所有文件："+filePath.toString());
		for(int i=0; i<files.length; i++){

//	    	KnLog.e(" "+files[i]);
			if(filePath.endsWith(files[i])){
				try {
					// 获取本地asset目录下adChannel.txt中的参数
					InputStreamReader inputReader = new InputStreamReader(context
							.getResources().getAssets().open(filePath));
					BufferedReader bufReader = new BufferedReader(inputReader);
					String line = "";

					while ((line = bufReader.readLine()) != null) {
						result += line;
					}
				} catch (FileNotFoundException e) {
					KnLog.e("not found " + filePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}



	public static String getAdchannle( Context context ){

		//		首先判断是否是易接工具接入的adchannel读取manifest.xml配置文件
		Context ctx = null ;
		if(null==context){
			ctx = Data.getInstance().getApplicationContex();
		}else{
			ctx = context ;
		}

		ApplicationInfo ai;
		String adChannel = null ;
		try {
			ai = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			if(null==bundle){
				KnLog.e("bundle is null");
			}else{
				if(bundle.containsKey("adChannel")){
					adChannel = bundle.get("adChannel").toString();
				}else{
					KnLog.e("adChannel is null");
				}
			}

		} catch (PackageManager.NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(adChannel==null){

			//			否则返回adchannel.png中的adChannel值
			String jsonStr = getAssetsFileContent(ctx,"SDKFile/adChannel.png");
			KnLog.i("正常渠道打包---------->>>>>>>>adchannel:"+getJsonStringByName(jsonStr,"adChannel"));
			return getJsonStringByName(jsonStr,"adChannel");

		}else{

			KnLog.i("易接工具打包---------->>>>>>>>adchannel:"+adChannel);
			return adChannel ;

		}

	}



	public static String getGameName( Context ctx ){

		String jsonStr = getAssetsFileContent(ctx,"SDKFile/adChannel.png");
		return getJsonStringByName(jsonStr,"game");

	}

	public static String getChannle( Context context ){

		Context ctx = null ;
		if(null==context){
			ctx = Data.getInstance().getApplicationContex();
		}else{
			ctx = context ;
		}

		String jsonStr = getAssetsFileContent(ctx,"SDKFile/adChannel.png");
		String version_channel = getJsonStringByName(jsonStr,"version_channel");

		if(!version_channel.equals("") && version_channel!=null && !version_channel.equals(" ") ){

			return version_channel ;

		}

		KnLog.e("渠道名称:"+getJsonStringByName(jsonStr,"channel"));
		return getJsonStringByName(jsonStr,"channel");

	}

	public static String getChannel( Context context ){

		Context ctx = null ;
		if(null==context){
			ctx = Data.getInstance().getApplicationContex();
		}else{
			ctx = context ;
		}

		String jsonStr = getAssetsFileContent(ctx,"SDKFile/adChannel.png");
		String version_channel = getJsonStringByName(jsonStr,"version_channel");

		if(!version_channel.equals("") && version_channel!=null && !version_channel.equals(" ") ){

			return version_channel ;

		}

		return getJsonStringByName(jsonStr,"channel");



	}

	public static String getSplash(Context ctx){

		String jsonStr = getAssetsFileContent(ctx,"SDKFile/adChannel.png");
		return getJsonStringByName( jsonStr , "kuniu_splash" );

	}

	public static boolean fileExits( Context ctx , String fileName ){

		boolean       isExits       = false ;
		initAssetsFile(ctx);
		for(int i=0; i<files.length; i++){

//	    	KnLog.e(" "+files[i]);
			if(fileName.endsWith(files[i])){
				isExits = true;
			}
		}
//    	AssetManager  assetManager  = ctx.getAssets();
//    	InputStream   inputS 		=  null ;
//    	boolean       isExits       = false ;
//
//    	try {
//			inputS  					= assetManager.open(fileName);
//			isExits = true;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			isExits = false ;
//		}

		return isExits;

	}



	public static String getAdChannel() {

		String adChannel = "";
		try {
			// 获取本地asset目录下adChannel.txt中的渠道号。方便反编译使用
			InputStreamReader inputReader = new InputStreamReader(Data
					.getInstance().getApplicationContex().getResources()
					.getAssets().open("SDKFile/adChannel.png"));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String Result = "";
			while ((line = bufReader.readLine()) != null) {
				Result += line;
			}
			if (!Result.equals("")) {
				adChannel = Result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		KnLog.i("<=============adChannel===============>" + adChannel);

		return adChannel;

	}

	//获取当前时间
	public static String getTimes(){
		//获取随机有户名
		SimpleDateFormat formatter   =   new   SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate =  new Date(System.currentTimeMillis());
		String   time  =   formatter.format(curDate);
		return  time;
	}

	//获取手机分辨率
	public static String  ImageGalleryAdapter(Context c) {
		DisplayMetrics dm = c.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		return  screenWidth+"/"+ screenHeight;
	}

	/*
   * 获取手机内存总大小
   * @return
   */
	public static String getTotalMemorySize() {
		try {

			FileReader fr = new FileReader("/proc/meminfo");
			BufferedReader br = new BufferedReader(fr, 2048);
//         String memoryLine = br.readLine();
			String subMemoryLine = "";
			String Line = "";
			while ((Line = br.readLine()) != null)
			{
				if (Line.contains("MemTotal:")){
					subMemoryLine = Line.substring(Line.indexOf("MemTotal:"));
					break;
				}

			}
			br.close();
			Matcher mer = Pattern.compile("^[0-9]+$").matcher(subMemoryLine.replaceAll("\\D+", ""));
			//如果为正整数就说明数据正确的，确保在Double.parseDouble中不会异常
			if (mer.find()) {
				long memSize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) ;
				double mem = (Double.parseDouble(memSize + "")/1024)/1024;
				NumberFormat nf = new DecimalFormat( "0.0 ");
				mem = Double.parseDouble(nf.format(mem));
				//Log.e(LOG_TAG,"=========mem================ " + mem);
				return String.valueOf(mem+"G");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Unavailable";
	}

	/**
	 * 获取当前手机系统版本号
	 *
	 * @return  系统版本号
	 */
	public static String getSystemVersion() {
		return android.os.Build.VERSION.RELEASE;
	}


	//提示弹出窗
	public static  void  SetDialogs(final Activity activity, String name, String password,String Spname){

		final  String Spnames = Spname;
		final  String names = name;
		final  String paw = password;
		LayoutInflater inflater = LayoutInflater.from(activity);
		View v = inflater.inflate(R.layout.mc_bind_mobile_dialog_ts, null); //绑定手机
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.visit_dialog);
		final AlertDialog dia = new AlertDialog.Builder(activity).create();
		Button bind = (Button) v.findViewById(R.id.visit_bind_account); //下次再说
		Button cont = (Button) v.findViewById(R.id.visit_continue);//立刻绑定
		TextView ts = (TextView) v.findViewById(R.id.ts);
		ImageView close = (ImageView)v.findViewById(R.id.mc_da_lose);//关闭
		CheckBox mcheckBox = (CheckBox)v.findViewById(R.id.mc_tx);//选择今日不提醒
		if(activity!=null){
			dia.show();
			dia.setContentView(v);
		}
		cont.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DBHelper.getInstance().insertOrUpdateUser(names,paw);
				Intent intent = new Intent( activity, BindCellActivity.class);
				intent.putExtra("userName", names);
				activity.startActivity(intent);
				if (null == activity) {
				} else {
					dia.dismiss();
					activity.finish();
				}
			}
		});

		//稍后绑定
		bind.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null == activity) {

				} else {
					//KnLog.log("读取标识："+TodayTimeUtils.getChecked(activity));
					if(TodayTimeUtils.getChecked(activity).equals("true")){
						//KnLog.log("保存今日不提醒账号1111:isChecked="+TodayTimeUtils.getChecked(activity)+"  Spnames="+Spnames+"  names+"+names);
						//保存勾选后的日期
						TodayTimeUtils.saveExitTime(activity);
						TodayTimeUtils.saveExitName(activity,Spnames,names);
						TodayTimeUtils.setChecked(activity,"false");
						Util.ShowTips(activity,names+",已登录成功");
						dia.dismiss();
						activity.finish();
					}else {
						//KnLog.log("保存今日不提醒账号2222:isChecked="+TodayTimeUtils.getChecked(activity)+"  Spnames="+Spnames+"  names="+names);
						Util.ShowTips(activity,names+",已登录成功");
						dia.dismiss();
						activity.finish();
					}
				}
			}
		});

		//关闭AlertDialog
		close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dia.dismiss();
				activity.finish();
			}
		});

		//选择提醒
		mcheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					TodayTimeUtils.setChecked(activity,"true");

				}else {
					TodayTimeUtils.setChecked(activity,"false");
				}
			}
		});

	}



}