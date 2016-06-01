package com.gft.bench.endpoints;


public interface NotificationHandler<T> {
	
 	 void onMessage(T notification);
 }