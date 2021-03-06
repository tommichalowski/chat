package com.gft.bench.endpoints.jms;

import java.io.Serializable;
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

import com.gft.bench.endpoints.NotificationHandler;
import com.gft.bench.endpoints.RequestHandler;
import com.gft.bench.endpoints.ServerEndpoint;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.exceptions.ChatException;

/**
 * Created by tzms on 3/31/2016.
 */
public class ServerJmsEndpoint implements ServerEndpoint {

	private static final String CLIENT_QUEUE_SUFFIX = ".to.client";
	private static final String SERVER_QUEUE_SUFFIX = ".to.server";
    private static final Log log = LogFactory.getLog(ServerJmsEndpoint.class);
    protected final String brokerUrl;
    protected ActiveMQConnectionFactory connectionFactory;
    protected Connection connection;
    protected Session session;
    MessageConsumer messageConsumer;
    protected ChatEventListener eventListener;
    private ConcurrentHashMap<String, MessageConsumer> serverReceivers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, MessageProducer> serverProducers = new ConcurrentHashMap<>();

    
    public ServerJmsEndpoint(String brokerUrl) throws ChatException {
        
    	this.brokerUrl = brokerUrl;
        connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        try {
			connection = connectionFactory.createConnection();
			connection.start();
	        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			throw new ChatException("Server can NOT create JMS connection!", e);
		}  
    }
    
    
    @Override
	public <T extends Serializable> void sendNotification(T request) {
		
		try {
			Message message = MessageBuilderUtil.buildMessage(request);		
			MessageProducer producer = getServerMessageProducer(request.getClass());
			producer.send(message); 
		} catch (JMSException e) {
			log.error("Server request method ERROR", e);
		}
	}
    
    
	@Override
	public <T extends Serializable> void registerNotificationListener(Class<T> clazz, NotificationHandler<T> handler) {

		log.info("\n\nServer registerNotificationListener method, clazz name: " + clazz.getName());
		try {
			//TODO: only one handler for type possible. Add handler to map and after deserialization iterate map? 
			MessageConsumer consumer = getServerMessageReceiver(clazz); 
			consumer.setMessageListener(message -> {
				try {
					T event = MessageBuilderUtil.buildEvent(message);
					handler.onMessage(event);
				} catch (JMSException e) {
					log.error("Server registerNotificationListener Message listener lambda error", e);
					e.printStackTrace();
				}
			});
		} catch (JMSException e) {
			log.error("Server registerNotificationListener Message listener lambda error", e);
			e.printStackTrace();
		}
	}
	
    
	@Override
	public <TRequest extends Serializable, TResponse extends Serializable> void registerListener(
			Class<TRequest> clazz, RequestHandler<TRequest, TResponse> handler) {

		log.info("\n\nServer registerListener method, clazz name: " + clazz.getName());
		try {
			//TODO: only one handler for type possible. Add handler to map and after deserialization iterate map? 
			MessageConsumer consumer = getServerMessageReceiver(clazz); 
			consumer.setMessageListener(message -> {
				try {
					TRequest event = MessageBuilderUtil.buildEvent(message);
					TResponse response = handler.onMessage(event);
					
					Message responseMsg = MessageBuilderUtil.buildMessage(response, message.getJMSReplyTo(), message.getJMSCorrelationID());						
					MessageProducer producer = session.createProducer(message.getJMSReplyTo());
	            	producer.send(message.getJMSReplyTo(), responseMsg);
				} catch (JMSException e) {
					log.error("Server registerListener Message listener lambda error", e);
					e.printStackTrace();
				}
			});
		} catch (JMSException e) {
			log.error("Server registerListener Message listener lambda error", e);
			e.printStackTrace();
		}
	}
	   	
	
    private synchronized <T> MessageConsumer getServerMessageReceiver(Class<T> clazz) throws JMSException {
		
    	MessageConsumer consumer = serverReceivers.get(clazz.getName());
    	if (consumer == null) {
    		log.info("\n\nServer created receiver: " + clazz.getName() + SERVER_QUEUE_SUFFIX);
    		Destination queue = session.createQueue(clazz.getName() + SERVER_QUEUE_SUFFIX);
    		consumer = session.createConsumer(queue);
    		//consumer.setMessageListener(new JmsMessageListener<T>(clazz, this.eventListener));
    		serverReceivers.putIfAbsent(clazz.getName(), consumer);
    	}
    	
    	return consumer;
	}
    
    private synchronized <T> MessageProducer getServerMessageProducer(Class<T> clazz) throws JMSException {
		
    	MessageProducer producer = serverProducers.get(clazz);
    	if (producer == null) {
			Destination queue = session.createQueue(clazz.getName() + CLIENT_QUEUE_SUFFIX);
    		producer = session.createProducer(queue);
			serverProducers.putIfAbsent(clazz.getName(), producer);
    	}
    	
    	return producer;
	}
        

//    @Override
//    public void sendEvent(DataEvent event) {
//
//        try {
//            TextMessage textMsg = EventBuilderUtil.buildTextMessage(event);
//            
//            if (event.getType().isRequestResponse()) {
//            	MessageProducer producer = session.createProducer(textMsg.getJMSReplyTo());
//            	producer.send(textMsg.getJMSReplyTo(), textMsg);
//            } else {
//            	Destination destination = session.createQueue(MESSAGE_QUEUE_TO_CLIENT);
//                MessageProducer producer = session.createProducer(destination);
//            	producer.send(textMsg);
//            }
//        } catch (JMSException e) {
//        	log.error("ERROR on server create user!!!");
//            e.printStackTrace();
//        }
//    }

    
//	@Override
//	public void setEventListeners(ChatEventListener eventListener) throws ChatException {
//		
//		this.eventListener = eventListener;
//		
//		try {
//			getServerMessageReceiver(ChatMessageEvent.class);
//			getServerMessageReceiver(CreateUserEvent.class);
//			getServerMessageReceiver(RoomChangedEvent.class);
//	
//	        getServerMessageProducer(ChatMessageEvent.class);
//	        getServerMessageProducer(CreateUserEvent.class);
//	        getServerMessageProducer(RoomChangedEvent.class);
//		} catch (JMSException e) {
//			throw new ChatException("Server can NOT create JMS queues!", e);
//		}
//	}
	
    
    @Override
    public void cleanup() throws JMSException {
    	if (messageConsumer != null) {
    		messageConsumer.close();
    	}
    	if (session != null) {
    		session.close();
    	}
    	if (connection != null) {
    		connection.close();
    	}
    }

}
