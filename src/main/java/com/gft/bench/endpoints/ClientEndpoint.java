package com.gft.bench.endpoints;

import java.util.concurrent.CompletableFuture;

import javax.jms.JMSException;

import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.DataEvent;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ClientEndpoint {

    void listenForEvent();
    
   // void sendEvent(ChatEvent event);

    CompletableFuture<DataEvent> request(DataEvent event);
    
    void sendEvent(DataEvent event);
    
//    CompletableFuture<ChatEvent> receiveEvent(EventType eventType) throws RequestException;
    
    void setEventListener(ChatEventListener messageListener);
    
    void cleanup() throws JMSException;
}

//<TRequest, TResponse> CompletableFuture<TResponse> request(TRequest request);
//EnterToRoomRequest request = new EnterToRoomRequest();
//EnterToRoomResponse response = endpoint.request<EnterToRoomRequest, Response>(request);