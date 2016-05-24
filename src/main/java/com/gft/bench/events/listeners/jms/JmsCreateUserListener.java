package com.gft.bench.events.listeners.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.business.CreateUserEvent;

public class JmsCreateUserListener implements MessageListener {

	private static final Log log = LogFactory.getLog(JmsCreateUserListener.class);
	private ChatEventListener chatEventListener;
	
	
	@Override
    public void onMessage(Message message) {

		//DataEvent event = EventBuilderUtil.buildEvent(message);
		try {
			if (message instanceof TextMessage) {
				TextMessage textMsg = (TextMessage) message;
				CreateUserEvent event = new CreateUserEvent();
				event.userName = textMsg.getText();

				log.info("JmsCreateUserListener, user name: " + event.userName);
				//chatClient.asyncEventReceived(event);
				chatEventListener.notifyListeners(CreateUserEvent.class, event);
			}
		} catch (JMSException e) {
			log.warn("JmsCreateUserListener received message of NOT TextMessage type!");
		}
    }
}
