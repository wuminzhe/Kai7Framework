package com.kai7framework.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.SensorManager;
import android.provider.SyncStateContract.Constants;

import com.kai7framework.model.Data;

public class DataPersistMangaer extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "DATA_DB";
	private static final int VERSION = 1;
	private List sqls = new ArrayList();
	
	public DataPersistMangaer(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
		
	}
	
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public void save(Data data){
		SQLiteDatabase db = this.getWritableDatabase();
//		ContentValues cv=new ContentValues(); 
//		cv.put(Constants.TITLE, "example title"); 
//		cv.put(Constants.VALUE, SensorManager.GRAVITY_DEATH_STAR_I); 
//		db.insert("mytable", getNullColumnHack(), cv);
	}

}
