package com.gft.bench.events;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ChatEventListener {

	//void listenForEvents() throws JMSException;

	void eventReceived(DataEvent event);
	
    //void onEvent(ChatEvent event) throws JMSException;
}
