package com.gft.bench.events;

import com.gft.bench.exceptions.RequestException;

/**
 * Created by tzms on 3/25/2016.
 */
public enum EventType {
    CREATE_USER("CREATE_USER"), ENTER_ROOM("ENTER_ROOM"), EXIT_ROOM("EXIT_ROOM"), MESSAGE("MESSAGE");
    
    String type;
    
    private EventType(String type) {
		this.type = type;
	}
    
    public EventType geEventType(String type) throws RequestException {
    	
    	if (type == null) {
    		throw new RequestException("Not supported event type error occured! Event: " + type);
    	}
    	
    	switch (type) {
			case "CREATE_USER": return CREATE_USER;
			case "ENTER_ROOM": return ENTER_ROOM;
			case "EXIT_ROOM": return EXIT_ROOM;
			case "MESSAGE": return MESSAGE;
			default: throw new RequestException("Not supported event type error occured! Event: " + type);
		}
    }
    
    public boolean isRequestResponse() {
    	
    	if (this == CREATE_USER || this == ENTER_ROOM || this == EXIT_ROOM) {
    		return true;
    	}
    	return false;
    }
}
