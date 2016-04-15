package com.gft.bench.events;

/**
 * Created by tzms on 3/31/2016.
 */
public class MessageEvent implements ChatEvent {

    private final EventType type;
    private RequestResult result;
    private final String room;
    private final String data;


    public MessageEvent(EventType type, String room) {
        this.type = type;
        this.room = room;
        this.data = null;
    }

    public MessageEvent(EventType type, RequestResult result, String room, String data) {
        this.type = type;
        this.result = result;
        this.room = room;
        this.data = data;
    }


    @Override
    public EventType getType() {
        return type;
    }
    
    @Override
	public RequestResult getRequestResult() {
		return result;
	}

    public String getRoom() {
        return room;
    }

    public String getData() { return data; }

    
	@Override
	public String toString() {
		return "MessageEvent [type=" + type + ", result=" + result + ", room=" + room + ", data=" + data + "]";
	}

}

