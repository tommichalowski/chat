package com.gft.bench.events;

/**
 * Created by tzms on 3/31/2016.
 */
public class MessageEvent implements ChatEvent {

    private final EventType type;
    private final String room;
    private final String data;


    public MessageEvent(EventType type, String room) {
        this.type = type;
        this.room = room;
        this.data = null;
    }

    public MessageEvent(EventType type, String room, String data) {
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
        return "MessageEvent{" +
                "type=" + type +
                ", room='" + room + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}

