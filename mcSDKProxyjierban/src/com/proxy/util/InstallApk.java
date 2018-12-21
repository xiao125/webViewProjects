package com.proxy.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;


public class InstallApk {
    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    
    /**
     * 从assert中安装apk
     * @param fileName apk的文件名
     * */
    public static boolean InstallApkFromAssert(final Context context, final String fileName) {
        if (null == context || null == fileName || "".equals(fileName)) {
            return false;
        }
        if(copyApkFromAssets(context, fileName, path + "/" + fileName)){
            
            //此处弹出一个确认框给用户，显示是否安装引用
			Builder mBuilder = new AlertDialog.Builder(context)
            .setMessage("《少年四大名捕》终极攻略，助您创关！特权礼包，领到手软！")
            .setPositiveButton("免流量安装", new OnClickListener() {
                @Override  
                public void onClick(DialogInterface dialog, int which) {
                    //用户点击确定，开始安装apk
                    Intent intent = new Intent(Intent.ACTION_VIEW);  
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
                    intent.setDataAndType(Uri.parse("file://" + path + "/" + fileName), "application/vnd.android.package-archive");  
                    context.startActivity(intent);
                    }  
                })
              .setNegativeButton("有钱任性", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    
                }
            });  
			mBuilder.show();
            
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 将打包在assert中的apk应用拷贝到手机内存中
     *  @param fileName apk的文件名
     */
    public static boolean copyApkFromAssets(Context context, String fileName, String path) {
        boolean copyIsFinish = false;  
        try {  
            InputStream is = context.getAssets().open(fileName);  
            File file = new File(path);  
            file.createNewFile();  
            FileOutputStream fos = new FileOutputStream(file);  
            byte[] temp = new byte[1024];  
            int i = 0;  
            while ((i = is.read(temp)) > 0) {  
                fos.write(temp, 0, i);  
            }  
            fos.close();  
            is.close();  
            copyIsFinish = true;  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return copyIsFinish;  
    }

    /**
	 * 根据应用的包名判断应用是否安装
	 * 
	 * @param packageName
	 *            应用的包名(攻略apk的包名)
	 * @return true:已安装 false:未安装
	 */
    public static boolean isApkInstall(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName,PackageManager.GET_ACTIVITIES);
            return true; //应用已安装
        } catch(PackageManager.NameNotFoundException e) {
            return false;//应用未安装
        }
    }
}