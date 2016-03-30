package com.kai7framework.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import com.kai7framework.Kai7Application;
import com.kai7framework.model.Data;

public class HttpUtil {
	public static interface Callback {
		public void success(Data headers, InputStream body);
		public void fail(int status, Data headers, InputStream body);
	}
	
	public static abstract class CallbackAdapter implements Callback {
		public void success(Data headers, InputStream body) {}
		public void fail(int status, Data headers, InputStream body) {}
	}
	
	private static DefaultHttpClient getHttpClient(){
		DefaultHttpClient httpClient  =  new DefaultHttpClient();
		
		if (!NetworkUtil.isWiFiActive(Kai7Application.getInstance())) {
			//set proxy when using cmwap
	        if (NetworkUtil.isCmwapUsed(Kai7Application.getInstance()) == true){
	        	HttpHost proxy = new HttpHost( "10.0.0.172", 80, "http");
	            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	        }else{
	        	String proxyHost = android.net.Proxy.getDefaultHost();
				if (proxyHost != null) {
					HttpHost proxy = new HttpHost(android.net.Proxy.getDefaultHost(), android.net.Proxy.getDefaultPort());
					httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
				}
	        }
		}
		
		httpClient.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT,TIMEOUT);		
		return httpClient;
	}
	
	private static String toCookieString(Data cookie){
		StringBuffer sb = new StringBuffer();
		for(Object key : cookie.keySet()){
			String value = cookie.getString((String)key);
			sb.append((String)key).append("=").append(value).append("; ");
		}
		return sb.toString();
	}
	
	public static String inputStreamToString(InputStream in) throws IOException{
		BufferedReader r = new BufferedReader(new InputStreamReader(in, CHARTSET));
		StringBuffer sb = new StringBuffer();
		String line = "";
		while ((line = r.readLine()) != null) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}

	public static int TIMEOUT = 15000;//单位 毫秒
	public static String CHARTSET = "utf8";//单位 毫秒

	public static String post(String url) {
		return null;
	}

	public static String post(String url, Data params) {
		Data result = HttpUtil.post(url, params, null);
		if(result.getString("body") == null){
			return null;
		}else{
			return result.getString("body");
		}
	}

	public static Data post(String url, Data params, Data headers) {
		final Data result = new Data();
		
		post(url, params, headers, new HttpUtil.CallbackAdapter(){
			public void success(Data headers, InputStream in){
				try {
					result.put("status", 200);
					result.put("headers", headers);
					result.put("body", inputStreamToString(in));
				} catch (IOException e) {
					throw new HttpException(e);
				}
				
			}
			public void fail(int status, Data headers, InputStream in){
				try {
					result.put("status", status);
					result.put("headers", headers);
					result.put("body", inputStreamToString(in));
				} catch (IOException e) {
					throw new HttpException(e);
				}
			}
		});
		return result;
	}

	public static void post(String url, Data params, Data headers, Callback callback) {
		try {
			doPost(url, params, headers, callback);
		} catch (ClientProtocolException e) {
			throw new HttpException(e);
		} catch (IOException e) {
			throw new HttpException(e);
		}
	}
	
	private static void doPost(String url, Data params, Data headers, Callback callback) throws ClientProtocolException, IOException{
		DefaultHttpClient httpclient = getHttpClient();
		HttpPost req = new HttpPost(url);
		if(headers != null){
			for(Object key : headers.keySet()){
				Object value = headers.get(key);
				if(key=="Cookie"){
					if(value instanceof Data){
						Data cookie = (Data)value;
						req.setHeader("Cookie", toCookieString(cookie));
					}else{
						req.setHeader("Cookie", (String)value);
					}
				}else{
					req.setHeader((String)key, value.toString());
				}
			}
		}

		req.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, TIMEOUT);

		if(params != null){
			List<NameValuePair> valuepairs = new ArrayList<NameValuePair>();// post提交的名值对
			for(Object key : params.keySet()){
				String value = params.getString((String)key);
				valuepairs.add(new BasicNameValuePair((String)key, value));
			}
	
			req.setEntity(new UrlEncodedFormEntity(valuepairs, HTTP.UTF_8));
		}
		HttpResponse rep = httpclient.execute(req);

		//
		Data responseHeaders = new Data();
		Header[] allHeaders = rep.getAllHeaders();
		StringBuffer sb = new StringBuffer();
		for (Header h : allHeaders) {
			String value = null;
			if(responseHeaders.containsKey(h.getName())){
				value = responseHeaders.getString(h.getName());
				value = value + "; " + h.getValue();
			}else{
				value = h.getValue();
			}
			if(value!=null){
				responseHeaders.put(h.getName(), value);
			}
		}

		HttpEntity entity = rep.getEntity();
		InputStream in = entity.getContent();
		
		//
//		if(responseHeaders)
		if( rep.getStatusLine().getStatusCode() == 200 ){
			callback.success(responseHeaders, in);
		}else{
			callback.fail(rep.getStatusLine().getStatusCode(), responseHeaders, in);
		}
		
	}

	public static String get(String url) {
		Data result = HttpUtil.get(url, null);
		if(result.getString("body") == null){
			return null;
		}else{
			return result.getString("body");
		}
	}

	public static Data get(String url, Data headers) {
		final Data result = new Data();
		
		get(url, headers, new HttpUtil.CallbackAdapter(){
			public void success(Data headers, InputStream in){
				try {
					result.put("status", 200);
					result.put("headers", headers);
					result.put("body", inputStreamToString(in));
				} catch (IOException e) {
					throw new HttpException(e);
				}
				
			}
			public void fail(int status, Data headers, InputStream in){
				try {
					result.put("status", status);
					result.put("headers", headers);
					result.put("body", inputStreamToString(in));
				} catch (IOException e) {
					throw new HttpException(e);
				}
			}
		});
		return result;
	}
	
	public static void get(String url, Data headers, CallbackAdapter callback){
		try {
			doGet(url, headers, callback);
		} catch (ClientProtocolException e) {
			throw new HttpException(e);
		} catch (IOException e) {
			throw new HttpException(e);
		}
	}

	private static void doGet(String url, Data headers, CallbackAdapter callback) throws ClientProtocolException, IOException {
		DefaultHttpClient httpclient = getHttpClient();
		HttpGet req = new HttpGet(url);
		if(headers != null){
			for(Object key : headers.keySet()){
				Object value = headers.get(key);
				if(key=="Cookie"){
					if(value instanceof Data){
						Data cookie = (Data)value;
						req.setHeader("Cookie", toCookieString(cookie));
					}else{
						req.setHeader("Cookie", (String)value);
					}
				}else{
					req.setHeader((String)key, value.toString());
				}
			}
		}
		
		HttpResponse rep = httpclient.execute(req);
		//
		Data responseHeaders = new Data();
		Header[] allHeaders = rep.getAllHeaders();
		StringBuffer sb = new StringBuffer();
		for (Header h : allHeaders) {
			String value = null;
			if(responseHeaders.containsKey(h.getName())){
				value = responseHeaders.getString(h.getName());
				value = value + "; " + h.getValue();
			}else{
				value = h.getValue();
			}
			if(value!=null){
				responseHeaders.put(h.getName(), value);
			}
		}

		HttpEntity entity = rep.getEntity();
		InputStream in = entity.getContent();
		
		//
		if( rep.getStatusLine().getStatusCode() == 200 ){
			callback.success(responseHeaders, in);
		}else{
			callback.fail(rep.getStatusLine().getStatusCode(), responseHeaders, in);
		}
	}
	
	

}
