package com.gft.bench.endpoints;

import java.util.concurrent.Future;

import javax.jms.Message;

import com.gft.bench.ResultMsg;
import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.exceptions.RequestException;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ClientEndpoint {

    void listenForEvent();
    
    void sendEvent(ChatEvent event);

    Future<ResultMsg> request(ChatEvent event);
    
    Message receiveMessage(ChatEvent event) throws RequestException;
    
    ResultMsg getResponseWhenCame() throws InterruptedException;
    
    void setEventListener(ChatEventListener messageListener);
}

//<TRequest, TResponse> CompletableFuture<TResponse> request(TRequest request);
//EnterToRoomRequest request = new EnterToRoomRequest();
//EnterToRoomResponse response = endpoint.request<EnterToRoomRequest, Response>(request);