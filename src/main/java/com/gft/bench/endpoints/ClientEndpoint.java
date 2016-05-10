package com.gft.bench.endpoints;

import javax.jms.JMSException;

import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.DataEvent;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ClientEndpoint {

    void listenForEvent();
    
    void sendEvent(DataEvent event);
    
    void setEventListener(ChatEventListener messageListener);
    
    void cleanup() throws JMSException;
}

//<TRequest, TResponse> CompletableFuture<TResponse> request(TRequest request);
//EnterToRoomRequest request = new EnterToRoomRequest();
//EnterToRoomResponse response = endpoint.request<EnterToRoomRequest, Response>(request);