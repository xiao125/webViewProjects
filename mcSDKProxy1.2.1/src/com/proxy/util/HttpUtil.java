package com.proxy.util;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class HttpUtil {
	
	public static synchronized String doHttpPost(Map<String, String> params,
			String postUrl) throws IOException {		
		
		String content = parseMapToString(params);
		
		String[] strs = postUrl.split("/");
		
		String postTag = strs[strs.length-1].split("\\.")[0];
		
		LogUtil.e("postTag = " + postTag + " , content =  " + content);
		
		return doHttpPostConnection(content, postUrl);
	}
	
	public static synchronized String doHttpPostTimeOut(Map<String, String> params,
			String postUrl , int timeOut) throws IOException {
		
		String content = parseMapToString(params);
		
		String[] strs = postUrl.split("/");
		
		String postTag = strs[strs.length-1].split("\\.")[0];
		
		LogUtil.e("postTag = " + postTag + " , content =  " + content);
		
		return doHttpPostConnectionFortimeOut(content, postUrl, timeOut );
	}


	public static synchronized String doHttpGet(Map<String, String> params, 
			String postUrl) throws IOException {
		
		String content = parseMapToString(params);
		
		return doHttpPostConnection(content.substring(1), postUrl);
	}

	private static synchronized String doHttpGetConnection(String content,
			String postUrl) throws IOException {
		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(postUrl + "?" + content);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(25000);
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.connect();
			return convertStreamToString(connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.disconnect();
		}
		return null;
	}
	
	private static synchronized String doHttpPostConnection(String content,String postUrl) throws IOException {
		
		return httpPostConnect(content,postUrl,3000);
		/*
		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(postUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(3000);
			connection.setReadTimeout(25000);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.connect();
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			out.write(content.getBytes("utf-8"));
			out.flush();
			out.close();
			
			InputStream is = connection.getInputStream();
			
			return convertStreamToString(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.disconnect();
		}
		
		return null;
		*/
		
	}
	
	private static synchronized String doHttpPostConnectionFortimeOut( String content,String postUrl , int timeOut ){
		
		return httpPostConnect(content,postUrl,timeOut);
		
	}
	
	public static synchronized String httpPostConnect(  String content,String postUrl , int timeOut){
		
		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(postUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(timeOut);
			connection.setReadTimeout(25000);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.connect();
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			out.write(content.getBytes("utf-8"));
			out.flush();
			out.close();
			
			InputStream is = connection.getInputStream();
			
			return convertStreamToString(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.disconnect();
		}
		
		return null;
		
	}


	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null)
				sb.append(line);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return Util.unicodeToUtf8( sb.toString() );
	}
	
	private static String parseMapToString( Map<String, String> params ){
		
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
}