package com.kai7framework.model;

import java.util.HashMap;
import java.util.Map;

public class DataMeta {
	private static final int NULL = 0;
	private static final int TEXT = 1;
	private static final int INTEGER = 2;
	private static final int REAL = 3;
	private static final int BLOB = 4;
	
	private Map<String, Integer> fieldTypes = new HashMap<String, Integer>();

	public static void main(String[] args){
		DataMeta dm = new DataMeta();
		dm.setFieldType("name", DataMeta.TEXT);
		dm.setFieldType("age", DataMeta.INTEGER);
		int nameType = dm.getFieldType("name");
	}

	public int getFieldType(String fieldName) {
		int fieldType = (int)fieldTypes.get(fieldName);
		return fieldType;
	}

	public void setFieldType(String fieldName, int type) {
		fieldTypes.put(fieldName, type);
	}
	
	
}
