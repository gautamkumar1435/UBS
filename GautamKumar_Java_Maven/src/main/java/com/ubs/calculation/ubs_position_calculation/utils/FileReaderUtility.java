package com.ubs.calculation.ubs_position_calculation.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FileReaderUtility {

	public JSONArray readJsonData(String _filePath) {
		JSONParser parser = new JSONParser();
		JSONArray result = new JSONArray();
		try {
			FileReader reader = new FileReader(_filePath);
			Object obj = parser.parse(reader);
			result = (JSONArray) obj;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONArray readCsvData(String _filePath, boolean _isPrint) {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		String[] headers = null;
		JSONArray csvJsonData = new JSONArray();
		try {
			br = new BufferedReader(new FileReader(_filePath));
			while ((line = br.readLine()) != null) {
				if(_isPrint)
					System.out.println(line);
				String[] lineData = line.split(cvsSplitBy);
				if (headers != null) {
					JSONObject rowData = new JSONObject();
					for (int i = 0; i < lineData.length; i++) {
						rowData.put(headers[i], lineData[i]);
					}
					if (rowData.size() > 0)
						csvJsonData.add(rowData);
				} else {
					headers = lineData;
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Error : " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error : " + e.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("Error : " + e.getMessage());
				}
			}
		}
		return csvJsonData;
	}
}
