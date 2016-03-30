package com.gft.bench.endpoints;

import javax.jms.JMSException;

import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.ChatEventListener;

/**
 * Created by tzms on 3/25/2016.
 */
public interface Endpoint {

    String getEndpointUrl();

    void listenForEvent(ChatEventListener listener, String eventName) throws JMSException;
    
    void sendEvent(ChatEvent event, String eventName) throws JMSException;
}
