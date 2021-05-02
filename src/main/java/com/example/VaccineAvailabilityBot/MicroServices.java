package com.example.VaccineAvailabilityBot;

//import static io.restassured.RestAssured.given;

import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
//import com.sun.xml.bind.v2.model.core.AttributePropertyInfo;

//import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

@Component
public class MicroServices {

	
	private static String apiJava(String districtId) throws Exception {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyy");
		LocalDateTime now = LocalDateTime.now();
		URL url = new URL(
				"https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id="+districtId+"&date="+dtf.format(now));
		String inline = "";
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();

		// Getting the response code
		int responsecode = conn.getResponseCode();

// 		if (responsecode != 200) {
// 			throw new RuntimeException("HttpResponseCode: " + responsecode);
// 		} else
// 		{

			Scanner scanner = new Scanner(url.openStream());
			// Write all the JSON data into a string using a scanner
			while (scanner.hasNext()) {
				inline += scanner.nextLine();
			}
			// Close the scanner
			scanner.close();
// 		}
		
		return inline;
	}
	
//	private static String API(String districtId,String age)
//	{
//		RestAssured.baseURI = "https://cdn-api.co-vin.in/api/";
//		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyy");
//		LocalDateTime now = LocalDateTime.now();
//		String res = given().header("Content-Type", "application/json").queryParam("district_id", districtId)
//				.queryParam("date", dtf.format(now)).when().get("v2/appointment/sessions/public/calendarByDistrict")
//				.then().extract().asString();
//		return res;
//	}
	
	private static String getDristrictCode(String state,String districtName) throws Exception {
		String code=null;
		String st=state.replaceAll("%20", " ");
		String dt=districtName.replaceAll("%20", " ");
		FileReader fileReader =null;
		List<String[]> data= null;
		fileReader = new FileReader("//usr//local//lib//StateDistrict.csv");
		CSVReader reader = new CSVReaderBuilder(fileReader).build();
		data=reader.readAll();
		for(String row[]:data)
		{
			if(row[1].equalsIgnoreCase(st))
			{
				if(row[3].equalsIgnoreCase(dt))
					{code = row[2];
					return code;
					}
					
			}
		}
		
		fileReader.close();
		reader.close();
		return code;
	}
	
	@SuppressWarnings("null")
	public static String checkAvailability(String state, String districtName,String age) throws Exception {
		String district=null;
		
		district=getDristrictCode(state, districtName);
		if(district==null)
			return "Incorrect State or District";
		
		String res=apiJava(district);
//				API(district,age);
		JsonPath js = new JsonPath(res);
		int cap = 0;
		int ageLimit = 0;
		StringBuilder data = new StringBuilder();
		int count = 0;
		for (int i = 0; i < js.getInt("centers.size()"); i++) {
			for (int j = 0; j < js.getInt("centers[" + i + "].sessions.size()"); j++) {
				
				cap = js.getInt("centers[" + i + "].sessions[" + j + "].available_capacity");
				ageLimit = js.getInt("centers[" + i + "].sessions[" + j + "].min_age_limit");
				
//					&&cap>0
				if (ageLimit <=Integer.parseInt(age)) {
					count++;
					if(count>1)
					data.append(",");
					data.append(js.getString("centers[" + i + "].name") + "|");
					data.append(js.getString("centers[" + i + "].sessions[" + j + "].date") + "|");
					data.append(js.getString("centers[" + i + "].sessions[" + j + "].vaccine") + "|");
					data.append(js.getString("centers[" + i + "].sessions[" + j + "].available_capacity"));
//						data.append("\n");
				}
			}
		}
		return data.toString();
	}
	
	
}
