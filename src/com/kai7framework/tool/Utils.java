package com.kai7framework.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Utils {
    public static void copyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    
	//decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File f){
        try {
        	return BitmapFactory.decodeStream(new FileInputStream(f));
//            //decode image size
//            BitmapFactory.Options o = new BitmapFactory.Options();
//            o.inJustDecodeBounds = true;
//            return BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            
//            //Find the correct scale value. It should be the power of 2.
//            final int REQUIRED_SIZE=70;
//            int width_tmp=o.outWidth, height_tmp=o.outHeight;
//            int scale=1;
//            while(true){
//                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
//                    break;
//                width_tmp/=2;
//                height_tmp/=2;
//                scale*=2;
//            }
//            
//            //decode with inSampleSize
//            BitmapFactory.Options o2 = new BitmapFactory.Options();
//            o2.inSampleSize=scale;
//            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        	return null;
        }
    }
    

	public static void saveBitmap(String filenameWithFullPath, Bitmap bitmap) {
		File file = new File(filenameWithFullPath);
		saveBitmap(file, bitmap);
	}

	public static void saveBitmap(File file, Bitmap bitmap) {
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 70, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}