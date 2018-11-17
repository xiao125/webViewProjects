package com.game.sdk.pay;

import android.app.Activity;
import android.text.TextUtils;

import com.game.sdk.bean.PayInfo;
import com.game.sdk.listener.PayListener;
import com.game.sdk.util.KnLog;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class PAY_API {
	/*
	private String appid;
	private String appkey;
	private String publicKey;
	
	private static PAY_API inst = null;
	
	
	public static PAY_API getInstance(){
		if(inst==null){
			inst = new PAY_API();
		}
		return inst;
	}
	
	
	public void init( final Activity activity , int orientation , String appid , String appkey , String publicKey ){
		this.appid = appid;
		this.appkey = appkey;
		this.publicKey = publicKey;

	//	IAppPay.init(activity, orientation, this.appid); //爱贝支付sdk初始化

	}
	
	public void pay(final Activity activity, final PayInfo payInfo,final PayListener payListener){
		
		startPay( activity, genUrl( payInfo.getUid(), payInfo.getExorderno(), payInfo.getWaresid(), payInfo.getPrice()/100 , payInfo.getExorderno() ,payInfo.getProductName() ),payListener );
		
	}
	
	*//**发起支付*//*
	private void startPay( final Activity activity , String param ,final PayListener payListener) {
    	IAppPay.startPay(activity , param, new IPayResultCallback() {
			
			@Override
			public void onPayResult(int resultCode, String signvalue, String resultInfo) {
				// TODO Auto-generated method stub
				
				switch (resultCode) {
				case IAppPay.PAY_SUCCESS:
					payListener.onSuccess(resultInfo);
					dealPaySuccess(signvalue);
					break;
				case IAppPay.PAY_CANCEL:
					//Toast.makeText(activity, "成功下单", Toast.LENGTH_LONG).show();
					break ;
				default:
					payListener.onFail(resultInfo);
					dealPayError(resultCode, resultInfo);
					break;
				}
				KnLog.i("requestCode:"+resultCode + ",signvalue:" + signvalue + ",resultInfo:"+resultInfo);
			}
			
			
			*//*4.支付成功。
			 *  需要对应答返回的签名做验证，只有在签名验证通过的情况下，才是真正的支付成功
			 * 
			 * *//*
			private void dealPaySuccess(String signValue) {
				KnLog.i("sign = " + signValue);
				if (TextUtils.isEmpty(signValue)) {
					*//**
					 *  没有签名值
					 *//*
					KnLog.i("pay success,but it's signValue is null");
					//Toast.makeText(activity, "pay success, but sign value is null", Toast.LENGTH_LONG).show();
					return;
				}

				boolean isvalid = false;
				try {
					isvalid = signCpPaySuccessInfo(signValue);
				} catch (Exception e) {
				
					isvalid = false;
				}
				if (isvalid) {
					//Toast.makeText(activity, "pay success", Toast.LENGTH_LONG).show();
				} else {
					//Toast.makeText(activity, "pay success, sign error", Toast.LENGTH_LONG).show();
				}

			}

			*//**
			 * valid cp callback sign
			 * @param signValue
			 * @return
			 * @throws Exception
			 * 
			 * transdata={"cporderid":"1","transid":"2","appid":"3","waresid":31,
			 * "feetype":4,"money":5,"count":6,"result":0,"transtype":0,
			 * "transtime":"2012-12-12 12:11:10","cpprivate":"7",
			 * "paytype":1}&sign=xxxxxx&signtype=RSA
			 *//*
			private boolean signCpPaySuccessInfo(String signValue) throws Exception {
				int transdataLast = signValue.indexOf("&sign=");
				String transdata = URLDecoder.decode(signValue.substring("transdata=".length(), transdataLast));
				
				int signLast = signValue.indexOf("&signtype=");
				String sign = URLDecoder.decode(signValue.substring(transdataLast+"&sign=".length(),signLast));
				
				String signtype = signValue.substring(signLast+"&signtype=".length());
				
				if (signtype.equals("RSA") 
						&&RSAHelper.verify(transdata, publicKey, sign)) {
				
					return true;
				}else{
					KnLog.i("wrong type ");
				}
				return false;
			}

			private void dealPayError(int resultCode, String resultInfo) {
				KnLog.i("failure pay, callback cp errorinfo : " + resultCode + "," + resultInfo);
//				Toast.makeText(activity,
//						"payfail:["+ "resultCode:"+resultCode + "," + (TextUtils.isEmpty(resultInfo) ? "unkown error" : resultInfo) + "]", Toast.LENGTH_LONG)
//						.show();

			}
		});

	}
	
	
	*//**
	 * 获取收银台参数
	 *//*
	private String genUrl( String appuserid, String cpprivateinfo, int waresid, double price, String cporderid , String waresname) {
		String json = "";

		JSONObject obj = new JSONObject();
		try {
			obj.put("appid", appid);
			obj.put("waresid", waresid);
			obj.put("cporderid", cporderid);
			obj.put("appuserid", appuserid);
			obj.put("price", price);//单位 元
			obj.put("currency", "RMB");//如果做服务器下单 该字段必填
			obj.put("waresname", waresname);//开放价格名称(用户可自定义，如果不传以后台配置为准)
			
			
			*//*CP私有信息，选填*//*
			String cpprivateinfo0 = cpprivateinfo;
			if(!TextUtils.isEmpty(cpprivateinfo0)){
				obj.put("cpprivateinfo", cpprivateinfo0);
			}
			
			*//*支付成功的通知地址。选填。如果客户端不设置本参数，则使用服务端配置的地址。*//*
//			if(!TextUtils.isEmpty(PayConfig.notifyurl)){
//				obj.put("notifyurl", "http://ipay.iapppay.com:9999/payapi/order");
//			}			
			json = obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String sign = "";
		try {
			String cppk = appkey;
		//	sign = RSAHelper.signForPKCS1(json, cppk);
			
		} catch (Exception e) {
			
		}
		
		return  "transdata=" + URLEncoder.encode(json) + "&sign=" + URLEncoder.encode(sign) + "&signtype=" + "RSA";
	}
	*/
}
