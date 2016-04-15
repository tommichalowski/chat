package com.gft.bench.events;

public class EnterToRoomRequest implements ChatEvent {

    private final EventType type;
    private RequestResult result;
    private final String room;
    private final String data;


    public EnterToRoomRequest(EventType type, String room) {
        this.type = type;
        this.room = room;
        this.data = null;
    }

    public EnterToRoomRequest(EventType type, RequestResult result, String room, String data) {
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
		return "EnterToRoomRequest [type=" + type + ", result=" + result + ", room=" + room + ", data=" + data + "]";
	}

}
