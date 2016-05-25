package com.gft.bench.endpoints;

import javax.jms.JMSException;

import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.business.BusinessEvent;
import com.gft.bench.exceptions.ChatException;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ClientEndpoint {

    <T extends BusinessEvent> void sendEvent(T event);
    
    void setEventListeners(ChatEventListener eventListener) throws ChatException;   
    
    void cleanup() throws JMSException; //TODO: shouldn't exist
}
