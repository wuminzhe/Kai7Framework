package com.kai7framework.image;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.kai7framework.Kai7Application;

public class AsyncImageViewLoader {
    
    private ImageMemoryCache memoryCache;
    private ImageFileCache fileCache;
	private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService executorService; 
    
    private int stub = 0;
    
    public AsyncImageViewLoader(Context context, int stub){
    	memoryCache=new ImageMemoryCache();
        fileCache=new ImageFileCache(context, Kai7Application.getInstance().getImageFileCacheDir());
        executorService=Executors.newFixedThreadPool(5);
        this.stub = stub;
    }
    
    public AsyncImageViewLoader(Context context){
    	memoryCache=new ImageMemoryCache();
        fileCache=new ImageFileCache(context, Kai7Application.getInstance().getImageFileCacheDir());
        executorService=Executors.newFixedThreadPool(5);
    }
    
    public void setFileCache(ImageFileCache fileCache) {
		this.fileCache = fileCache;
	}
    
    public void load(String url, ImageView imageView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null) {
            imageView.setImageBitmap(bitmap);
        } else {
            loadImage(url, imageView);
            if(stub!=0){
            	imageView.setImageResource(stub);
            }
            
        }
    }
        
    private void loadImage(String url, ImageView imageView)
    {
        ImageToLoad p=new ImageToLoad(url, imageView);
        executorService.submit(new ImageLoader(p));
    }
    
    private Bitmap getBitmap(String url) 
    {   
        //from SD cache
        Bitmap b = fileCache.get(url);
        if(b!=null)
            return b;
        
        //from web
        try {
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            b = BitmapFactory.decodeStream(is);
            fileCache.put(url, b);
            return b;
        } catch (Exception ex){
           ex.printStackTrace();
           return null;
        }
    }
    
    private class ImageToLoad
    {
        public String url;
        public ImageView imageView;
        public ImageToLoad(String u, ImageView i){
            url=u; 
            imageView=i;
        }
    }
    
    class ImageLoader implements Runnable {
        ImageToLoad imageToLoad;
        ImageLoader(ImageToLoad imageToLoad){
            this.imageToLoad=imageToLoad;
        }
        
        public void run() {
            if(imageViewReused(imageToLoad))
                return;
            Bitmap bmp=getBitmap(imageToLoad.url);
            memoryCache.put(imageToLoad.url, bmp);
            if(imageViewReused(imageToLoad))
                return;
            BitmapDisplayer bd=new BitmapDisplayer(bmp, imageToLoad);
            Activity a=(Activity)imageToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }
    
    boolean imageViewReused(ImageToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        ImageToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, ImageToLoad p){
        	bitmap=b;
        	photoToLoad=p;
        }
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(stub);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}
