package com.gft.bench.events;

import javax.jms.Destination;

/**
 * Created by tzms on 3/31/2016.
 */
public class MessageEvent implements DataEvent {

	private final EventType type;
	private String userName;
    private String room;
    private String data;
    private Destination replyTo;
    private RequestResult result;
    
    
    public MessageEvent(EventType type) {
    	this.type = type;
    }
    
    public MessageEvent(EventType type, String userName) {
    	this.type = type;
    	this.userName = userName;
    }
    
    public MessageEvent(EventType type, String userName, String room) {
		this.type = type;
		this.userName = userName;
		this.room = room;
	}
    
    public MessageEvent(EventType type, String userName, String room, String data) {
		this.type = type;
		this.userName = userName;
		this.room = room;
		this.data = data;
	}

    
    @Override
	public String getUserName() {
		return userName;
	}

    @Override
	public void setUserName(String userName) {
		this.userName = userName;
	}

    @Override
	public String getRoom() {
		return room;
	}

    @Override
	public void setRoom(String room) {
		this.room = room;
	}

    @Override
	public String getData() {
		return data;
	}

    @Override
	public void setData(String data) {
		this.data = data;
	}

    @Override
	public Destination getReplyTo() {
		return replyTo;
	}

    @Override
	public void setReplyTo(Destination replyTo) {
		this.replyTo = replyTo;
	}

	@Override
	public EventType getType() {
		return type;
	}
	
	public RequestResult getResult() {
		return result;
	}

	public void setResult(RequestResult result) {
		this.result = result;
	}
    
}

