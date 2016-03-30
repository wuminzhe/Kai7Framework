package com.kai7framework.network.internal;

import java.io.InputStream;

import android.graphics.drawable.Drawable;

public class AsyncImageLoader extends AsyncHttpLoader {

	@Override
	public Object inputStreamToObject(InputStream in) {
		return Drawable.createFromStream(in, "src");
	}
	
	public static void main(String[] args){
		new AsyncImageLoader().load("http://", new AsyncHttpLoader.AsyncHttpLoaderCallback(){

			public void onfinished(Object obj) {
				Drawable img = (Drawable)obj;
			}
			
		} );
	}

}
