package com.gft.bench.events;

/**
 * Created by tzms on 3/31/2016.
 */
public class MessageEvent implements ChatEvent {

	private final EventType type;
	private final String message;
    private final String room;
    private RequestResult result;
    
    
    public MessageEvent(EventType type, String message, String room) {
		this.type = type;
		this.message = message;
		this.room = room;
	}
    
    public MessageEvent(EventType type, String message, String room, RequestResult result) {
		this.type = type;
		this.message = message;
		this.room = room;
		this.result = result;
	}
    
	@Override
	public EventType getType() {
		return type;
	}
	
	@Override
    public String getMessage() {
		return message;
	}

	public String getRoom() {
		return room;
	}

	public RequestResult getResult() {
		return result;
	}

}

