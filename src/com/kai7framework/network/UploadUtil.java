package com.kai7framework.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.kai7framework.tool.Base64Coder;

public class UploadUtil {
	public static boolean transferWithBase64 = false;
	//
	private static String CONTENTTYPE_WORD = "application/msword";
	private static String CONTENTTYPE_EXCEL = "application/vnd.ms-excel";
	private static String CONTENTTYPE_TEXT = "text/plain";
	private static String CONTENTTYPE_JPEG = "image/jpeg";
	private static String CONTENTTYPE_PNG = "image/png";
	private static String CONTENTTYPE_GIF = "image/gif";
	private static Map<String, String> contentTypes = new HashMap<String, String>();
	static {
		contentTypes.put("doc", CONTENTTYPE_WORD);
		contentTypes.put("docx", CONTENTTYPE_WORD);
		contentTypes.put("xls", CONTENTTYPE_EXCEL);
		contentTypes.put("xlsx", CONTENTTYPE_EXCEL);
		contentTypes.put("txt", CONTENTTYPE_TEXT);
		contentTypes.put("jpg", CONTENTTYPE_JPEG);
		contentTypes.put("jpeg", CONTENTTYPE_JPEG);
		contentTypes.put("png", CONTENTTYPE_PNG);
		contentTypes.put("gif", CONTENTTYPE_GIF);
	}
	
	private static String getContentType(String filename){
		String result = "text/plain";
		String[] segments = filename.split("\\.");
		if(segments.length>0){
			String suffix = segments[segments.length-1];
			if(suffix!=null){
				result = contentTypes.get(suffix);
			}
		}
		return result;
	}

	public static String uploadFiles(String actionUrl, Map<String, String> params, Map<String, File> files) throws IOException {

		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";
		
		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(5 * 1000);
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false);
		conn.setRequestMethod("POST"); // Post方式
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

		DataOutputStream os = new DataOutputStream(conn.getOutputStream());
		
		// 首先组拼文本类型的参数
		if(params != null){
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(entry.getValue());
				sb.append(LINEND);
			}
			
			os.write(sb.toString().getBytes());
		}
		
		// 发送文件数据
		if (files != null){
			
			for (Map.Entry<String, File> file : files.entrySet()) {
				String name = file.getKey();
				String filename = file.getValue().getName();
				String contentType = getContentType(filename);

				if( (contentType.equals(CONTENTTYPE_WORD) || contentType.equals(CONTENTTYPE_EXCEL)) && transferWithBase64){
					contentType = CONTENTTYPE_TEXT;
					String formHeader = genFormHeader(PREFIX, BOUNDARY, LINEND, CHARSET, name, filename, contentType);
					os.write(formHeader.getBytes());
					//
					String base64String = fileToBase64String(file.getValue());
					os.writeBytes(base64String);
				}else{
					String formHeader = genFormHeader(PREFIX, BOUNDARY, LINEND, CHARSET, name, filename, contentType);
					os.write(formHeader.getBytes());
					//
					InputStream is = new FileInputStream(file.getValue());
					copyStream(os, is);
				}

				os.write(LINEND.getBytes());
			}
		}
		
		// 请求结束标志
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		os.write(end_data);
		os.flush();

		// 得到响应码
		int res = conn.getResponseCode();
		System.out.println(res);
		InputStream in = conn.getInputStream();
		String result = inputStream2String(in);
		
		os.close();
		conn.disconnect();
		return result;
	}

	private static String genFormHeader(String PREFIX, String BOUNDARY,
			String LINEND, String CHARSET, String name, String filename,
			String contentType) {
		StringBuilder sb1 = new StringBuilder();
		sb1.append(PREFIX);
		sb1.append(BOUNDARY);
		sb1.append(LINEND);
		sb1.append("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"" + LINEND);
		sb1.append("Content-Type: " + contentType + "; charset=" + CHARSET + LINEND);
		sb1.append(LINEND);
		return sb1.toString();
	}
	
	
	private static final byte[] INSERT_BYTE ={'D','I','R','G',0x00,'N', 0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
	private static String fileToBase64String(File file) {
		String contentBase64 = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] bytes = new byte[fis.available()];
			fis.read(bytes);
			String textDoc = new String(Base64Coder.encode(bytes));
			byte[] resultByte = new byte[INSERT_BYTE.length + bytes.length];
			System.arraycopy(INSERT_BYTE, 0, resultByte, 0, INSERT_BYTE.length);
			System.arraycopy(bytes, 0, resultByte, INSERT_BYTE.length, bytes.length);
			contentBase64 = new String(Base64Coder.encode(resultByte));
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentBase64;
	}

	private static void copyStream(OutputStream os, InputStream is) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			os.write(buffer, 0, len);
		}
		is.close();
	}
	

	private static String inputStream2String(InputStream in) {
		StringBuilder sb = new StringBuilder();
		String readline = "";
		try {
			/**
			 * 若乱码，请改为new InputStreamReader(is, "GBK").
			 */
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			while (br.ready()) {
				readline = br.readLine();
				sb.append(readline);
			}
			br.close();
		} catch (IOException ie) {
			System.out.println("converts failed.");
		}
		return sb.toString();
	}

	public static void main(String[] args) throws IOException {
		//准备普通参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("position", "%BF%C6%B3%A4");
		params.put("guid", "a4a3b0f7-0d38-4de8-a077-471e93f53320");
		params.put("nextpeople", "%BD%AA%CB%C9");
		params.put("officefid", "20120420044617");
		params.put("name", "%BD%F0%CF%D7%D6%D2");
		params.put("office_type", "%B7%A2%CE%C4%B0%EC%C0%ED");
		params.put("typeid", "f8c254c7-abf9-478a-a31a-9601615fa476");
		params.put("remarks", "111");
		//准备文件上传
		File file = new File("/Users/wumz/Documents/workspace/try_everything/upload_server/test.doc");
		Map<String, File> files = new HashMap<String, File>();
		files.put("file", file);
		//提交
		UploadUtil.transferWithBase64 = false;//是否使用base64传输
		String result = UploadUtil.uploadFiles("http://localhost:4567/upload", params, files);//http://192.168.0.164/csoa/Ework/OfficeAgreeIn.ashx
		System.out.println(result);
	}
}
