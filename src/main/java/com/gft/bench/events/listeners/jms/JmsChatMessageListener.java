package com.gft.bench.events.listeners.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.business.ChatMessageEvent;

public class JmsChatMessageListener implements MessageListener {

	private static final Log log = LogFactory.getLog(JmsChatMessageListener.class);
	private ChatEventListener chatEventListener;
	
	
	@Override
    public void onMessage(Message message) {

		//DataEvent event = EventBuilderUtil.buildEvent(message);
		try {
			if (message instanceof TextMessage) {
				TextMessage textMsg = (TextMessage) message;
				ChatMessageEvent event = new ChatMessageEvent();
				event.message = textMsg.getText();

				log.info("JmsChatMessageListener, message: " + event.message);
				//chatClient.asyncEventReceived(event);
				chatEventListener.notifyListeners(ChatMessageEvent.class, event);
			}
		} catch (JMSException e) {
			log.warn("JmsChatMessageListener received message of NOT TextMessage type!");
		}
    }
}
