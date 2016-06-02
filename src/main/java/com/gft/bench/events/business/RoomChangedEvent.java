package com.gft.bench.events.business;

import java.io.Serializable;

public class RoomChangedEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String room;
	
	
	public RoomChangedEvent(String room) {
		this.room = room;
	}
}
