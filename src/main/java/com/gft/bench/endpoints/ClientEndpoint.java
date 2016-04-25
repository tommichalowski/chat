package com.gft.bench.endpoints;

import java.util.concurrent.CompletableFuture;

import javax.jms.Message;

import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.ResultMsg;
import com.gft.bench.exceptions.RequestException;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ClientEndpoint {

    void listenForEvent();
    
    void sendEvent(ChatEvent event);

    CompletableFuture<ResultMsg> request(ChatEvent event);
    
    Message receiveMessage(ChatEvent event) throws RequestException;
    
    ResultMsg getResponseWhenCame() throws InterruptedException;
    
    void setEventListener(ChatEventListener messageListener);
}

//<TRequest, TResponse> CompletableFuture<TResponse> request(TRequest request);
//EnterToRoomRequest request = new EnterToRoomRequest();
//EnterToRoomResponse response = endpoint.request<EnterToRoomRequest, Response>(request);