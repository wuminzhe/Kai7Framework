package com.kai7framework.network;

import android.os.Handler;
import android.os.Message;

import com.kai7framework.model.Data;

public class AsyncHttpUtil {
	public static interface Callback {
		public void success(Data headers, String body);
		public void fail(int status, Data headers, String body);
		public void exception(HttpException e);
	}

	public static abstract class CallbackAdapter implements Callback {
		public void success(Data headers, String body) {}
		public void fail(int status, Data headers, String body) {}	
		public void exception(HttpException e) {}
	}
	
	public static void get(final String url, final Callback callback) {
		get(url, null, callback);
	}
	
	public static void get(final String url, final Data headers, final Callback callback) {
		final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	if(msg.what == 0){
            		Data result = (Data)msg.obj;
                	int status = result.getInt("status");
                	Data headers = result.getData("headers");
                	String body = result.getString("body");
                	if(status==200){
                		callback.success(headers, body);
                	}else{
                		callback.fail(status, headers, body);
                	}
            	}else if(msg.what == 1){
            		HttpException e = (HttpException)msg.obj;
            		callback.exception(e);
            	}
            	
            }
        };
        //load data
        new Thread() {
            public void run() {
            	try{
	            	Data result = HttpUtil.get(url, headers);
	            	handler.sendMessage(handler.obtainMessage(0, result));
            	}catch(HttpException e){
            		handler.sendMessage(handler.obtainMessage(1, e));
            	}
            };
        }.start();
	}
	
	public static void post(final String url, final Callback callback) {
		post(url, null, callback);
	}
	
	public static void post(final String url, final Data params, final Callback callback) {
		post(url, params, null, callback);
	}
	
	public static void post(final String url, final Data params, final Data headers, final Callback callback) {
		final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	if(msg.what == 0){
            		Data result = (Data)msg.obj;
                	int status = result.getInt("status");
                	Data headers = result.getData("headers");
                	String body = result.getString("body");
                	if(status==200){
                		callback.success(headers, body);
                	}else{
                		callback.fail(status, headers, body);
                	}
            	}else if(msg.what == 1){
            		HttpException e = (HttpException)msg.obj;
            		callback.exception(e);
            	}
            	
            }
        };
        //load data
        new Thread() {
            public void run() {
            	try{
	            	Data result = HttpUtil.post(url, params, headers);
	            	handler.sendMessage(handler.obtainMessage(0, result));
            	}catch(HttpException e){
            		handler.sendMessage(handler.obtainMessage(1, e));
            	}
            };
        }.start();
	}

	public static void main(String[] args){
		String url = "";
		Data headers = new Data();
		AsyncHttpUtil.get(url, headers, new AsyncHttpUtil.CallbackAdapter(){
			public void success(Data headers, String body) {
				
			}

			public void fail(int status, Data headers) {
				
			}
		});
		
		AsyncHttpUtil.get(url, new AsyncHttpUtil.CallbackAdapter() {
			
			public void success(Data headers, String body) {
				
			}
		});
		
		AsyncHttpUtil.post(url, new AsyncHttpUtil.CallbackAdapter(){
			public void success(Data headers, String body) {
				
			}
		});
	}

	
}
