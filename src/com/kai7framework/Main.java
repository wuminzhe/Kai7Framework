package com.kai7framework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.kai7framework.cache.DataCache;
import com.kai7framework.model.Data;
import com.kai7framework.model.DataUtil;
import com.kai7framework.model.Dataset;
import com.kai7framework.network.ApkDownloadService;
import com.kai7framework.network.AsyncHttpUtil;
import com.kai7framework.network.HttpUtil;

public class Main extends Activity implements OnClickListener {
	private Button pButton;
	public  final static String PAR_KEY = "com.tutor.objecttran.par";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);   
        setupViews();
 
    }
 
    public void setupViews(){
    	pButton = (Button)findViewById(R.id.button2);
    	pButton.setOnClickListener(this);
    }
   

    public void PacelableMethod(){
    	Data mBook = new Data();
    	mBook.put("Name", "Android Tutor");
    	mBook.put("Author", "Frankie");
    	mBook.put("PublishTime", 2010.112);
    	Data mBook2 = new Data();
    	mBook2.put("Name", "Android Tutor");
    	mBook2.put("Author", "Frankie");
    	mBook2.put("PublishTime", 2010.221);
    	mBook2.put("another_book", mBook);
    	//System.out.println(mBook2);
    	Intent mIntent = new Intent(this,ObjectTranDemo2.class);
    	Bundle mBundle = new Bundle();
    	mBundle.putString(PAR_KEY, mBook2.toString());
    	mIntent.putExtras(mBundle);
 
    	startActivity(mIntent);
    }
    
//    public void testJSONFromNetwork(){
//    	NetManager netManager = NetManager.getInstance(this.getApplicationContext());
//		String result = netManager.dogetAsString("http://61.155.238.30/zyj/test.json");
//		
//		Intent mIntent = new Intent(this,ObjectTranDemo2.class);
//    	Bundle mBundle = new Bundle();
//    	mBundle.putString(PAR_KEY, result);
//    	mIntent.putExtras(mBundle);
//    	startActivity(mIntent);
//    }
//    
//    public void testJSONFromNetwork2(){
//    	NetManager netManager = NetManager.getInstance(this.getApplicationContext());
//		String result = netManager.dogetAsString("http://61.155.238.30/ohbaba/interface/toilets_2?lat=31.979457&lon=118.785553");
//		DataCache cacheManager = new DataCache(this.getApplicationContext());
//		Data data = (Data)DataUtil.fromJson(result);
//		Dataset toilets = data.getDataset("toilets");
//		
//		cacheManager.put("toilets", toilets);
//		Intent mIntent = new Intent(this,ObjectTranDemo2.class);
//    	
//    	startActivity(mIntent);
//    }
//    
    public void testHttp(){
    	String body = HttpUtil.get("http://www.ohbaba.com/interface/toilets_2?lat=31.979457&lon=118.785553");
		System.out.println(body);
    }
    
    public void testAsyncHttp(){
    	AsyncHttpUtil.get("http://www.ohbaba.com/interface/toilets_2?lat=31.979457&lon=118.785553", new AsyncHttpUtil.CallbackAdapter() {
			public void success(Data headers, String body) {
				Toast.makeText(getApplicationContext(), body, Toast.LENGTH_SHORT).show();
			}
		});
    }
    
    public void testDownload(){
    	startService(new Intent(this.getApplicationContext(), ApkDownloadService.class));
    }
    
    
    public void test(){
    	String body = HttpUtil.get("http://www.ohbaba.com/interface/toilets_2?lat=31.979457&lon=118.785553");
    	Data data = (Data)DataUtil.fromJson(body);
    	Dataset toilets = data.getDataset("toilets");
    	DataCache cache = new DataCache(this);
    	cache.put("toilets", toilets);
    	toilets = (Dataset)cache.get("toilets");
    }
    
	public void onClick(View v) {
		testDownload();
	}
}