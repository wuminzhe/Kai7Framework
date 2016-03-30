package com.kai7framework.image;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;

import com.kai7framework.tool.SimpleSHA1;
import com.kai7framework.tool.Utils;

public class ImageFileCache {
    
    private File cacheDir;
    
    public ImageFileCache(Context context, String dirName){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(), dirName);
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    public Bitmap get(String url){
        String filename=SimpleSHA1.SHA1(url);
        File f = new File(cacheDir, filename);
        if(f.exists()){
        	Bitmap b = Utils.decodeFile(f);
        	return b;
        }else{
        	return null;
        }
    }
    
    public void put(String url, Bitmap bitmap) {
		String filename=SimpleSHA1.SHA1(url);
		File f = new File(cacheDir, filename);
		Utils.saveBitmap(f, bitmap);
	}
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        for(File f:files)
            f.delete();
    }

	
}