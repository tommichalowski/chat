package com.gft.bench.events.notification;

import com.gft.bench.events.Event;
import com.gft.bench.events.EventType;

public class RoomChanged implements Event {

	public String room;
	
	
	public EventType geEventType() {
		return EventType.ROOM_CHANGED;
	}
}
