package com.gft.bench.events;

public class EnterToRoomEvent implements ChatEvent {

    private final EventType type;
    private final String room;
    private final String data;


    public EnterToRoomEvent(EventType type, String room) {
        this.type = type;
        this.room = room;
        this.data = null;
    }

    public EnterToRoomEvent(EventType type, String room, String data) {
        this.type = type;
        this.room = room;
        this.data = data;
    }


    @Override
    public EventType getType() {
        return type;
    }

    public String getRoom() {
        return room;
    }

    public String getData() { return data; }


    @Override
    public String toString() {
        return "EnterToRoomEvent{" +
                "type=" + type +
                ", room='" + room + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
