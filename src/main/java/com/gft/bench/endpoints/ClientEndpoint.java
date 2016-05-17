package com.gft.bench.endpoints;

import javax.jms.JMSException;

import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.DataEvent;
import com.gft.bench.events.EventListener;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ClientEndpoint {

    void sendEvent(DataEvent event);
    void setEventListener(ChatEventListener messageListener);
    void setEventListener(EventListener listener);
    
    void listenForEvent(); //TODO: shouldn't exist
    void cleanup() throws JMSException; //TODO: shouldn't exist
}

//<TRequest, TResponse> CompletableFuture<TResponse> request(TRequest request);
//EnterToRoomRequest request = new EnterToRoomRequest();
//EnterToRoomResponse response = endpoint.request<EnterToRoomRequest, Response>(request);