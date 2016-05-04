package com.gft.bench.events;

import javax.jms.Destination;

/**
 * Created by tzms on 3/25/2016.
 */
public interface DataEvent {

    EventType getType();
    String getUserName();
    void setUserName(String userName);
    String getRoom();
    void setRoom(String room);
    String getData();
    void setData(String data);
    Destination getReplyTo();
    void setReplyTo(Destination replyTo);
}
