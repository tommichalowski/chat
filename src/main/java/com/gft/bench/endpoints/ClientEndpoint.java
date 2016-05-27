package com.gft.bench.endpoints;

import java.util.concurrent.CompletableFuture;

import javax.jms.JMSException;

import com.gft.bench.events.ChatEventListener;
import com.gft.bench.exceptions.ChatException;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ClientEndpoint {

    <T> void sendEvent(T event);
    
    <TRequest, TResponse> CompletableFuture<TResponse> request(TRequest request);
    
    void setEventListeners(ChatEventListener eventListener) throws ChatException;   
    
    void cleanup() throws JMSException; //TODO: shouldn't exist
}
