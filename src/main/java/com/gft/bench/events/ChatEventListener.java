package com.gft.bench.events;

import java.io.Serializable;

import com.gft.bench.endpoints.NotificationHandler;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ChatEventListener {

	<T extends Serializable> void registerNotificationListener(Class<T> clazz, NotificationHandler<T> handler);
	
	<T extends Serializable> void sendNotification(T request);
	
	//<T> void registerListener(Class<T> clazz, EventListener<T> listener);
	
	<T> void notifyListeners(Class<T> clazz, T event);
	
	<T> EventListener<T> getEventListener(Class<T> clazz);
}
