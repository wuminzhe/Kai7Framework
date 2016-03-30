package com.kai7framework.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class DataUtil {
	public static IData fromJson(String jsonString){
		IData result = null;
		try {
			jsonString = jsonString.trim();
			if(jsonString.startsWith("{")){
				result = Data.fromJson(jsonString);
			}else if(jsonString.startsWith("[")){
				result = Dataset.fromJson(jsonString);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Data fromXml(String xml) {
		Data result = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream is =new ByteArrayInputStream(xml.getBytes());
			Document dom = builder.parse(is);
			Element root = dom.getDocumentElement();
			
			if(!hasChildren(root) && root.getAttributes().getLength()==0){ //<p>hello</p>
				result = new Data();
				result.put(root.getNodeName(), root.getFirstChild().getTextContent());
			}else{
				result = parseNode(root);
			}
			return result;
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static Data parseNode(Element node){
		
		Data result = new Data();
		Data attrs = parseAttributes(node.getAttributes());
		result.putAll(attrs);
		
		if(hasChildren(node)){//有子节点
			NodeList childNodes = node.getChildNodes();
			for(int i=0;i<childNodes.getLength();i++){
				Node child = childNodes.item(i);
				if(child instanceof Element){
					if(hasChildren((Element)child) || child.getAttributes().getLength()>0){
						
						if(result.containsKey(child.getNodeName())){
							Object exist = result.get(child.getNodeName());
							Dataset datas = null;
							if(exist instanceof Data){
								datas = new Dataset();
								datas.add((Data)exist);
							}else if(exist instanceof Dataset){
								datas = (Dataset)exist;
							}
							
							datas.add(parseNode((Element)child));
							result.put(child.getNodeName(), datas);
						}else{
							result.put(child.getNodeName(), parseNode((Element)child));
						}
					}else{
						result.put(child.getNodeName(), child.getFirstChild().getTextContent());
					}
				}
			}
		}
		
		return result;
	}
	
	private static boolean hasChildren(Element node){
		NodeList childNodes = node.getChildNodes();
		if(childNodes.getLength()>1){
			return true;
		}else if(childNodes.getLength()==1){
			Node child = node.getFirstChild();
			if(child instanceof Text || child instanceof CDATASection){
				return false;
			}else{
				return true;
			}
		}else{
			return false;
		}
	}
	
	private static Data parseAttributes(NamedNodeMap attrs){
		Data result = new Data();
		for(int i=0;i<attrs.getLength();i++){
			Node attr = attrs.item(i);
			String name = attr.getNodeName();
			String value = attr.getNodeValue();
			result.put(name, value);
		}
		return result;
	}
	
	public static void main(String[] args){
//		String xml = 
//		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//			"<animals count=\"1\">" +
//			"<total>2</total>" +
//			"<animal name=\"mimi\"><age><die>11</die></age></animal>" +
//			"<animal name=\"bibi\"><age>6</age></animal>" +
//		"</animals>";
	String xml = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<animals>" +
			"<animal name=\"mimi\"><age>11</age></animal>" +
			"<animal name=\"bibi\"><age>6</age></animal>" +
		"</animals>";
//	String xml = "<total>2</total>";
	//System.out.println(DataUtil.fromXml(xml));
	}
}