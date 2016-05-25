package com.gft.bench.events.listeners.jms;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.SerializationUtils;

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

		if (message instanceof BytesMessage) {
			try {
				BytesMessage msg = (BytesMessage) message;
				byte[] byteArr = new byte[(int) msg.getBodyLength()];
				msg.readBytes(byteArr);
				
				@SuppressWarnings("unchecked")
				T event = (T) SerializationUtils.deserialize(byteArr);
				
				chatEventListener.notifyListeners(clazz, event);
				
			} catch (JMSException e) {
				log.error("Error in JmsMessageListener: " + e);
			}
		}

    }
}
