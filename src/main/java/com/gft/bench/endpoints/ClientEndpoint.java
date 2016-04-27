package com.gft.bench.endpoints;

import java.util.concurrent.CompletableFuture;

import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.EventType;
import com.gft.bench.exceptions.RequestException;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ClientEndpoint {

    void listenForEvent();
    
    void sendEvent(ChatEvent event);

    CompletableFuture<ChatEvent> request(ChatEvent event);
    
    CompletableFuture<ChatEvent> receiveEvent(EventType eventType) throws RequestException;
    
    void setEventListener(ChatEventListener messageListener);
}

//<TRequest, TResponse> CompletableFuture<TResponse> request(TRequest request);
//EnterToRoomRequest request = new EnterToRoomRequest();
//EnterToRoomResponse response = endpoint.request<EnterToRoomRequest, Response>(request);