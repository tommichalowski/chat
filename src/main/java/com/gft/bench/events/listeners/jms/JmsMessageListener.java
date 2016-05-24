package com.gft.bench.events.listeners.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.business.BusinessEvent;

public class JmsMessageListener<T extends BusinessEvent> implements MessageListener {

	private static final Log log = LogFactory.getLog(JmsMessageListener.class);
	private ChatEventListener chatEventListener;
	private Class<T> clazz;
	
	public JmsMessageListener(Class<T> clazz, ChatEventListener chatEventListener) {
		this.clazz = clazz;
		this.chatEventListener = chatEventListener;
	}
	
	@Override
    public void onMessage(Message message) {

		//DataEvent event = EventBuilderUtil.buildEvent(message);

		if (message instanceof TextMessage) {
			try {
				TextMessage textMsg = (TextMessage) message;
				
				T event = clazz.newInstance();
				event.setData(textMsg.getText());

				//chatClient.asyncEventReceived(event);
				chatEventListener.notifyListeners(clazz, event);
				
			} catch (InstantiationException e) {
				log.error("Error in JmsMessageListener: " + e);
			} catch (IllegalAccessException e) {
				log.error("Error in JmsMessageListener: " + e);
			} catch (JMSException e) {
				log.error("Error in JmsMessageListener: " + e);
			}
		}

    }
}
