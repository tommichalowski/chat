package com.gft.bench.events;

import javax.jms.Destination;

public class EnterToRoomRequest implements DataEvent {

    private final EventType type;
    private final String room;
    private final String message;
    private String userName;
    private Destination replyTo;
    private RequestResult result;


    public EnterToRoomRequest(EventType type, String room) {
        this.type = type;
        this.room = room;
        this.message = null;
    }

    public EnterToRoomRequest(EventType type, String room, String message, RequestResult result) {
        this.type = type;
        this.room = room;
        this.message = message;
        this.result = result;
    }


    @Override
    public EventType getType() {
        return type;
    }

    public String getRoom() {
        return room;
    }

    @Override
    public String getData() { return message; }

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
	
	public RequestResult getRequestResult() {
		return result;
	}

	@Override
	public void setUserName(String userName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRoom(String room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setData(String data) {
		// TODO Auto-generated method stub
		
	}
}
