package com.gft.bench.endpoints.jms;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.gft.bench.endpoints.DestinationType;
import com.gft.bench.endpoints.NotificationHandler;

public class JmsEndpointUtils {

	public static synchronized <T> MessageProducer getMessageProducer(Class<T> clazz, Map<String, MessageProducer> producers, 
			Session session, DestinationType destinationType) throws JMSException {
		
		MessageProducer producer = producers.get(clazz.getName()); 
				
		if (producer == null) {
			System.out.println("\n\nClient created producer: " + clazz.getName() + getQueueSuffix(destinationType) + "\n\n");
			Destination queue = session.createQueue(clazz.getName() + getQueueSuffix(destinationType));
			producer = session.createProducer(queue);
			producers.putIfAbsent(clazz.getName(), producer);
		}
		
		return producer;
	}
	

	public static synchronized <T> MessageConsumer getMessageReceiver(Class<T> clazz, Map<String, MessageConsumer> receivers, 
			@SuppressWarnings("rawtypes") Map<Class, MessageListener> messageListeners, Session session, 
			DestinationType destinationType) throws JMSException {
	
		MessageConsumer consumer = receivers.get(clazz.getName());	
			
		if (consumer == null) {
			Destination queue = session.createQueue(clazz.getName() + getQueueSuffix(destinationType));
			consumer = session.createConsumer(queue);
			consumer.setMessageListener(messageListeners.get(clazz));
			receivers.putIfAbsent(clazz.getName(), consumer);
		}
		
		return consumer;
	}

	
	@SuppressWarnings("rawtypes")
	public static synchronized <T> MessageConsumer getTemporaryMessageReceiver(Class<T> clazz, 
			Map<Class, MessageListener> messageListeners, Map<String, MessageConsumer> receivers, 
			Map<String, Destination> receiversQueue, Session session) throws JMSException {
		
		MessageConsumer consumer = receivers.get(clazz.getName());
		
		if (consumer == null) {
			Destination queue = session.createTemporaryQueue();
			consumer = session.createConsumer(queue);
			consumer.setMessageListener(messageListeners.get(clazz));
			receiversQueue.putIfAbsent(clazz.getName(), queue);
			receivers.putIfAbsent(clazz.getName(), consumer);
		}
		
		return consumer;
	}
	

	@SuppressWarnings("rawtypes")
	public static synchronized <T> void registerHandler(Class<T> clazz, NotificationHandler<T> handler, 
			Map<Class, CopyOnWriteArrayList<NotificationHandler<?>>> notificationHandlers) {
		
		CopyOnWriteArrayList<NotificationHandler<?>> handlers = notificationHandlers.get(clazz);
		
		if (handlers == null) {
			handlers = new CopyOnWriteArrayList<NotificationHandler<?>>();
			notificationHandlers.putIfAbsent(clazz, handlers);
		}
		
		handlers.add(handler);
	}
	
	
	@SuppressWarnings("rawtypes")
	public static synchronized <T> void defineNotificationListener(Class<T> clazz, Map<Class, MessageListener> messageListeners,
			Map<Class, CopyOnWriteArrayList<NotificationHandler<?>>> notificationHandlers) {
		
		if (messageListeners.get(clazz) == null) {
			MessageListener listener = message -> {
				try {
					T event = MessageBuilderUtil.buildEvent(message);
					for (NotificationHandler<?> handler : notificationHandlers.get(clazz)) {
						@SuppressWarnings("unchecked")
						NotificationHandler<T> tHandler = (NotificationHandler<T>) handler;
						tHandler.onMessage(event);
					}
				} catch (JMSException e) {
					e.printStackTrace();
				}
			};
			messageListeners.put(clazz, listener);
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public static synchronized <T> void defineRequestResponseListener(Class<T> clazz, Map<Class, MessageListener> messageListeners,
			ConcurrentHashMap<String, CompletableFuture<?>> futureRequestMap) {
		
		if (messageListeners.get(clazz) == null) {
			MessageListener listener = message -> {
				try {
					T response = MessageBuilderUtil.buildEvent(message);
					
					@SuppressWarnings("unchecked")
					CompletableFuture<T> futureComplete = (CompletableFuture<T>) 
						futureRequestMap.remove(message.getJMSCorrelationID());
					futureComplete.complete(response);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}; 
			messageListeners.put(clazz, listener);
		}
	}
	
	
	private static String getQueueSuffix(DestinationType destinationType) {
		return ".to." + destinationType.type;
	}
}
