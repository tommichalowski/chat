package com.gft.bench.events.business;

import java.io.Serializable;

public class RoomChangedEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String user;
	public String room;
	
	
	public RoomChangedEvent(String user, String room) {
		this.user = user;
		this.room = room;
	}
}
