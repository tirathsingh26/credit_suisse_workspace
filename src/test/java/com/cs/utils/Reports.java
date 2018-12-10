package com.cs.utils;

import java.io.File;
import java.util.ArrayList;

import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class Reports{

	// extend reports variables
	public static ExtentReports extent;
	public static ExtentTest parent, test;
	public static ExtentHtmlReporter htmlReporter;
	
	public static ArrayList<String> test_info = new ArrayList<String>();

	public static void onTestStart(String tc_desc) {
		parent = extent.createTest(tc_desc);

	}

	public static void logTestResult(String step_desc, String result) {
		if(result .equalsIgnoreCase("true")){
			test = parent.createNode(step_desc).pass(result);
		}else if(result.equalsIgnoreCase("false")){
			test = parent.createNode(step_desc).fail(result);
		}else{
			test = parent.createNode(step_desc).skip(result);
		}
		
		//writing the test info
		if(test_info.size() != 0){
			for(String info : test_info){
				test.info(info);
			}
			test_info.clear();
		}
	}

	public static void onStart() {

		//create test-output-extent directory if not exists
		File file = new File(System.getProperty("user.dir") + "\\test-output-extent");
		if(! file.exists()){
			file.mkdir();
		}
		
		// initialize the HtmlReporter
		htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir")
				+ "\\test-output-extent\\" + "extent.html");

		// initialize ExtentReports and attach the HtmlReporter
		extent = new ExtentReports();

		// attach only HtmlReporter
		extent.attachReporter(htmlReporter);

	}

	public static void onFinish() {
		// flush the reports
		extent.flush();

	}

	

}
