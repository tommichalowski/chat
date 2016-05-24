package com.gft.bench.events.business;

public class CreateUserEvent implements BusinessEvent {

	private static final long serialVersionUID = 1L;
	public String userName;
	
	
	@Override
	public String getData() {
		return userName;
	}

	@Override
	public void setData(String userName) {
		this.userName = userName;
	}
}
