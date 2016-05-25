package com.gft.bench.events;

/**
 * Created by tzms on 3/25/2016.
 */
public enum EventType {
    CREATE_USER("CREATE_USER"), ENTER_ROOM("ENTER_ROOM"), ROOM_CHANGED("ROOM_CHANGED"), EXIT_ROOM("EXIT_ROOM"), 
    MESSAGE("MESSAGE");
    
    String type;
    
    private EventType(String type) {
		this.type = type;
	}
    
    
    public boolean isRequestResponse() {
    	
    	if (this == CREATE_USER || this == ENTER_ROOM || this == EXIT_ROOM) {
    		return true;
    	}
    	return false;
    }
}
