package com.proxy.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.proxy.Data;
import com.proxy.sdk.channel.GameApplication;
import com.proxy.sdk.channel.SDKConfig;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class Util {

	private static long  mTime = System.currentTimeMillis() ;
	private static Activity mActivity = null ;
	public static final  String LOGFILEPATH = "/sdcard/knsdkproxy/" ;
	public static final String LOGFILE = 	"kn.log" ;
	public static final String RESULT_INFO = 	"knresult.log" ;
	public static final String RESULT_INFO1 = 	"knresult1.log" ;
	public static final String RESULT_INFO2 = 	"knresult2.log" ;
	public static final String LOGFILEALLPATH = LOGFILEPATH+LOGFILE;
	public static final String RESFILEALLPATH = LOGFILEPATH+RESULT_INFO;
	public static final String RESFILEALLPATH1 = LOGFILEPATH+RESULT_INFO1;
	public static final String RESFILEALLPATH2 = LOGFILEPATH+RESULT_INFO2;
	public static String[]  files = null;
	public static boolean  getAssetsFileflag = false;

	public static String getCurTimestamp() {
		String timeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
		return timeStamp;
	}

	public static void ShowTips(Context context, String strText) {
		if (TextUtils.isEmpty(strText))
			return;

		ShowTips(context, strText, Toast.LENGTH_LONG);
	}


	public static void ShowTips(Context context, String strText, int time) {
		if (TextUtils.isEmpty(strText))
			return;

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

	//获取Assets/SDKFile下的文件
	public static void initAssetsFile(Context context){

		if (getAssetsFileflag) {

		} else {
			getAssetsFileflag = true;
			AssetManager assetManager = context.getResources().getAssets();
			try {
				files = assetManager.list("SDKFile");
			} catch (IOException e) {
				LogUtil.e(e.getMessage());
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
					LogUtil.e("not found " + filePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	public static String getApiappId( Context ctx ){

		String jsonStr = getAssetsFileContent(ctx,"SDKFile/config.png");
		return getJsonStringByName( jsonStr , "appid" );

	}

	public static String getApiprivateKey( Context ctx ){

		String jsonStr = getAssetsFileContent(ctx,"SDKFile/config.png");
		return getJsonStringByName( jsonStr , "privateKey" );

	}

	public static String getApipublicKey( Context ctx ){

		String jsonStr = getAssetsFileContent(ctx,"SDKFile/config.png");
		return getJsonStringByName( jsonStr , "publicKey" );

	}

	public static String getDataEyEAppId( Context ctx ){

		String jsonStr = getAssetsFileContent(ctx,"SDKFile/config.png");
		return getJsonStringByName( jsonStr , "DataEyEAppId" );

	}

	public static String getYunwaAppId( Context ctx ){

		String jsonStr = getAssetsFileContent(ctx,"SDKFile/config.png");
		return getJsonStringByNameNew( jsonStr , "YunwaAppId" );

	}

	public static String getDataEyechannelId( Context ctx ){

		String jsonStr = getAssetsFileContent(ctx,"SDKFile/config.png");
		return getJsonStringByName( jsonStr , "DataEyechannelId" );

	}

	public static String getBuglyappId( Context ctx){

		String jsonStr = getAssetsFileContent(ctx,"SDKFile/config.png");
		return getJsonStringByName( jsonStr , "bugly_appid" );

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
				LogUtil.e("bundle is null");
			}else{
				if(bundle.containsKey("adChannel")){
					adChannel = bundle.get("adChannel").toString();
				}else{
					LogUtil.e("adChannel is null");
				}
			}

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(adChannel==null){

			//			否则返回adchannel.png中的adChannel值
			String jsonStr = getAssetsFileContent(ctx,"SDKFile/adChannel.png");
			LogUtil.i("正常渠道打包---------->>>>>>>>adchannel:"+getJsonStringByName(jsonStr,"adChannel"));
			return getJsonStringByName(jsonStr,"adChannel");

		}else{

			LogUtil.i("易接工具打包---------->>>>>>>>adchannel:"+adChannel);
			return adChannel ;

		}

	}


	public static String getGameChanelParameter(String parameter){ //获取渠道参数

		String jsonStr = getAssetsFileContent(GameApplication.getInstance(),"SDKFile/adChannel.png");
		return getJsonStringByName(jsonStr,parameter);
	}


	public static String getGameAPPID(){ //appid

		String jsonStr = getAssetsFileContent(GameApplication.getInstance(),"SDKFile/adChannel.png");
		return getJsonStringByName(jsonStr,"appid");

	}

	public static String getGameAPPKEY( ){ //appkey

		String jsonStr = getAssetsFileContent(GameApplication.getInstance(),"SDKFile/adChannel.png");
		return getJsonStringByName(jsonStr,"appkey");
	}

	public static String getGameAPPNOTIFY(){ //回到地址

		String jsonStr = getAssetsFileContent(GameApplication.getInstance(),"SDKFile/adChannel.png");
		return getJsonStringByName(jsonStr,"notify");
	}


	public static String getGameAPPSECRET( ){ // APPSECRET
		String jsonStr = getAssetsFileContent(GameApplication.getInstance(),"SDKFile/adChannel.png");
		return getJsonStringByName(jsonStr,"appsecret");
	}

	public static String getGameName( Context ctx ){

		String jsonStr = getAssetsFileContent(ctx,"SDKFile/adChannel.png");
		return getJsonStringByName(jsonStr,"game");

	}

	//获取channel 渠道标识
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

		LogUtil.e("渠道名称:"+getJsonStringByName(jsonStr,"channel"));
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
		//“kuniu_splash”字段是打包系统配置SDK是否设置闪屏标识。 “1” ： 开启闪屏。 “0”：不需要闪屏
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

		LogUtil.i("<=============adChannel===============>" + adChannel);

		return adChannel;

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

	public static String getJsonStringByNameNew(String json, String name) {



		try {
			JSONObject obj = new JSONObject(json);

			String retStr = obj.getString(name);

			if (retStr == null) {
				return null ;
			}
			return obj.getString(name);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null ;
	}



	public static String getJsonStringByName(String json, String name) {



		try {
			JSONObject obj = new JSONObject(json);

			String retStr = obj.getString(name);

			if (retStr == null) {
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

	public static void exit( final Activity  act ,  DialogInterface.OnClickListener submit , DialogInterface.OnClickListener cancel  ){

		AlertDialog.Builder builder = new Builder(act);
		builder.setMessage("点击确认退出游戏");
		builder.setTitle("系统提示");
		builder.setPositiveButton("确定", submit );
		builder.setNegativeButton("取消", cancel );
		builder.create().show();

	}


	public static void sdklog( Activity act , String filePath , String logName , String resultInfoPath , String resultInfoPath1 , String resultInfoPath2 ){
		if(Data.getInstance().isDeBugMode()){
			mActivity = act ;
			mkFilePath(filePath, logName , resultInfoPath , resultInfoPath1 , resultInfoPath2 );
		}
	}

	public static void mkFilePath( String filePath , String fileName , String resultInfoPath , String resultInfoPath1 , String resultInfoPath2 ){
		File file = null ;
		mkRootDirectory(filePath);
		file = new File( filePath+fileName );
		if(file.exists()){
			file.delete();
		}
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		File fileRes = new File( filePath+resultInfoPath );
		if(fileRes.exists()){
			fileRes.delete();
		}
		if(!fileRes.exists()){
			try {
				fileRes.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		File fileRes1 = new File( filePath+resultInfoPath1 );
		if(fileRes1.exists()){
			fileRes1.delete();
		}
		if(!fileRes1.exists()){
			try {
				fileRes1.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		File fileRes2 = new File( filePath+resultInfoPath2 );
		if(fileRes2.exists()){
			fileRes2.delete();
		}
		if(!fileRes2.exists()){
			try {
				fileRes2.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void mkRootDirectory( String filePath ){

		File file = null ;
		file = new File(filePath);
		if(file.exists()){
			file.delete();
		}
		if(!file.exists()){
			file.mkdir();
		}

	}

	public static void writeErrorLog( String data ){
		writeLog(Util.LOGFILEALLPATH,data);
	}

	public static  void writeInfoLog( String data ){
		writeLog(Util.RESFILEALLPATH,data);
	}

	public static  void writeInfoLog1( String data ){
		writeLog(Util.RESFILEALLPATH1,data);
	}

	public static  void writeInfoLog2( String data ){
		writeLog(Util.RESFILEALLPATH2,data);
	}

	private static void writeLog( String fileaPath ,String data ){
		if(Data.getInstance().isDeBugMode()){
			ArrayList<String>  strs = readLog(fileaPath);
			for (String str : strs) {
				if(data.equals(str)){
					return ;
				}
			}

			try {

				File f = new File(fileaPath);
				if(!f.exists()){
					try {
						f.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f,true),"UTF-8");
					BufferedWriter writer=new BufferedWriter(write);
					data = data+"\n";
					byte[] bytes = data.getBytes();
					try {
						writer.write(data);
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static ArrayList<String> readLog( String fileaPath ){
		ArrayList<String>  arr = new ArrayList<String>();
		try {
			FileInputStream  fint  = new FileInputStream(fileaPath);
			try {
				InputStreamReader reader = new InputStreamReader(fint);
				BufferedReader breader = new BufferedReader(reader);
				String  str;
				while ((str = breader.readLine()) != null) {
					arr.add(str);
				}
				fint.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return arr;
	}

	public static  boolean isNetWorkAvailable( Context  context ) {

		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null&& info.isConnected()) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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
		update_params.put("sign",Md5Util.getMd5(sign).toLowerCase());
		return 		update_params;
	}

	public static String parseMapToString( Map<String, String> params ){

		TreeMap apiparamsMap = new TreeMap();
		for (String key : params.keySet()) {
			apiparamsMap.put(key, params.get(key));
		}

		StringBuilder param = new StringBuilder();
		for (Iterator it = apiparamsMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry e = (Map.Entry) it.next();
			param.append("&")
					.append((String) e.getKey())
					.append("=")
					.append(e.getValue() == null ? "" : URLEncoder
							.encode((String) e.getValue()));
		}

		return param.toString().substring(1);
	}

	/**
	 * 						微信包名:com.tencent.mm
	 * 						QQ包名   :
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	public static int getOritation(){

		int gameOri = Configuration.ORIENTATION_LANDSCAPE ;
		int oritation = Data.getInstance().getGameInfo().getScreenOrientation();

		if(0==oritation){

			gameOri = Configuration.ORIENTATION_LANDSCAPE ;

		}else if(1==oritation){

			gameOri = Configuration.ORIENTATION_PORTRAIT ;

		}

		return gameOri ;

	}

	public static  String    getYunwaAppId(){
		String YunwaAppId = null ;
		try {
			Field field  = SDKConfig.class.getDeclaredField("YunwaAppId") ;
			field.setAccessible(true);
			YunwaAppId = field.get(null).toString();
		} catch (Exception e) {
			System.out.println("not found the filed of YunwaAppId  121 ");
		}
		return YunwaAppId ;
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
		return  screenWidth+"*"+ screenHeight;
	}


	//读取需切换的参数
	public static Map<String, String> readHttpData(Context context){

		Map<String, String> pay_params = new HashMap<String, String>();
		File   appFile = context.getFilesDir();
		String appFilePath = appFile.getAbsolutePath()+"/Hmcsdk.log";
		FileInputStream fint;
		try {

			if(fileIsExists(appFilePath)){
				fint = new FileInputStream(appFilePath);
				InputStreamReader reader = new InputStreamReader(fint);
				BufferedReader breader = new BufferedReader(reader);
				String line = "";
				String Result = "";
				try {
					while ((line = breader.readLine()) != null) {
						Result += line;
					}
					LogUtil.log("Resultqqqqqqqqqqqqqqqq:"+Result);
					try {
						JSONObject  json = new JSONObject(Result);
						pay_params.put("game_id", json.getString("game_id"));
						pay_params.put("channel", json.getString("channel"));
						pay_params.put("platform", json.getString("platform"));
						pay_params.put("adchannel", json.getString("adchannel"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					fint.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return pay_params ;
	}



	//写入
	public static void writeHttpData(Context context, Map<String, String> pay_params ){
		File   appFile = context.getFilesDir();
		String appFilePath = appFile.getAbsolutePath()+"/Hmcsdk.log";
		File   file = new File( appFilePath );
		if(file.exists()){
			file.delete();
		}
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		OutputStreamWriter write;
		try {
			write = new OutputStreamWriter(new FileOutputStream(file,true),"UTF-8");
			BufferedWriter writer=new BufferedWriter(write);
			JSONObject  json = new JSONObject();
			try {
				json.put("game_id",pay_params.get("game_id").toString());
				json.put("channel", pay_params.get("channel").toString());
				json.put("platform", pay_params.get("platform").toString());
				json.put("adchannel", pay_params.get("adchannel").toString());

				String data = json.toString();
				byte[] bytes = data.getBytes();
				try {
					writer.write(data);
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}



	public static boolean fileIsExists( final String path ){
		try{
			File f=new File(path);
			if(!f.exists()){
				return false;
			}

		}catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
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

	/**
	 * 检测是否安装微信
	 *
	 * @param context
	 * @return
	 */
	public static boolean isWxInstall(Context context) {
		final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.tencent.mm")) {
					return true;
				}
			}
		}

		return false;
	}



}