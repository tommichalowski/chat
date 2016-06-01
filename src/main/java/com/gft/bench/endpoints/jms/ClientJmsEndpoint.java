package com.gft.bench.endpoints.jms;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.ClientEndpoint;
import com.gft.bench.endpoints.NotificationHandler;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.exceptions.ChatException;

/**
 * Created by tzms on 3/31/2016.
 * @param <T>
 * @param <T>
 */
public class ClientJmsEndpoint implements ClientEndpoint, JmsEndpoint {

	private static final String CLIENT_QUEUE_SUFFIX = ".to.client";
	private static final String SERVER_QUEUE_SUFFIX = "to.server";
	private static final Log log = LogFactory.getLog(ClientJmsEndpoint.class);
    protected final String brokerUrl;
    private Connection connection;
    protected Session session;
    MessageProducer producer;
    protected ChatEventListener eventListener;
    private ConcurrentHashMap<String, MessageConsumer> clientReceivers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, MessageProducer> clientProducers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, CompletableFuture<?>> futureRequestMap = new ConcurrentHashMap<>();
    

    public ClientJmsEndpoint(String brokerUrl) throws ChatException {
        
    	this.brokerUrl = brokerUrl;
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        try {
        	connection = connectionFactory.createConnection();
			connection.start();
	        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			throw new ChatException("Client can NOT create JMS connection!", e);
		}
    }
 
	
    @Override
	public <T extends Serializable> void registerNotificationListener(Class<T> clazz, NotificationHandler<T> handler) {

		log.info("\n\nClient registerNotificationListener method, clazz name: " + clazz.getName());
		try {
			//TODO: only one handler for type possible. Add handler to map and after deserialization iterate map? 
			MessageConsumer consumer = getClientMessageReceiver(clazz); 
			consumer.setMessageListener(message -> {
				try {
					T event = MessageBuilderUtil.buildEvent(message);
					handler.onMessage(event);
				} catch (JMSException e) {
					log.error("Client registerNotificationListener Message listener lambda error", e);
					e.printStackTrace();
				}
			});
		} catch (JMSException e) {
			log.error("Client registerNotificationListener Message listener lambda error", e);
			e.printStackTrace();
		}
	}
    
    
	@Override
	public <T extends Serializable> void sendNotification(T request) {
		
		try {
			Message message = MessageBuilderUtil.buildMessage(request);		
			MessageProducer producer = getClientMessageProducer(request.getClass());
			producer.send(message); 
		} catch (JMSException e) {
			log.error("Client request method ERROR", e);
		}
	}

	
	@Override
	public <TRequest extends Serializable, TResponse extends Serializable> CompletableFuture<TResponse> requestResponse(TRequest request) 
			throws JMSException {
		
		CompletableFuture<TResponse> future = new CompletableFuture<TResponse>();
		String correlationId = UUID.randomUUID().toString();
    	futureRequestMap.put(correlationId, future);
    	
		try {
			Destination tempDest = session.createTemporaryQueue();
			MessageConsumer responseConsumer = session.createConsumer(tempDest);
			responseConsumer.setMessageListener(message -> {
				try {
					log.info("\n\nClient request method message listener lambda\n\n");
					TResponse response = MessageBuilderUtil.buildEvent(message);
					
					@SuppressWarnings("unchecked")
					CompletableFuture<TResponse> futureComplete = (CompletableFuture<TResponse>) 
						futureRequestMap.get(message.getJMSCorrelationID());
					futureComplete.complete(response);
				} catch (JMSException e) {
					log.error("Client requestResponse method ERROR", e);
				}
			}); 

			Message message = MessageBuilderUtil.buildMessage(request, tempDest, correlationId);	
			MessageProducer producer = getClientMessageProducer(request.getClass());
			producer.send(message); 	
		} catch (Exception e) {
			log.error("Client requestResponse method ERROR", e);
		}
    	
		return future;
	}

	
	private <T> MessageProducer getClientMessageProducer(Class<T> clazz) throws JMSException {
	
		MessageProducer producer = clientProducers.get(clazz.getName() + SERVER_QUEUE_SUFFIX); 
				
		if (producer == null) {
			Destination queue = session.createQueue(clazz.getName() + SERVER_QUEUE_SUFFIX);
			producer = session.createProducer(queue);
			clientProducers.putIfAbsent(clazz.getName(), producer);
		}
		
		return producer;
	}
	
	
	private <T> MessageConsumer getClientMessageReceiver(Class<T> clazz) throws JMSException {
	
		MessageConsumer consumer = clientReceivers.get(clazz.getName() + CLIENT_QUEUE_SUFFIX);	
			
		if (consumer == null) {
			Destination queue = session.createQueue(clazz.getName() + CLIENT_QUEUE_SUFFIX);
			consumer = session.createConsumer(queue);
			clientReceivers.putIfAbsent(clazz.getName(), consumer);
		}
		
		return consumer;
}
	
	
//	private <TResponse> void createClientReceiveTemporaryQueue(TResponse t) throws JMSException {
//	
//	Destination queue = session.createTemporaryQueue();
//	MessageConsumer consumer = session.createConsumer(queue);
//	consumer.setMessageListener(new JmsMessageListener<TResponse>(futureRequestMap));
//	//clientReceivers.putIfAbsent(clazz.getName(), queue);
//}
	
	
//    private <T> void createClientProducerQueue(Class<T> clazz) throws JMSException {
//		
//		Destination queue = session.createQueue(clazz.getName() + SERVER_QUEUE_SUFFIX);
//		MessageProducer producer = session.createProducer(queue);
//		clientProducers.putIfAbsent(clazz.getName(), producer);
//	}
    		
	
//    @Override
//    public void setEventListeners(ChatEventListener eventListener) throws ChatException {
//        
//    	this.eventListener = eventListener;
//        
//    	try {
//	        createClientReceiveQueue(ChatMessageEvent.class);
//	    	createClientReceiveTemporaryQueue(CreateUserEvent.class);
//	    	createClientReceiveTemporaryQueue(RoomChangedEvent.class);
//	
//	    	createClientProducerQueue(ChatMessageEvent.class);
//	    	createClientProducerQueue(CreateUserEvent.class);
//	    	createClientProducerQueue(RoomChangedEvent.class);
//        } catch (JMSException e) {
//			throw new ChatException("Client can NOT create JMS queues!", e);
//		}
//    }
    
    @Override
    public void cleanup() throws JMSException {
    	if (producer != null) {
    		producer.close();
    	}
    	if (session != null) {
    		session.close();
    	}
    	if (connection != null) {
    		connection.close();
    	}
    }

}
