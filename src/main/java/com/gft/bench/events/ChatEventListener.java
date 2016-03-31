package com.gft.bench.events;

import javax.jms.JMSException;
import javax.jms.MessageListener;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ChatEventListener {

	//void listenForEvents() throws JMSException;

	void eventReceived(ChatEvent event);
	
    //void onEvent(ChatEvent event) throws JMSException;
}
