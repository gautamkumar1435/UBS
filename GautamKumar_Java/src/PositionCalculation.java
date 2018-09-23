import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unused")
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
	public void calculationEngine() {

		JSONArray inputTransaction = readJsonData("inputFiles/1537277231233_Input_Transactions.txt");
		JSONArray openingData = readCsvData("inputFiles/Input_StartOfDay_Positions.txt");
		
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
		Iterator<String> iteratorClosingData = closingData.keySet().iterator();
		iteratorClosingData.forEachRemaining(closing ->{
			Map currentRow = closingData.get(closing);
			Long delta = (long)currentRow.get(this.delta);
			Long quantity = (long)currentRow.get(this.quantity);
			currentRow.put(this.delta, quantity-delta);
			System.out.println(currentRow.get(instrument)+","+currentRow.get(account)+","+currentRow.get(accountType)+","+currentRow.get(this.quantity)+","+currentRow.get(this.delta));
		});
	}

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

	public JSONArray readCsvData(String _filePath) {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		String[] headers = null;
		JSONArray csvJsonData = new JSONArray();
		try {
			br = new BufferedReader(new FileReader(_filePath));
			while ((line = br.readLine()) != null) {
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

	public static void main(String[] args) {
		System.out.println("======== START =======");
		/*
		 * String filePath = "inputFiles/Expected_EndOfDay_Positions.txt"; String
		 * jsonFilePath = "inputFiles/1537277231233_Input_Transactions.txt";
		 * PositionCalculation ps = new PositionCalculation(); JSONArray csvData =
		 * ps.readCsvData(filePath); JSONArray jsonData = ps.readJsonData(jsonFilePath);
		 * System.out.println(csvData); System.out.println(jsonData);
		 */

		PositionCalculation ps = new PositionCalculation();
		ps.calculationEngine();

		System.out.println("======== END =======");
	}
}
