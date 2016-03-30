package com.kai7framework.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Data implements Map, IData{
	private HashMap map;
	
	public Data(HashMap map){
		this.map = map;
	}
	
	public Data(){
		this.map = new HashMap();
	}

	public void clear() {
		this.map.clear();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set entrySet() {
		return map.entrySet();
	}

	public Object get(Object key) {
		return map.get(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set keySet() {
		return map.keySet();
	}

	public Object put(Object key, Object value) {
		return map.put(key, value);
	}

	public void putAll(Map arg0) {
		map.putAll(arg0);
	}

	public Object remove(Object key) {
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection values() {
		return map.values();
	}
	
	/////////////////////////////
	//数据获取
	/////////////////////////////
	public Object set(String key, Object value){
		return put(key, value);
	}
	
	public Object get(String name, Object def) {
		Object value = get(name);
		return value == null ? def : value;
	}

	public String[] getNames() {
		String[] names = (String[]) keySet().toArray(new String[0]);
		Arrays.sort(names);
		return names;
	}

	public String getString(String name) {
		Object value = get(name);
		return value == null ? null : value.toString();
	}
	
	public String getString(String name, String defaultValue) {
		return get(name, defaultValue).toString();
	}
	
	public int getInt(String name) {
		return getInt(name, 0);
	}
	
	public int getInt(String name, int defaultValue) {
		String value = getString(name, "");
		return "".equals(value) ? defaultValue : Integer.parseInt(value);
	}
	
	public double getDouble(String name) {
		return getDouble(name, 0);
	}
	
	public double getDouble(String name, double defaultValue) {
		String value = getString(name, "");
		return "".equals(value) ? defaultValue : Double.parseDouble(value);
	}
	
	public boolean getBoolean(String name) {
		return getBoolean(name, false);
	}
	
	public Data getData(String name){
		Object value = get(name);
		return value == null ? null : (Data)value;
	}
	
	public Dataset getDataset(String name){
		Object value = get(name);
		return value == null ? null : (Dataset)value;
	}
	
	/**
	 * get boolean
	 * @param name
	 * @param defaultValue
	 * @return boolean
	 */
	public boolean getBoolean(String name, boolean defaultValue) {
		String value = getString(name, "");
		return "".equals(value) ? defaultValue : Boolean.valueOf(value).booleanValue();
	}
	
	
	/////////////////////////////
	//缓存支持
	/////////////////////////////
//	public String getCacheKey(){
//		return SimpleSHA1.SHA1(this.toString());
//	}
	
	/////////////////////////////
	//和外部交互
	/////////////////////////////
	public String toString() {
		return toJson();
	}
	
	public String toJson(){
		return toJSONObject().toString();
	}
	
	JSONObject toJSONObject(){
		JSONObject json = new JSONObject();
		try {
			Iterator keyIter = this.keySet().iterator();
			while (keyIter.hasNext()) {
				String key = (String)keyIter.next();
				Object value = this.get(key);
				if (value instanceof String) {
					json.put(key, value);
				}else if(value instanceof Data){
					Data data = (Data)value;
					json.put(key, data.toJSONObject());
				}else if(value instanceof Dataset){
					Dataset datas = (Dataset)value;
					json.put(key, datas.toJSONArray());
				}else{
					json.put(key, value);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static Data fromJson(String jsonString) throws JSONException{
		JSONObject jsonObject = new JSONObject(jsonString);
		
		return Data.fromJSONObject(jsonObject);
	}
	
	static Data fromJSONObject(JSONObject jsonObject) throws JSONException{
		Data data = new Data();
		Iterator keys = jsonObject.keys();
		while(keys.hasNext()){
			String key = (String)keys.next();
			if(!jsonObject.isNull(key)){
				Object obj = jsonObject.get(key);
				if(obj instanceof JSONObject){
					data.put(key, Data.fromJSONObject((JSONObject)obj));
				}else if(obj instanceof JSONArray){
					Dataset dataset = Dataset.fromJSONArray((JSONArray)obj);
					data.put(key, dataset);
				}else{
					data.put(key, obj);
				}
			}else{
				data.put(key, null);
			}
			
		}

		return data;
	}
	
	public boolean isArray(){
		return false;
	}
	
//	public Map toMap(){
//		Map<String, Object> result = new HashMap<String, Object>();
//		Iterator keyIter = this.keySet().iterator();
//		while (keyIter.hasNext()) {
//			String key = (String)keyIter.next();
//			Object value = this.get(key);
//			if (value instanceof String) {
//				result.put(key, value);
//			}else if(value instanceof Data){
//				Data data = (Data)value;
//				result.put(key, data.toMap());
//			}else if(value instanceof Dataset){
//				Dataset datas = (Dataset)value;
//				result.put(key, datas.toList());
//			}else{
//				result.put(key, value);
//			}
//		}
//		return result;
//	}
}
