package com.gft.bench.endpoints;

import java.io.Serializable;

import javax.jms.JMSException;

import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.DataEvent;
import com.gft.bench.exceptions.ChatException;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ServerEndpoint {
    
	
    void sendEvent(DataEvent event);
    
    void setEventListeners(ChatEventListener eventListener) throws ChatException;
    
    <TRequest extends Serializable, TResponse extends Serializable> void registerListener(
    		Class<TRequest> clazz, RequestHandler<TRequest, TResponse> handler); 
    
    void cleanup() throws JMSException;
}

