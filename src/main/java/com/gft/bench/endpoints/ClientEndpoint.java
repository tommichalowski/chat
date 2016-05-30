package com.gft.bench.endpoints;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

import javax.jms.JMSException;

import com.gft.bench.events.ChatEventListener;
import com.gft.bench.exceptions.ChatException;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ClientEndpoint {

    <T> void sendEvent(T event);
    
    <TRequest extends Serializable, TResponse> CompletableFuture<TResponse> request(TRequest request) throws JMSException;
    
    void setEventListeners(ChatEventListener eventListener) throws ChatException;   
    
    void cleanup() throws JMSException; //TODO: shouldn't exist
}
