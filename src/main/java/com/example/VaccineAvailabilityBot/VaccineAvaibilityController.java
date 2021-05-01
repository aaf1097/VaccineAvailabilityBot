package com.example.VaccineAvailabilityBot;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import static io.restassured.RestAssured.given;

@RestController
public class VaccineAvaibilityController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
//
	@GetMapping("/checkAvaibility")
	public VaccineAvaibility greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		System.out.println("HI");
		System.out.println(checkAvailability());
		System.out.println("HI");
		return new VaccineAvaibility(counter.incrementAndGet(), String.format(template, checkAvailability()));
	}
	
	 @SuppressWarnings("null")
		private static String checkAvailability()
		{
			RestAssured.baseURI = "https://cdn-api.co-vin.in/api/";
			String res = given().header("Content-Type", "application/json").queryParam("district_id", "151")
					.queryParam("date", "01-05-2021").when()
					.get("v2/appointment/sessions/public/calendarByDistrict").then().extract().asString();
			
			
			
			JsonPath js = new JsonPath(res);
			int cap=0;
			int ageLimit=0;
			StringBuilder data =new StringBuilder();
			int count = 0;
			for(int i=0;i<js.getInt("centers.size()");i++)
			{
				for(int j=0;j<js.getInt("centers["+i+"].sessions.size()");j++) {
					cap=js.getInt("centers["+i+"].sessions["+j+"].available_capacity");
					ageLimit=js.getInt("centers["+i+"].sessions["+j+"].min_age_limit");
//					&&cap>0
					if(ageLimit==18)
						{
						count++;
//						System.out.println(js.getString("centers["+i+"].name"));
						data.append("|"+count+"|");
						data.append(js.getString("centers["+i+"].name")+"|");
						data.append(js.getString("centers["+i+"].sessions["+j+"].date")+"|");
						data.append(js.getString("centers["+i+"].sessions["+j+"].vaccine")+"|");
						data.append(js.getString("centers["+i+"].sessions["+j+"].available_capacity")+"|");					
//						data.append("\n");
						}
				}
			}
			return data.toString();
		}
	
}
