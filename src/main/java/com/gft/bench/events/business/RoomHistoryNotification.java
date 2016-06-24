package com.gft.bench.events.business;

import java.io.Serializable;

public class RoomHistoryNotification implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String history;
	
	
	public RoomHistoryNotification(String history) {
		this.history = history;
	}
}
