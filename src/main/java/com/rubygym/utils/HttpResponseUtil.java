package com.rubygym.utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class HttpResponseUtil {
	
	public static void setResponse(HttpServletResponse resp, JSONArray data, JSONArray error) throws IOException {
		PrintWriter printWriter = resp.getWriter();
		resp.setContentType("application/json; charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		JSONObject bodyJsonResponse = new JSONObject();
		bodyJsonResponse.put("error", error);
		bodyJsonResponse.put("data", data);
		String bodyStringResponse = bodyJsonResponse.toJSONString();
		
		printWriter.print(bodyStringResponse);
	    printWriter.flush();
	    printWriter.close();
	}

}
