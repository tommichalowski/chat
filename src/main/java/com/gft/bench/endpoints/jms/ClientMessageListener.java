package com.gft.bench.endpoints.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.DataEvent;

public class ClientMessageListener implements MessageListener {

	private static final Log log = LogFactory.getLog(ClientMessageListener.class);
	ChatEventListener messageListener;
	
	
	public ClientMessageListener(ChatEventListener messageListener) {
		this.messageListener = messageListener;
	}
	
	
	@Override
	public void onMessage(Message message) {
		
		try {
			DataEvent event = EventBuilderUtil.buildEvent(message);
			log.info("Client received event: " + event.getType() + "; UserName: " + event.getUserName()); 
			messageListener.messageReceived(event);
		} catch (JMSException e) {
			log.error("\nOnMessage ERROR in client!\n\n\n");
			e.printStackTrace();
		}
		
	}

	
}
