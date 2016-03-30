package com.kai7framework.image;

import android.graphics.Bitmap;

import com.kai7framework.Kai7Application;

public class ImageCache {
	private ImageMemoryCache memoryCache;
	private ImageFileCache fileCache;

	public ImageCache() {
		this.memoryCache=new ImageMemoryCache();
		this.fileCache=new ImageFileCache(Kai7Application.getInstance(), Kai7Application.getInstance().getImageFileCacheDir());
	}

	public Bitmap get(String url){
		Bitmap result = this.memoryCache.get(url);
		if(result!=null){
			return result;
		}
		
		result = this.fileCache.get(url);
		if(result!=null){
			return result;
		}else{
			return null;
		}
    }
    
    public void put(String url, Bitmap bitmap) {
		this.fileCache.put(url, bitmap);
		this.memoryCache.put(url, bitmap);
	}
    
    public void clear(){
    	this.memoryCache.clear();
    	this.fileCache.clear();
    }
}
