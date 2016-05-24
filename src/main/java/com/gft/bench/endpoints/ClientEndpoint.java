package com.gft.bench.endpoints;

import java.io.Serializable;

import javax.jms.JMSException;

import com.gft.bench.events.ChatEventListener;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ClientEndpoint {

    <T extends Serializable> void sendEvent(T event);
    void setEventListener(ChatEventListener eventListener);
//    void setEventListener(EventListener listener);
    
    void cleanup() throws JMSException; //TODO: shouldn't exist
}

//<TRequest, TResponse> CompletableFuture<TResponse> request(TRequest request);
//EnterToRoomRequest request = new EnterToRoomRequest();
//EnterToRoomResponse response = endpoint.request<EnterToRoomRequest, Response>(request);