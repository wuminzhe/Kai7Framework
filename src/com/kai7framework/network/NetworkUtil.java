package com.kai7framework.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
	public static boolean isWiFiActive(Context ctx) {
		ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] infos = connectivity.getAllNetworkInfo();
			if (infos != null) {
				for (int i = 0; i < infos.length; i++) {
					if (infos[i].getTypeName().equals("WIFI")
							&& infos[i].isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断是否cmwap在使用
	 */
	public static boolean isCmwapUsed(Context ctx){
        ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
        	NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null ){
            	String extraInfo = info.getExtraInfo();
            	if (extraInfo != null){
            		if (extraInfo.equals("cmwap")){
            			return true;
            		}
            	}
            }
        }
        return false;
	}
	
	/**
	 * 判断是否有可用网络
	 */
	public static boolean isNetworkAvailable(Context ctx) { 
	    ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (connectivity == null) {    
	      return false;
	    } else {  
	        NetworkInfo[] info = connectivity.getAllNetworkInfo();    
	        if (info != null) {        
	            for (int i = 0; i<info.length; i++) {           
	                if (info[i].getState() == NetworkInfo.State.CONNECTED) {              
	                    return true; 
	                }        
	            }     
	        } 
	    }   
	    return false;
	}
}
