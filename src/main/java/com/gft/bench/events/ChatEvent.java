package com.gft.bench.events;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ChatEvent {

    EventType getType();
    String getMessage();
}
