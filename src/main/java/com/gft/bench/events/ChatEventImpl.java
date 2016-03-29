package com.gft.bench.events;

public class ChatEventImpl implements ChatEvent {

	EventType type;
	String data;
	
	
	public ChatEventImpl(EventType type, String data) {
		this.type = type;
		this.data = data;
	}
	
	
	@Override
	public EventType getType() {
		return type;
	}

	@Override
	public String getData() {
		return data;
	}

}
