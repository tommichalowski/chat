package com.gft.bench.endpoints.jms;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.DestinationType;
import com.gft.bench.endpoints.NotificationHandler;
import com.gft.bench.endpoints.RequestHandler;

public class JmsEndpointUtils {
	
	private static final Log log = LogFactory.getLog(JmsEndpointUtils.class);
	
	
	public static synchronized <T> MessageProducer getMessageProducer(Class<T> clazz, Map<String, MessageProducer> producers, 
			Session session, DestinationType destinationType) throws JMSException {
		
		MessageProducer producer = producers.get(clazz.getName()); 
				
		if (producer == null) {
			System.out.println("\n\nCreated producer for destination: " + destinationType.type + ", class: " + clazz.getName() + getQueueSuffix(destinationType) + "\n\n");
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
	
	
	public static synchronized <TResponse extends Serializable> void sendMessageToTemporaryQueue(TResponse data, Destination replyTo, 
			String correlationId, Session session) throws JMSException {
		
		Message message = MessageBuilderUtil.buildMessage(data, replyTo, correlationId);
		MessageProducer producer = session.createProducer(message.getJMSReplyTo());
		producer.send(replyTo, message);
		producer.close();
	}
	

	@SuppressWarnings("rawtypes")
	public static synchronized <T> void registerNotificationHandler(Class<T> clazz, NotificationHandler<T> handler, 
			Map<Class, CopyOnWriteArrayList<NotificationHandler<?>>> notificationHandlers) {
		
		CopyOnWriteArrayList<NotificationHandler<?>> handlers = notificationHandlers.get(clazz);
		
		if (handlers == null) {
			handlers = new CopyOnWriteArrayList<NotificationHandler<?>>();
			notificationHandlers.putIfAbsent(clazz, handlers);
		}
		
		handlers.add(handler);
	}
	
	
	@SuppressWarnings("rawtypes")
	public static synchronized <TRequest, TResponse> void registerRequestResponseHandler(
			Class<TRequest> clazz, RequestHandler<TRequest, TResponse> handler, 
			Map<Class, CopyOnWriteArrayList<RequestHandler<?, ?>>> requestHandlers) {
		
		CopyOnWriteArrayList<RequestHandler<?, ?>> handlers = requestHandlers.get(clazz);
		
		if (handlers == null) {
			handlers = new CopyOnWriteArrayList<RequestHandler<?, ?>>();
			requestHandlers.putIfAbsent(clazz, handlers);
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
	public static synchronized <TRequest, TResponse extends Serializable> void defineRequestReciverListener(Class<TRequest> tRequest, Class<TResponse> tResponse, 
			Map<Class, MessageListener> messageListeners, Map<Class, CopyOnWriteArrayList<RequestHandler<?, ?>>> requestHandlers,
			Session session) {
		
		if (messageListeners.get(tRequest) == null) {
			MessageListener listener = message -> {
				try {
					TRequest event = MessageBuilderUtil.buildEvent(message);
					for (RequestHandler<?, ?> handler : requestHandlers.get(tRequest)) {
						@SuppressWarnings("unchecked")
						RequestHandler<TRequest, TResponse> rHandler = (RequestHandler<TRequest, TResponse>) handler;
						TResponse response = rHandler.onMessage(event);
						sendMessageToTemporaryQueue(response, message.getJMSReplyTo(), message.getJMSCorrelationID(), session);
					}
				} catch (JMSException e) {
					e.printStackTrace();
				}
			};
			messageListeners.put(tRequest, listener);
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public static synchronized <T> void defineRequestResponseListener(Class<T> clazz, Map<Class, MessageListener> messageListeners,
			ConcurrentHashMap<String, CompletableFuture<?>> futureRequestMap) {
		
		if (messageListeners.get(clazz) == null) {
			MessageListener listener = message -> {
				try {
					log.info("\n\n\nIn RequestResponseListener\n\n\n");
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
