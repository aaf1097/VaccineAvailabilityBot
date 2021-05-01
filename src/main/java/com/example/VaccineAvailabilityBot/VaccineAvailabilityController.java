package com.example.VaccineAvailabilityBot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import static io.restassured.RestAssured.given;

@RestController
public class VaccineAvailabilityController {

	@GetMapping("/checkAvailability")
	public VaccineAvailability greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		String response = checkAvailability();
		System.out.println(response);
		return new VaccineAvailability(response);
	}

	@SuppressWarnings("null")
	private static String checkAvailability() {
		RestAssured.baseURI = "https://cdn-api.co-vin.in/api/";

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyy");
		LocalDateTime now = LocalDateTime.now();

		String res = given().header("Content-Type", "application/json").queryParam("district_id", "151")
				.queryParam("date", dtf.format(now)).when().get("v2/appointment/sessions/public/calendarByDistrict")
				.then().extract().asString();

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
				if (ageLimit == 18) {
					count++;
//						System.out.println(js.getString("centers["+i+"].name"));
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
