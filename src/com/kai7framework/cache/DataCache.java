package com.kai7framework.cache;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kai7framework.model.DataUtil;
import com.kai7framework.model.IData;

public class DataCache extends SQLiteOpenHelper{
	private static final String LOG_TAG = "CacheManager";
	
	private static final String DATABASE_NAME = "CACHE_DB";
	private static final String TABLE_NAME = "CACHE_TABLE";
	private static final int VERSION = 1;
	
	private static final String COLUMN_ID = "ID";
	private static final String COLUMN_CACHE_KEY = "NAME";
	private static final String COLUMN_CACHE_VALUE = "VALUE";
	private static final String COLUMN_CACHE_CREATED_AT = "CREATED_AT";
	
	
	public DataCache(Context context) {
		super(context,DATABASE_NAME,null,VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = 
			"create table " + TABLE_NAME + " (" + 
				COLUMN_ID + " integer primary key autoincrement, " + 
				COLUMN_CACHE_KEY + " text not null unique , " + 
				COLUMN_CACHE_VALUE + " text not null," + 
				COLUMN_CACHE_CREATED_AT + " datetime not null " + 
			");";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
		onCreate(db);
	}
	
	//
	public void put(String cacheKey, IData data){
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String now = format.format(new Date());
			String sql = 
				"insert into " + TABLE_NAME + "(" + 
					COLUMN_CACHE_KEY + ", " + 
					COLUMN_CACHE_VALUE + ", " + 
					COLUMN_CACHE_CREATED_AT + 
				" ) values ('" + 
					cacheKey + "', '" + 
					data.toJson() + "', '" + 
					now + 
				"')";
			Log.d(DataCache.LOG_TAG, sql);
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}
	
	public IData get(String cacheKey){
		SQLiteDatabase db = this.getReadableDatabase();
		IData data = null;
		try {
			String sql = "select * from " + TABLE_NAME + " where " + COLUMN_CACHE_KEY + "=?";
			Log.d(DataCache.LOG_TAG, sql+", "+cacheKey);
			Cursor c = db.rawQuery(sql, new String[]{cacheKey});
			if(c.moveToFirst()) {
				 String value = c.getString(2);//获取第3列的值
				 data = DataUtil.fromJson(value);
			}
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return data;
	}
	
	public void removeCache(String cacheKey){
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			String sql = 
				"delete from " + TABLE_NAME + " where " + COLUMN_CACHE_KEY + "='" + cacheKey + "'";
			Log.d(DataCache.LOG_TAG, sql);
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

}
