package com.kai7framework.network.internal;

import java.io.InputStream;

import android.os.Handler;
import android.os.Message;

import com.kai7framework.model.Data;
import com.kai7framework.network.HttpUtil;

public abstract class AsyncHttpLoader {
	abstract public Object inputStreamToObject(InputStream in);
	
	public interface AsyncHttpLoaderCallback{
		public void onfinished(Object obj);
	}
	
	public void load(final String url, final AsyncHttpLoaderCallback callback){
//		if(this.cache.contains(url)){
//			Object result = this.cache.get(url);
//			callback.onfinished(result);
//		}
		
		final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	Object result = msg.obj;
            	callback.onfinished(result);
            }
        };
        //load data
        new Thread() {
            public void run() {
            	HttpUtil.get(url, null, new HttpUtil.CallbackAdapter() {
            		public void success(Data headers, InputStream in) {
            			Object obj = inputStreamToObject(in);
   
            			handler.sendMessage(handler.obtainMessage(0, obj));
            		}
				});
            	
            };
        }.start();
	}
}
