package com.game.sdk.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.game.sdk.GameSDK;
import com.game.sdk.bean.Data;
import com.game.sdk.openudid.OpenUDID_manager;

public class DeviceUtil
{
  private static Context mContext = Data.getInstance().getApplicationContex();

  private static final String NETWORKTYPE_INVALID = "invalid";
  private static final String NETWORKTYPE_WAP = "wap";
  private static final String NETWORKTYPE_WIFI = "wifi";
  private static final String NETWORKTYPE_2G = "2G";
  private static final String NETWORKTYPE_3G = "3G";
  
  public static String getPhoneType(){
	  return Build.MODEL;
  }


    //判断网络类型
    public static String getNetWorkType()
    {
        String strNetworkType = "";

        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取链接网络信息
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected())
        {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
            {
                strNetworkType = "WIFI";
            }
            else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                String _strSubTypeName = networkInfo.getSubtypeName();

                KnLog.w("Network getSubtypeName : " + _strSubTypeName);

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000"))
                        {
                            strNetworkType = "3G";
                        }
                        else
                        {
                            strNetworkType = _strSubTypeName;
                        }

                        break;
                }

                KnLog.w("Network getSubtype : " + Integer.valueOf(networkType).toString());
            }
        }

        KnLog.w("Network Type : " + strNetworkType);

        return strNetworkType;
    }



    public static boolean isWifi( Context context ){
        boolean  ret = false ;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if(type.equalsIgnoreCase("WIFI")){
                ret =  true  ;
            }else{
                ret =  false ;
            }
        }
        return ret ;
    }

 
 public static boolean isFastMobileNetwork(Context context) {  
	  TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);  
	  switch (telephonyManager.getNetworkType()) {  
	         case TelephonyManager.NETWORK_TYPE_1xRTT:  
	             return false; // ~ 50-100 kbps  
	         case TelephonyManager.NETWORK_TYPE_CDMA:  
	             return false; // ~ 14-64 kbps  
	         case TelephonyManager.NETWORK_TYPE_EDGE:  
	             return false; // ~ 50-100 kbps  
	         case TelephonyManager.NETWORK_TYPE_EVDO_0:  
	             return true; // ~ 400-1000 kbps  
	         case TelephonyManager.NETWORK_TYPE_EVDO_A:  
	             return true; // ~ 600-1400 kbps  
	         case TelephonyManager.NETWORK_TYPE_GPRS:  
	             return false; // ~ 100 kbps  
	         case TelephonyManager.NETWORK_TYPE_HSDPA:  
	             return true; // ~ 2-14 Mbps  
	         case TelephonyManager.NETWORK_TYPE_HSPA:  
	             return true; // ~ 700-1700 kbps  
	         case TelephonyManager.NETWORK_TYPE_HSUPA:  
	             return true; // ~ 1-23 Mbps  
	         case TelephonyManager.NETWORK_TYPE_UMTS:  
	             return true; // ~ 400-7000 kbps  
	         case TelephonyManager.NETWORK_TYPE_EHRPD:  
	             return true; // ~ 1-2 Mbps  
	         case TelephonyManager.NETWORK_TYPE_EVDO_B:  
	             return true; // ~ 5 Mbps  
	         case TelephonyManager.NETWORK_TYPE_HSPAP:  
	             return true; // ~ 10-20 Mbps  
	         case TelephonyManager.NETWORK_TYPE_IDEN:  
	             return false; // ~25 kbps  
	         case TelephonyManager.NETWORK_TYPE_LTE:  
	             return true; // ~ 10+ Mbps  
	         case TelephonyManager.NETWORK_TYPE_UNKNOWN:  
	             return false;  
	         default:  
	             return false;  
	      }  
	  }
  
  public static String getMacAddress()
  {
    String macAddress = null;
    try
    {
      WifiManager wifi = (WifiManager)mContext.getSystemService("wifi");

      WifiInfo info = wifi.getConnectionInfo();
      macAddress = info.getMacAddress();
    }
    catch (Exception e)
    {
      macAddress = null;
    }

    return macAddress;
  }

  public static String getIPAddress()
  {
    String ipAddress = null;
    try
    {
      WifiManager wifi = (WifiManager)mContext.getSystemService("wifi");

      WifiInfo info = wifi.getConnectionInfo();
      ipAddress = intToIp(info.getIpAddress());
    }
    catch (Exception e)
    {
      ipAddress = null;
    }

    return ipAddress;
  }

  private static String intToIp(int i)
  {
    return (i & 0xFF) + "." + 
      (i >> 8 & 0xFF) + "." + 
      (i >> 16 & 0xFF) + "." + 
      (i >> 24 & 0xFF);
  }
  
  public static String getDeviceId()
  {
    if (mContext == null)
    {
      KnLog.e("获取设备ID Context为空");
      return null;
    }

    String deviceId = null;
    try
    {
      TelephonyManager manager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
      if (manager != null)
      {
        deviceId = manager.getDeviceId();
      }

      if (TextUtils.isEmpty(deviceId))
      {
        deviceId = OpenUDID_manager.getOpenUDID();
      }

    }
    catch (Exception e)
    {
      deviceId = null;
    }

    if (TextUtils.isEmpty(deviceId))
    {
      deviceId = "default_device_id";
    }

    return deviceId;
  }
  
}
