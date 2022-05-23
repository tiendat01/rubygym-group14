package com.rubygym.utils;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class HttpRequestUtil {
	
	// dành cho bảo mật => tạm thời bỏ qua
//	public static boolean checkAuthentication(HttpServletRequest req) {
//		if (req.getHeader("Authentication") != null)
//			return true;
//		return false;
//	}
	
	// lấy 1 param từ URL
	public static String parseURL(HttpServletRequest req, String servletName) {
		
		String requestURL = req.getRequestURI();
		String subString = "/cnpm/" + servletName +"/";
		return requestURL.substring(subString.length());
	}
	
	// lấy params từ URL, parse URL
	public static Map<String, String> parseQuery(HttpServletRequest req) throws UnsupportedEncodingException {
		
		String queryString = req.getQueryString();;
		 Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		    String[] pairs = queryString.split("&");
		    for (String pair : pairs) {
		        int idx = pair.indexOf("=");
		        query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		    }
		    return query_pairs;
	}
	
	//Lấy dữ liệu trong Body của 1 http request, tra ve Object => ep sang JSONObject
	public static Object getBody(HttpServletRequest req) throws Exception {
			BufferedReader br = req.getReader();
		//	System.out.print(JSONValue.parse(readAllLines(br)).getClass());
			return JSONValue.parse(readAllLines(br));
	}	
	
	//Chuyển đổi BufferedReader sang String
	public static String readAllLines(BufferedReader reader) throws IOException {
	    StringBuilder content = new StringBuilder();
	    String line;
	    
	    while ((line = reader.readLine()) != null) {
	        content.append(line);
	        content.append(System.lineSeparator());
	    }

	    return content.toString();
	}
	
	public static String[] getQuery(HttpServletRequest req) throws Exception{
		try {
			String stringQuery = req.getQueryString();
			if(stringQuery != null) {
			return stringQuery.split("&");
			}
			else {
				return null;
			}
		}
		catch(Exception e) {
			throw e;
		}
	}
}
