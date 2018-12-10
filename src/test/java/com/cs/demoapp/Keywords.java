package com.cs.demoapp;


import java.util.HashMap;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


import com.cs.utils.Reports;
import com.cs.utils.Lib;

public class Keywords {
	
		public static Response response = null;
	
		public static boolean post(HashMap<String, String> expected_results, HashMap<String, String> test_data){
		
		String url = Lib.CONFIG.getProperty("url") + ":" + Lib.CONFIG.getProperty("port");
		Reports.test_info.add("URL: " + url);
		
		RestAssured.baseURI = url;
		RequestSpecification request = RestAssured.given();
		
		//getting & creating the request header
		String [] hdrs = test_data.get("request_header").split(",");
		Reports.test_info.add("Request Headers: " + test_data.get("request_header"));
		for(String hdr : hdrs){
			String [] arr = hdr.split("="); 
			request.header(arr[0], arr[1]);
		}
		
		//creating body
		String jsonString =  test_data.get("request_body");
		Reports.test_info.add("Request Body: " + jsonString);
		request.body(jsonString);
		
		Reports.test_info.add("Posting Request...");
		response = request.post("/" + test_data.get("end_point"));
		Reports.test_info.add("Request sent.");
		
		if(response  != null){
			//System.out.println(response.getStatusCode());
			//System.out.println(response.getStatusLine());
			Reports.test_info.add("Response Recieved !!");
			Reports.test_info.add("Response Body: " + response.asString());
			Reports.test_info.add("Status Code: " + String.valueOf(response.getStatusCode()));
			Reports.test_info.add("Status Line: " + response.getStatusLine());
			return true;
		}else{
			Reports.test_info.add("Failed to post to end point");
			return false;
		}
	}

}
