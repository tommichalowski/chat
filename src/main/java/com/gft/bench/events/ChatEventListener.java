package com.gft.bench.events;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ChatEventListener {

	//void listenForEvents() throws JMSException;

	void asyncEventReceived(DataEvent event);
	
	void messageReceived(DataEvent event);
	
    //void onEvent(ChatEvent event) throws JMSException;
}
