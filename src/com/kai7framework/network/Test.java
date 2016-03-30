package com.kai7framework.network;

import java.io.InputStream;

import com.kai7framework.model.Data;

public class Test {
	public static void main(String[] args) {
		Data information = new Data();
		information.put("type", "1");
		information.put("getwhat", "2");
		information.put("type_type", "0");
		Data params = new Data();
		params.put("information", information.toJson());

//		Data headers = new Data();
//		Data cookie = new Data();
//		cookie.put("userid", "123456");
//		headers.put("Cookie", cookie);
//		String body = HttpUtil.post("http://192.168.0.1");
//		body = HttpUtil.post("http://192.168.0.1", params);
//		
//		Data result = HttpUtil.post("http://192.168.0.132/twf/json/get_information.php", params, headers);
//		body = (String)result.get("body");
//		String header = (String)result.get("headers");
		HttpUtil.post("http://192.168.0.132/twf/json/get_information.php", params, null, new HttpUtil.CallbackAdapter(){
			public void success(Data headers, InputStream body){
				System.out.println("1111");
			}
			public void fail(int status, Data headers){
				System.out.println("2222");
			}
		});
//		String body = result.get("body")
	}
	
	public static void testGet(){
		String body = HttpUtil.get("http://www.ohbaba.com/interface/toilets_2?lat=31.979457&lon=118.785553");
		
		Data headers = new Data();
		Data result = HttpUtil.get("http://www.ohbaba.com/interface/toilets_2?lat=31.979457&lon=118.785553", headers);
		
		HttpUtil.get("http://www.ohbaba.com/interface/toilets_2?lat=31.979457&lon=118.785553", headers, new HttpUtil.CallbackAdapter() {
			public void success(Data headers, InputStream body){
				System.out.println("1111");
			}
			public void fail(int status, Data headers){
				System.out.println("2222");
			}
		});
	}
}
