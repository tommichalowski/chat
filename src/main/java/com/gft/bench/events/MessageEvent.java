package com.gft.bench.events;

import javax.jms.Destination;

/**
 * Created by tzms on 3/31/2016.
 */
public class MessageEvent implements ChatEvent {

	private final EventType type;
	private String message;
    private String room;
    private String userName;
    private Destination replyTo;
    private RequestResult result;
    
    public MessageEvent(EventType type, String userName) {
    	this.type = type;
    	this.userName = userName;
    }
    
    
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
	
	@Override
	public String getUserName() {
		return userName;
	}

	public Destination getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(Destination replyTo) {
		this.replyTo = replyTo;
	}
	
	public RequestResult getResult() {
		return result;
	}

}

