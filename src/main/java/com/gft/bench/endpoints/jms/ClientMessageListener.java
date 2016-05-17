package com.gft.bench.endpoints.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.events.DataEvent;
import com.gft.bench.events.EventListener;

public class ClientMessageListener implements MessageListener {

	private static final Log log = LogFactory.getLog(ClientMessageListener.class);
	EventListener eventListener;
	
	
	public ClientMessageListener(EventListener eventListener) {
		this.eventListener = eventListener;
	}
	
	
	@Override
	public void onMessage(Message message) {
		
		try {
			DataEvent event = EventBuilderUtil.buildEvent(message);
			//log.info("Client received event: " + event.getType() + "; UserName: " + event.getUserName()); 
			eventListener.onEvent(event);
		} catch (JMSException e) {
			log.error("\nOnMessage ERROR in client!\n\n\n");
			e.printStackTrace();
		}
	}

}
