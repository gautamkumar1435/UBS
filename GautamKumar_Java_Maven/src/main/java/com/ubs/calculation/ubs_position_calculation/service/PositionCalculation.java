package com.ubs.calculation.ubs_position_calculation.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ubs.calculation.ubs_position_calculation.utils.FileReaderUtility;

public class PositionCalculation {

	private String instrument = "Instrument";
	private String account = "Account";
	private String accountType = "AccountType";
	private String quantity = "Quantity";
	private String delta = "Delta";
	private String ttBuy = "B";
	private String ttSale = "S";
	private String transactionType = "TransactionType";
	private String transactionQuantity = "TransactionQuantity";

	@SuppressWarnings("unchecked")
	public Map calculationEngine(String _inputTransactionFilePath, String _openingDataFilePath) {

		FileReaderUtility fileReader = new FileReaderUtility();
		JSONArray inputTransaction = fileReader.readJsonData(_inputTransactionFilePath);
		JSONArray openingData = fileReader.readCsvData(_openingDataFilePath, false);
		
		Map<String, Map> closingData = new HashMap<String, Map>();
		openingData.forEach(opening -> {
			Map<String, Object> rowData = new HashMap<>();
			JSONObject openingDataJson = (JSONObject) opening;
			rowData.put(instrument, openingDataJson.get(instrument).toString());
			rowData.put(account, openingDataJson.get(account).toString());
			rowData.put(accountType, openingDataJson.get(accountType).toString());
			rowData.put(quantity, Long.parseLong(openingDataJson.get(quantity).toString()));
			rowData.put(delta, Long.parseLong(openingDataJson.get(quantity).toString()));
			closingData.put(openingDataJson.get(instrument).toString()
					.concat("_".concat(openingDataJson.get(accountType).toString())), rowData);
		});

		inputTransaction.forEach(transaction -> {

			JSONObject transactionDataJson = (JSONObject) transaction;
			Long e = (long) closingData.get(transactionDataJson.get(instrument).toString().concat("_E")).get(quantity);
			Long i = (long) closingData.get(transactionDataJson.get(instrument).toString().concat("_I")).get(quantity);
			Long currentValue = Long.parseLong(transactionDataJson.get(transactionQuantity).toString());
			if (transactionDataJson.get(transactionType).toString().equalsIgnoreCase(ttBuy)) {
				e = e + currentValue;
				i = i - currentValue;
			} else if (transactionDataJson.get(transactionType).toString().equalsIgnoreCase(ttSale)) {
				e = e - currentValue;
				i = i + currentValue;
			}
			
			closingData.get(transactionDataJson.get(instrument).toString().concat("_E")).put(quantity, e);
			closingData.get(transactionDataJson.get(instrument).toString().concat("_I")).put(quantity, i);
		});
		System.out.println("\n\nCalculated End of the Day Position:-");
		Iterator<String> iteratorClosingData = closingData.keySet().iterator();
		iteratorClosingData.forEachRemaining(closing ->{
			Map currentRow = closingData.get(closing);
			Long delta = (long)currentRow.get(this.delta);
			Long quantity = (long)currentRow.get(this.quantity);
			currentRow.put(this.delta, quantity-delta);
			System.out.println(currentRow.get(instrument)+","+currentRow.get(account)+","+currentRow.get(accountType)+","+currentRow.get(this.quantity)+","+currentRow.get(this.delta));
		});
		return closingData;
	}



	/*public static void main(String[] args) {
		PositionCalculation ps = new PositionCalculation();
		ps.calculationEngine();
	}*/
}
