package com.kai7framework;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.kai7framework.cache.DataCache;
import com.kai7framework.model.Dataset;

public class ObjectTranDemo2 extends Activity{
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView mTextView = new TextView(this);
//        String jsonString = getIntent().getStringExtra(Main.PAR_KEY);
//        System.out.println(jsonString);
//        Data mBook = null;
//		try {
//			Data book = (Data)DataUtil.fromJSONString(jsonString);
//			System.out.println(book);
//			mBook = (Data)book.get("another_book");
//		} catch (JSONException e) {
//
//			e.printStackTrace();
//		}
//        mTextView.setText("Book name is: " + mBook.getString("Name")+"\n"+
//		  "Author is: " + mBook.getString("Author") + "\n" +
//		  "PublishTime is: " + mBook.getString("PublishTime"));
        
//        String jsonString = getIntent().getStringExtra(Main.PAR_KEY);
//        try {
//        	StringBuffer txt = new StringBuffer();
//			Data result = (Data)DataUtil.fromJson(jsonString);
//			Dataset bottles = result.getData("package").getDataset("bottle");
////			System.out.println(result.getCacheKey());
//			for(int i=0;i<bottles.size();i++){
//				Data bottle = (Data)bottles.get(i);
//				txt.append(bottle.getString("item")).append(", ");
//			}
//			mTextView.setText(txt.toString());
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
        DataCache cacheManager = new DataCache(this.getApplicationContext());
        Dataset toilets = (Dataset)cacheManager.get("toilets");
        mTextView.setText(toilets.toJson());
        setContentView(mTextView);
    }
}
