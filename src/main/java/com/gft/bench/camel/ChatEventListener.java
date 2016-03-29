package com.gft.bench.camel;

import javax.jms.JMSException;
import javax.jms.MessageListener;

import com.gft.bench.events.ChatEvent;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ChatEventListener extends MessageListener {

	ChatEvent listenForEvent() throws JMSException;
	
    //void onEvent(ChatEvent event) throws JMSException;
}
