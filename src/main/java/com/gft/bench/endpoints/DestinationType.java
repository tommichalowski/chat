package com.gft.bench.endpoints;

public enum DestinationType {

	CLIENT("client"), SERVER("server");
	
	public String type;
	
	
	DestinationType(String type) {
		this.type = type;
	}
}
