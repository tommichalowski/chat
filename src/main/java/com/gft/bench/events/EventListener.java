package com.gft.bench.events;

public interface EventListener {
	
	<T> void onEvent(T event);
	
	//<T extends Event> void onEvent(T event, EventListener listener);
}
