package com.gft.bench.events.notification;

import com.gft.bench.events.Event;
import com.gft.bench.events.EventType;

public class UserCreated implements Event {

	public String user;

	@Override
	public EventType geEventType() {
		return EventType.CREATE_USER;
	}
	
	
}
