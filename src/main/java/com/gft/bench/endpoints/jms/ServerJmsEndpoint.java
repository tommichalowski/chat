package com.gft.bench.endpoints.jms;

import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.RequestHandler;
import com.gft.bench.endpoints.ServerEndpoint;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.DataEvent;
import com.gft.bench.events.business.ChatMessageEvent;
import com.gft.bench.events.business.CreateUserEvent;
import com.gft.bench.events.business.RoomChangedEvent;
import com.gft.bench.events.listeners.jms.JmsMessageListener;
import com.gft.bench.exceptions.ChatException;

/**
 * Created by tzms on 3/31/2016.
 */
public class ServerJmsEndpoint implements ServerEndpoint, JmsEndpoint {

	private static final String CLIENT_QUEUE_SUFFIX = ".to.client";
	private static final String SERVER_QUEUE_SUFFIX = "to.server";
    private static final Log log = LogFactory.getLog(ServerJmsEndpoint.class);
    protected final String brokerUrl;
    protected ActiveMQConnectionFactory connectionFactory;
    protected Connection connection;
    protected Session session;
    MessageConsumer messageConsumer;
    protected ChatEventListener eventListener;
    private ConcurrentHashMap<String, Destination> serverReceivers = new ConcurrentHashMap<>();
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
	public <TRequest, TResponse> void registerListener(RequestHandler<TRequest, TResponse> handler) {
				/** TODO:
				 *  - create queue and consumer on TReqeust (if not exist) and put into collection where 
				 *  key is TRequest class and value is queue
				 *  - 
				 */
	}
    
	@Override
	public void setEventListeners(ChatEventListener eventListener) throws ChatException {
		
		this.eventListener = eventListener;
		
		try {
			createServerReceiveQueue(ChatMessageEvent.class);
	        createServerReceiveQueue(CreateUserEvent.class);
	        createServerReceiveQueue(RoomChangedEvent.class);
	
	        createServerProducerQueue(ChatMessageEvent.class);
	        createServerProducerQueue(CreateUserEvent.class);
	        createServerProducerQueue(RoomChangedEvent.class);
		} catch (JMSException e) {
			throw new ChatException("Server can NOT create JMS queues!", e);
		}
	}
	
    private <T> void createServerProducerQueue(Class<T> clazz) throws JMSException {
		
		Destination queue = session.createQueue(clazz.getName() + CLIENT_QUEUE_SUFFIX);
		MessageProducer producer = session.createProducer(queue);
		serverProducers.putIfAbsent(clazz.getName(), producer);
	}
    
    private <T> void createServerReceiveQueue(Class<T> clazz) throws JMSException {
		
		Destination queue = session.createQueue(clazz.getName() + SERVER_QUEUE_SUFFIX);
		MessageConsumer consumer = session.createConsumer(queue);
		consumer.setMessageListener(new JmsMessageListener<T>(clazz, this.eventListener));
		serverReceivers.putIfAbsent(clazz.getName(), queue);
	}
    

    @Override
    public void sendEvent(DataEvent event) {

        try {
            TextMessage textMsg = EventBuilderUtil.buildTextMessage(event);
            
            if (event.getType().isRequestResponse()) {
            	MessageProducer producer = session.createProducer(textMsg.getJMSReplyTo());
            	producer.send(textMsg.getJMSReplyTo(), textMsg);
            } else {
            	Destination destination = session.createQueue(MESSAGE_QUEUE_TO_CLIENT);
                MessageProducer producer = session.createProducer(destination);
            	producer.send(textMsg);
            }
        } catch (JMSException e) {
        	log.error("ERROR on server create user!!!");
            e.printStackTrace();
        }
    }

    
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
