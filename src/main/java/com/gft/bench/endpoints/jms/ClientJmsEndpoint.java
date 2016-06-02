package com.gft.bench.endpoints.jms;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.ClientEndpoint;
import com.gft.bench.endpoints.DestinationType;
import com.gft.bench.endpoints.NotificationHandler;
import com.gft.bench.exceptions.ChatException;

/**
 * Created by tzms on 3/31/2016.
 * @param <T>
 * @param <T>
 */
public class ClientJmsEndpoint implements ClientEndpoint {

	private static final Log log = LogFactory.getLog(ClientJmsEndpoint.class);
    protected final String brokerUrl;
    private Connection connection;
    protected Session session;
    private ConcurrentHashMap<String, Destination> clientReceiversQueue = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, MessageConsumer> clientReceivers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, MessageProducer> clientProducers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, CompletableFuture<?>> futureRequestMap = new ConcurrentHashMap<>();
	@SuppressWarnings("rawtypes")
	private ConcurrentHashMap<Class, MessageListener> messageListeners = new ConcurrentHashMap<>();
	@SuppressWarnings("rawtypes")
	private ConcurrentHashMap<Class, CopyOnWriteArrayList<NotificationHandler<?>>> notificationHandlers = new ConcurrentHashMap<>();

    

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
			JmsEndpointUtils.registerHandler(clazz, handler, notificationHandlers);
			JmsEndpointUtils.defineNotificationListener(clazz, messageListeners, notificationHandlers); //TODO: where to put it
			JmsEndpointUtils.getMessageReceiver(clazz, clientReceivers, messageListeners, session, DestinationType.CLIENT); 
		} catch (JMSException e) {
			log.error("Client registerNotificationListener Message listener lambda error", e);
			e.printStackTrace();
		}
	}

    
	@Override
	public <T extends Serializable> void sendNotification(T request) {
		
		try {
			Message message = MessageBuilderUtil.buildMessage(request);		
			MessageProducer producer = JmsEndpointUtils.getMessageProducer(request.getClass(), clientProducers, 
					session, DestinationType.SERVER);
			producer.send(message); 
		} catch (JMSException e) {
			log.error("Client request method ERROR", e);
		}
	}

	
	@Override
	public <TRequest extends Serializable, TResponse extends Serializable> CompletableFuture<TResponse> requestResponse(
			TRequest request) throws JMSException {
		
		CompletableFuture<TResponse> future = new CompletableFuture<TResponse>();
		String correlationId = UUID.randomUUID().toString();
    	futureRequestMap.put(correlationId, future);
    	
		try {
			JmsEndpointUtils.defineRequestResponseListener(request.getClass(), messageListeners, futureRequestMap);
			JmsEndpointUtils.getTemporaryMessageReceiver(request.getClass(), messageListeners, clientReceivers, clientReceiversQueue, session);

			Destination replyTo = clientReceiversQueue.get(request.getClass().getName());
			Message message = MessageBuilderUtil.buildMessage(request, replyTo, correlationId);	
			MessageProducer producer = JmsEndpointUtils.getMessageProducer(request.getClass(), clientProducers, 
					session, DestinationType.SERVER);
			producer.send(message); 	
		} catch (Exception e) {
			log.error("Client requestResponse method ERROR", e);
		}
    	
		return future;
	}

	
    @Override
    public void cleanup() throws JMSException {
    	
    	for (MessageConsumer consumer : clientReceivers.values()) {
    		consumer.close();
    	}
    	
    	for (MessageProducer producer : clientProducers.values()) {
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
