package com.kai7framework;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;

import com.kai7framework.image.Kai7SimpleAdapter;
import com.kai7framework.model.Data;
import com.kai7framework.model.DataUtil;
import com.kai7framework.model.Dataset;
import com.kai7framework.model.ValueProvider;
import com.kai7framework.network.HttpUtil;

public class ToiletsActivity extends ListActivity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Kai7SimpleAdapter adapter = new Kai7SimpleAdapter(
			this,
			getData(),
			R.layout.toilet, 
			new String[]{"name", "city", "pic"}, 
			new int[]{R.id.title, R.id.info, R.id.img}
		);
		setListAdapter(adapter);
	}
	
	private List getData(){
		String result = HttpUtil.get("http://www.ohbaba.com/interface/toilets_2?lat=31.979457&lon=118.785553");
		Data data = (Data)DataUtil.fromJson(result);
		Dataset dataset = data.getDataset("toilets");
		dataset.setAll("pic", new ValueProvider(){

			public Object getValue(Data data) {
				if(data.get("pic") != null){
					return "http://www.ohbaba.com/files/"+data.get("pic");
				}else{
					return "http://www.ohbaba.com/files/gc.png";
				}
			}
			
		});
		
		return dataset;
		
	}
}
