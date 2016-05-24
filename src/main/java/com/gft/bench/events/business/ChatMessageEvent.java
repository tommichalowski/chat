package com.gft.bench.events.business;

public class ChatMessageEvent implements BusinessEvent {

	private static final long serialVersionUID = 1L;
	public String message;

	
	@Override
	public String getData() {
		return message;
	}

	@Override
	public void setData(String message) {
		this.message = message;
	}
}
