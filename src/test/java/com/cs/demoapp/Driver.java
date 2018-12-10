package com.cs.demoapp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Recordset;
import com.cs.utils.Lib;
import com.cs.utils.Reports;




public class Driver {
	
	@BeforeSuite
	public static void beforeSuite(){
		Assert.assertTrue(Lib.init());
		Reports.onStart();
	}
	
	@BeforeTest
	public static void beforeTest(){
	}
	
	
	@BeforeClass
	public static void beforeClass(){
	}
	
	@BeforeMethod
	public static void beforeMethod(){
	}
		
	@org.testng.annotations.Test
	public static void Test() throws FilloException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		String tcQuery="Select * from RunManager";
		
		Recordset tc=Lib.CONN.executeQuery(tcQuery);
		
		//reading the test cases
		while(tc.next()){
			
			if(tc.getField("run_ind").equals("Y")){
		
				//reading the test data
				String tdQuery = "Select * from Data";
				Recordset td = Lib.CONN.executeQuery(tdQuery);
				
				System.out.println("Test Case ID: " + tc.getField("tc_id"));
				System.out.println("Test Desc: " + tc.getField("tc_desc"));
				
				Reports.onTestStart(tc.getField("tc_id") +  ": " + tc.getField("tc_desc"));
				
				HashMap<String, String> test_data = new HashMap<String, String>();
				ArrayList<String> fields = td.getFieldNames();
				
				//test data loop
				while(td.next()){
					if(td.getField("tc_id").endsWith(tc.getField("tc_id"))){
						
					
					//writing the test data to hash map
					for(int i =0; i<fields.size(); i++){
						if(td.getField(i).value() != ""){
						test_data.put(fields.get(i), td.getField(i).value());}
					}
					
					//System.out.println("Test Data:" + "\n" + test_data);
					
					//reading the test steps
					String tsQuery = "Select * from StepDefinition";
					Recordset ts = Lib.CONN.executeQuery(tsQuery);
					
					System.out.println("Test Data ID: " + td.getField("data_id"));
					System.out.println("Test Data:" + "\t" + test_data);
					Reports.test_info.add("Test Data ID: " + td.getField("data_id") + "\n" + test_data);
					
					while(ts.next()){
						
						if(ts.getField("tc_id").equals(tc.getField("tc_id")) && ts.getField("run_mode").equals("Y")){
							
							String step_no = ts.getField("step_no");
							String step_desc = ts.getField("step_desc");
							String ex = ts.getField("expected_results");
							
							HashMap<String, String> expected_results = new HashMap<String, String>();
							
							if(ex.length()!=0){
								String[] keyValuePairs = ex.split(";");     
								for(String pair : keyValuePairs)            
								{
							    String[] entry = pair.split("=");        
							    expected_results.put(entry[0].trim(), entry[1].trim());
								}
							}
							
							System.out.print("Test Step " + step_no + "." + "\t" + step_desc + "\t" );
							System.out.println("Expected Results: " + expected_results);
							Reports.test_info.add("Expected Results: " + expected_results);
							
							String keyword = ts.getField("keyword");
							
							try{
								Method method = Keywords.class.getMethod(keyword, HashMap.class, HashMap.class);
								Object result = method.invoke(Keywords.class, expected_results, test_data);
								
								Reports.logTestResult(step_no + ". " + step_desc, result.toString());
							}catch(java.lang.NoSuchMethodException nsme){
								Method method = Lib.class.getMethod(keyword, HashMap.class, HashMap.class);
								Object result = method.invoke(Lib.class, expected_results, test_data);
								
								Reports.logTestResult(step_no + ". "  + step_desc, result.toString());
								}
							}
						}
					}
				}
			}
		}
	}

	@AfterSuite
	public static void afterSuite(){
		Lib.CONN.close();
		Reports.onFinish();
	}

}
