package com.gft.bench.events.business;

import java.io.Serializable;

public class CreateUserEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String userName;
	
	
	public CreateUserEvent(String userName) {
		this.userName = userName;
	}
}
