package com.gft.bench.endpoints;

import com.gft.bench.events.DataEvent;
import com.gft.bench.events.ChatEventListener;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ServerEndpoint {

    void listenForEvent();
    
    void sendEvent(DataEvent event);
    
    void setEventListener(ChatEventListener messageListener);
}
