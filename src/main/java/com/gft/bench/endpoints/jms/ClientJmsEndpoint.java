package com.gft.bench.endpoints.jms;

import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.SerializationUtils;

import com.gft.bench.endpoints.ClientEndpoint;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.business.BusinessEvent;
import com.gft.bench.events.business.ChatMessageEvent;
import com.gft.bench.events.business.CreateUserEvent;
import com.gft.bench.events.business.RoomChangedEvent;
import com.gft.bench.events.listeners.jms.JmsMessageListener;
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
    private ConcurrentHashMap<String, Destination> clientReceivers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, MessageProducer> clientProducers = new ConcurrentHashMap<>();
    

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
    public void setEventListeners(ChatEventListener eventListener) throws ChatException {
        
    	this.eventListener = eventListener;
        
    	try {
	        createClientReceiveQueue(ChatMessageEvent.class);
	    	createClientReceiveTemporaryQueue(CreateUserEvent.class);
	    	createClientReceiveTemporaryQueue(RoomChangedEvent.class);
	
	    	createClientProducerQueue(ChatMessageEvent.class);
	    	createClientProducerQueue(CreateUserEvent.class);
	    	createClientProducerQueue(RoomChangedEvent.class);
        } catch (JMSException e) {
			throw new ChatException("Client can NOT create JMS queues!", e);
		}
    }
    
    
	@Override
	public <T extends BusinessEvent> void sendEvent(T event) {

		try {
			log.info("Sending event className: " + event.getClass().getName());
			
			//Destination replyTo = clientReceivers.get(event.getClass().getName());
			MessageProducer producer = clientProducers.get(event.getClass().getName());
			
			byte[] serializedEvent = SerializationUtils.serialize(event);
			
			ActiveMQBytesMessage message = new ActiveMQBytesMessage();
			message.writeBytes(serializedEvent);
			producer.send(message);
			
		} catch (JMSException e) {
			log.error("In sendEvent: " + e);
		} catch (Exception e) {
			log.error("In sendEvent: " + e);
		}
	}
    
    
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

    
    private <T extends BusinessEvent> void createClientProducerQueue(Class<T> clazz) throws JMSException {
		
		Destination queue = session.createQueue(clazz.getName() + SERVER_QUEUE_SUFFIX);
		MessageProducer producer = session.createProducer(queue);
		clientProducers.putIfAbsent(clazz.getName(), producer);
	}
    
    
	private <T extends BusinessEvent> void createClientReceiveQueue(Class<T> clazz) throws JMSException {
		
		Destination queue = session.createQueue(clazz.getName() + CLIENT_QUEUE_SUFFIX);
		MessageConsumer consumer = session.createConsumer(queue);
		consumer.setMessageListener(new JmsMessageListener<T>(clazz, this.eventListener));
		clientReceivers.putIfAbsent(clazz.getName(), queue);
	}
	
	private <T extends BusinessEvent> void createClientReceiveTemporaryQueue(Class<T> clazz) throws JMSException {
		
		Destination queue = session.createTemporaryQueue();
		MessageConsumer consumer = session.createConsumer(queue);
		consumer.setMessageListener(new JmsMessageListener<T>(clazz, this.eventListener));
		clientReceivers.putIfAbsent(clazz.getName(), queue);
	}

}
