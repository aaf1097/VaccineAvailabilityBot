package com.example.VaccineAvailabilityBot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import static io.restassured.RestAssured.given;

@RestController
public class VaccineAvailabilityController {

	@Autowired
	MicroServices microservice;
	
	
	@GetMapping("/checkAvailability")
	public VaccineAvailability greeting(@RequestParam(value = "districtName", defaultValue = "South Goa") String districtName,
			@RequestParam(value = "age", defaultValue = "18") String age,
			@RequestParam(value = "state", defaultValue = "Goa") String state) throws Exception {
		@SuppressWarnings("static-access")
		String response = microservice.checkAvailability(state,districtName,age );
		System.out.println(response);
		return new VaccineAvailability(response);
	}

}
