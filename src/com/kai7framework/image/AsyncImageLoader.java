package com.kai7framework.image;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;


public class AsyncImageLoader {
	private ImageCache cache;
	private AsyncImageLoader(){
		cache = new ImageCache();
	}
	private static AsyncImageLoader instance;
	public static AsyncImageLoader getInstance(){
		if(instance == null){
			instance = new AsyncImageLoader();
		}
		return instance;
	}

    public Bitmap load(final String imageUrl, final ImageCallback callback) {
        Bitmap b = cache.get(imageUrl);
        if(b!=null){
        	//callback.imageLoaded(b, imageUrl);
        	return b;
        }
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                callback.imageLoaded((Bitmap) msg.obj, imageUrl);
            }
        };
        //load data
        new Thread() {
            public void run() {
            	Bitmap b = loadImageFromUrl(imageUrl);
                if(b!=null){
                	cache.put(imageUrl, b);
                }
                
                handler.sendMessage(handler.obtainMessage(0, b));
            };
        }.start();
        return null;
    }

//    protected Drawable loadImageFromUrl(String imageUrl) {
//        try {
//            return Drawable.createFromStream(new URL(imageUrl).openStream(), "src");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    
    protected Bitmap loadImageFromUrl(String imageUrl) {
    	Bitmap b = null;
		try {
			b = BitmapFactory.decodeStream(new URL(imageUrl).openStream());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return b;
    }

    //call back interface
    public interface ImageCallback {
        public void imageLoaded(Bitmap image, String imageUrl);
    }
    
    public static void main(String[] args){
    	AsyncImageLoader.getInstance().load("http://kai7.cn/images/e947d02c0c9bee7ab3236bb050a052a114d781ca.png", new AsyncImageLoader.ImageCallback() {
			
			public void imageLoaded(Bitmap image, String imageUrl) {
				
			}
		});
    }
}
