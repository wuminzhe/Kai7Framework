package com.kai7framework.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Dataset implements List<Data>, IData{
	private List<Data> list;
	
	public Dataset(){
		this.list = new ArrayList<Data>();
	}
	
	public Dataset(List<Data> list){
		this.list = new ArrayList<Data>();
		this.list.addAll(list);
	}

	//////////////////////////
	//List<Data>
	//////////////////////////
	public boolean add(Data object) {
		return this.list.add(object);
	}

	public void add(int location, Data object) {
		this.list.add(location, object);
	}

	public boolean addAll(Collection<? extends Data> collection) {
		return this.list.addAll(collection);
	}

	public boolean addAll(int location, Collection<? extends Data> collection) {
		return this.addAll(location, collection);
	}

	public void clear() {
		this.list.clear();
	}

	public boolean contains(Object object) {
		return this.list.contains(object);
	}

	public boolean containsAll(Collection<?> collection) {
		return this.list.containsAll(collection);
	}

	public Data get(int location) {
		return this.list.get(location);
	}

	public int indexOf(Object object) {
		return this.list.indexOf(object);
	}

	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	public Iterator<Data> iterator() {
		return this.list.iterator();
	}

	public int lastIndexOf(Object object) {
		return this.list.lastIndexOf(object);
	}

	public ListIterator<Data> listIterator() {
		return this.list.listIterator();
	}

	public ListIterator<Data> listIterator(int location) {
		return this.listIterator(location);
	}

	public Data remove(int location) {
		return this.remove(location);
	}

	public boolean remove(Object object) {
		return this.list.remove(object);
	}

	public boolean removeAll(Collection<?> collection) {
		return this.removeAll(collection);
	}

	public boolean retainAll(Collection<?> collection) {
		return this.retainAll(collection);
	}

	public Data set(int location, Data object) {
		return this.set(location, object);
	}

	public int size() {
		return this.list.size();
	}

	public List<Data> subList(int start, int end) {
		return this.subList(start, end);
	}

	public Data[] toArray() {
		return this.toArray();
	}

	public <T> T[] toArray(T[] array) {
		return this.toArray(array);
	}
	
	//////////////////////////
	//IData
	//////////////////////////
	public String toJson() {
		return toJSONArray().toString();
	}

	public boolean isArray() {
		return true;
	}
	
	//////////////////////////
	//internal
	//////////////////////////
	JSONArray toJSONArray(){
		JSONArray json = new JSONArray();
		for(int i=0;i<this.size();i++){
			Object obj = this.get(i);
			if(obj instanceof Data){
				Data data = (Data)obj;
				json.put(data.toJSONObject());
			}
//			else if(obj instanceof Dataset){
//				Dataset datas = (Dataset)obj;
//				json.put(datas.toJSONArray());
//			}
			
		}
		
		return json;
	}
	
	static Dataset fromJSONArray(JSONArray jsonArray) throws JSONException{
		Dataset result = new Dataset();
		for(int i=0;i<jsonArray.length();i++){
			Object obj = jsonArray.get(i);
			if(obj instanceof JSONObject){
				result.add(Data.fromJSONObject((JSONObject)obj));
			}
		}
		return result;
	}
	
	//////////////////////////
	//other
	//////////////////////////
	public static Dataset fromJson(String jsonString) throws JSONException{
		JSONArray jsonArray = new JSONArray(jsonString);
		
		return Dataset.fromJSONArray(jsonArray);
	}
	
	public String toString() {
		return toJson();
	}

	public void setAll(String name, ValueProvider valueProvider) {
		for(Data data : this.list){
			data.put(name, valueProvider.getValue(data));
		}
	}
	
	public interface DataAction{
		public void exec(Data data);
	}
	
	public void each(DataAction action){ 
		for(Data data : this.list){
			action.exec(data);
		}
	}
	
	public interface Filter{
		public boolean isThisWhatIWant(Data data);
	}
	
	public Dataset filter(Filter filter){
		Dataset result = new Dataset();
		for(Data data : this.list){
			if(filter.isThisWhatIWant(data)){
				result.add(data);
			}
		}
		return result;
	}
	
	public interface GroupBy{
		public int groupBy(Data data);
	}
	
	public Map<Integer, Dataset> group(GroupBy groupBy){
		Map<Integer, Dataset> result = new HashMap<Integer, Dataset>();
		for(Data data : this.list){
			int groupId = groupBy.groupBy(data);
			if(result.containsKey(groupId)){
				Dataset g = (Dataset)result.get(groupId);
				g.add(data);
			}else{
				Dataset g = new Dataset();
				g.add(data);
				result.put(groupId, g);
			}
		}
		return result;
	}

}
