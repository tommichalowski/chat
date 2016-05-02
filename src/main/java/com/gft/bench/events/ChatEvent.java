package com.gft.bench.events;

import javax.jms.Destination;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ChatEvent {

    EventType getType();
    String getMessage();
    String getUserName();
    Destination getReplyTo();
}
