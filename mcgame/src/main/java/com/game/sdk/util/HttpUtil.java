package com.game.sdk.util;

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
		
		return doHttpPostConnection(content, postUrl);
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
		} finally {
			if (connection != null)
				connection.disconnect();
		}
		return null;
	}
	
	private static synchronized String doHttpPostConnection(String content,
			String postUrl) throws IOException {
		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(postUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(15000);
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
			return convertStreamToString(connection.getInputStream());
		} catch (IOException e) {
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