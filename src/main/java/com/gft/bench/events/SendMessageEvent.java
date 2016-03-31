package com.gft.bench.events;

/**
 * Created by tzms on 3/31/2016.
 */
public class SendMessageEvent implements ChatEvent {

    private final EventType type;
    private final String room;
    private final String data;


    public SendMessageEvent(EventType type, String room) {
        this.type = type;
        this.room = room;
        this.data = null;
    }

    public SendMessageEvent(EventType type, String room, String data) {
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
        return "SendMessageEvent{" +
                "type=" + type +
                ", room='" + room + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}

