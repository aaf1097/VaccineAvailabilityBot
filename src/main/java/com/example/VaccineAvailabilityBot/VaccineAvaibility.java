package com.example.VaccineAvailabilityBot;

public class VaccineAvaibility {

	private final long id;
	private final String content;

	public VaccineAvaibility(long id, String content) {
		this.id = id;
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}
	
}
