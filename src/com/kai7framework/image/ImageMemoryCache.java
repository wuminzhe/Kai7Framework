package com.kai7framework.image;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import android.graphics.Bitmap;

public class ImageMemoryCache {
    private HashMap<String, SoftReference<Bitmap>> cache=new HashMap<String, SoftReference<Bitmap>>();
    
    public Bitmap get(String url){
        if(!cache.containsKey(url))
            return null;
        SoftReference<Bitmap> ref=cache.get(url);
        return ref.get();
    }
    
    public void put(String url, Bitmap bitmap){
        cache.put(url, new SoftReference<Bitmap>(bitmap));
    }

    public void clear() {
        cache.clear();
    }
}