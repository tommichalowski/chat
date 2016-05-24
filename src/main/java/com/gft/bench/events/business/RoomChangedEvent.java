package com.gft.bench.events.business;

public class RoomChangedEvent implements BusinessEvent {

	private static final long serialVersionUID = 1L;
	public String room;
	
	
	@Override
	public String getData() {
		return room;
	}

	@Override
	public void setData(String room) {
		this.room = room;
	}
	
}
