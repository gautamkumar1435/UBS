package com.ubs.calculation.ubs_position_calculation;

import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.ubs.calculation.ubs_position_calculation.service.PositionCalculation;
import com.ubs.calculation.ubs_position_calculation.utils.FileReaderUtility;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	@SuppressWarnings("unchecked")
	public void testApp() {
		boolean testflag = true;
		PositionCalculation calculator = new PositionCalculation();
		FileReaderUtility fileReader = new FileReaderUtility();
		
		String inputTransactionFilePath="inputFiles/1537277231233_Input_Transactions.txt"; 
		String openingDataFilePath="inputFiles/Input_StartOfDay_Positions.txt";
		String expectedDataFilePath="inputFiles/Expected_EndOfDay_Positions.txt";
		
		System.out.println("Expected End of the Day Position:-");
		JSONArray expectedData = fileReader.readCsvData(expectedDataFilePath, true);
		
		Map<String, Map> closingData = calculator.calculationEngine(inputTransactionFilePath, openingDataFilePath);
		
		for(int i=0; i<expectedData.size(); i++) {
			JSONObject openingDataJson = (JSONObject) expectedData.get(i);
			String key = openingDataJson.get("Instrument").toString()
					.concat("_".concat(openingDataJson.get("AccountType").toString()));
			
			if(closingData.containsKey(key)) {
				Long quantity = Long.parseLong(openingDataJson.get("Quantity").toString());
				Long delta = Long.parseLong(openingDataJson.get("Delta").toString());
				if(quantity.compareTo((long)closingData.get(key).get("Quantity")) != 0) {
					testflag = false;
					break;
				}
				else if(delta.compareTo((long)closingData.get(key).get("Delta")) != 0) {
					testflag = false;
					break;
				}
			}
			else {
				testflag = false;
				break;
			}
		}
		assertTrue(testflag);
	}
}
