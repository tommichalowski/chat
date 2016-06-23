package com.gft.bench.endpoints;

import java.io.Serializable;

import javax.jms.JMSException;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ServerEndpoint {
    
	<T extends Serializable> void sendNotification(T request);
	
	<T extends Serializable> void registerNotificationListener(Class<T> clazz, NotificationHandler<T> handler);
	
	<TRequest extends Serializable, TResponse extends Serializable> void registerRequestResponseListener(
			Class<TRequest> tRequest, Class<TResponse> tResponse, RequestHandler<TRequest, TResponse> handler);
//	<TRequest extends Serializable, TResponse extends Serializable> void registerListener(
//    		Class<TRequest> clazz, RequestHandler<TRequest, TResponse> handler);
	
	
	
    //void sendEvent(DataEvent event);
    
    //void setEventListeners(ChatEventListener eventListener) throws ChatException;

    void cleanup() throws JMSException;
}

