package com.gft.bench.events.listeners.jms;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.SerializationUtils;

public class JmsMessageListener<TResponse> implements MessageListener {

	private static final Log log = LogFactory.getLog(JmsMessageListener.class);
	private ConcurrentHashMap<String, CompletableFuture<?>> futureRequestMap;
	
	
	public JmsMessageListener(ConcurrentHashMap<String, CompletableFuture<?>> futureRequestMap) {
		this.futureRequestMap = futureRequestMap;
	}
	
	
	@Override
    public void onMessage(Message message) {

		try {
			log.info("\n\nJMS messageListener\n\n");
			if (message instanceof BytesMessage) {
				BytesMessage msg = (BytesMessage) message;
				byte[] byteArr = new byte[(int) msg.getBodyLength()];
				msg.readBytes(byteArr);
				
				@SuppressWarnings("unchecked")
				TResponse response = (TResponse) SerializationUtils.deserialize(byteArr);
				
				@SuppressWarnings("unchecked")
				CompletableFuture<TResponse> futureComplete = (CompletableFuture<TResponse>) futureRequestMap.get(message.getJMSCorrelationID());
				futureComplete.complete(response);
			}
		} catch (JMSException e) {
			log.error("JmsMessageListener ERROR", e);
		}
    }
}
