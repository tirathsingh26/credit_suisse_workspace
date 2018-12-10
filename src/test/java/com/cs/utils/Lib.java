package com.cs.utils;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ResponseBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.cs.demoapp.Keywords;

public class Lib {
	
	public static Connection CONN= null;
	public static Properties CONFIG = null;
	
	public static boolean init(){
		
		Reader reader;
		try {
			reader = new FileReader(new File(System.getProperty("user.dir") + "\\src\\test\\resources\\properties\\config.properties"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
			
		}
		CONFIG = new Properties();
		try {
			CONFIG.load(reader);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		Fillo fillo = new Fillo();
		try {
			CONN = fillo.getConnection(System.getProperty("user.dir") + "\\src\\test\\resources\\master.xlsx");
		} catch (FilloException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * @param expected_results, test_data
	 * @return boolean
	 * Note: Before calling this method, ensure static variable 'response' in keywords class has been initialized
	 */
	public static boolean validateStatusCode(HashMap<String, String> expected_results, HashMap<String, String> test_data){
		Reports.test_info.add("Response Body: " + Keywords.response.asString());
		
		if(expected_results.containsKey("statusCode")){
			int statusCode = Keywords.response.getStatusCode();
			if( String.valueOf(statusCode).equals(expected_results.get("statusCode"))){
				Reports.test_info.add("Actual Results: {statusCode=" + statusCode + "}");
				return true;
			}else{
				Reports.test_info.add("Actual Results: {statusCode=" + statusCode + "}");
				return false;
			}
		}else{
			Reports.test_info.add("Invalid expected result data. statusCode key not found in expected_results");
			return false;
		}
	}
	
	/**
	 * @param expected_results, test_data
	 * @return boolean
	 * Validates the Jpath path key and its value passed in expected_results
	 * Note: Before calling this method, ensure static variable 'response' in keywords class has been initialized
	 */
	public static boolean validateJsonKeyValues(HashMap<String, String> expected_results, HashMap<String, String> test_data){
		Reports.test_info.add("Response Body: " + Keywords.response.asString());
		
		List<Boolean> flags = new ArrayList<Boolean>();
		ResponseBody rb = Keywords.response.getBody();
		JsonPath jsonPath = rb.jsonPath();
		
		for (String key : expected_results.keySet()) {
			String expected_value = expected_results.get(key);
			String actual_value = jsonPath.getString(key);
			
			if(actual_value != null && actual_value.contains(expected_value)){
				Reports.test_info.add("Actual Results: {" + key + "=" + expected_value + "} found in response body");
			}else{
				Reports.test_info.add("Actual Results: {" +  key + "=" + expected_value + "} not found in response body");
				flags.add(false);
			}
		}
		if(flags.contains(false)){
			return false;
		}else{
			return true;
		}
	}
}


