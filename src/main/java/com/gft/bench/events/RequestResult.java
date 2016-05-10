package com.gft.bench.events;

public enum RequestResult {
	SUCCESS("SUCCESS"), ERROR("SUCCESS");
	
	String type;
    
    private RequestResult(String type) {
		this.type = type;
	}
}
