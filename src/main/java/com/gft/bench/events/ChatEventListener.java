package com.gft.bench.events;

import javax.jms.JMSException;
import javax.jms.MessageListener;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ChatEventListener extends MessageListener {

	void listenForEvent() throws JMSException;
	
    //void onEvent(ChatEvent event) throws JMSException;
}
