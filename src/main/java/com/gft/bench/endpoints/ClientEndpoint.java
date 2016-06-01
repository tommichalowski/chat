package com.gft.bench.endpoints;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

import javax.jms.JMSException;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ClientEndpoint {
    
	<T extends Serializable> void registerNotificationListener(Class<T> clazz, NotificationHandler<T> handler);
	
    <T extends Serializable> void sendNotification(T request);
    
    <TRequest extends Serializable, TResponse extends Serializable> CompletableFuture<TResponse> requestResponse(TRequest request) throws JMSException;
    
    
    
    //void setEventListeners(ChatEventListener eventListener) throws ChatException;   
    
    void cleanup() throws JMSException; //TODO: shouldn't exist

	
}
