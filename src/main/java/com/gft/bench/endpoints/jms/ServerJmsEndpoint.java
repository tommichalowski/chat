package com.gft.bench.endpoints.jms;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.DestinationType;
import com.gft.bench.endpoints.NotificationHandler;
import com.gft.bench.endpoints.RequestHandler;
import com.gft.bench.endpoints.ServerEndpoint;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.exceptions.ChatException;

/**
 * Created by tzms on 3/31/2016.
 */
public class ServerJmsEndpoint implements ServerEndpoint {

    private static final Log log = LogFactory.getLog(ServerJmsEndpoint.class);
    protected final String brokerUrl;
    protected ActiveMQConnectionFactory connectionFactory;
    protected Connection connection;
    protected Session session;
    MessageConsumer messageConsumer;
    protected ChatEventListener eventListener;
    private ConcurrentHashMap<String, MessageConsumer> serverReceivers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, MessageProducer> serverProducers = new ConcurrentHashMap<>();
	@SuppressWarnings("rawtypes")
	private ConcurrentHashMap<Class, MessageListener> messageListeners = new ConcurrentHashMap<>();
	@SuppressWarnings("rawtypes")
	private ConcurrentHashMap<Class, CopyOnWriteArrayList<NotificationHandler<?>>> notificationHandlers = new ConcurrentHashMap<>();
	@SuppressWarnings("rawtypes")
	private ConcurrentHashMap<Class, CopyOnWriteArrayList<RequestHandler<?, ?>>> requestHandlers = new ConcurrentHashMap<>();
    
	
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
	public <TRequest extends Serializable, TResponse extends Serializable> void registerRequestResponseListener(
			Class<TRequest> tRequest, Class<TResponse> tResponse, RequestHandler<TRequest, TResponse> handler) {

		log.info("\n\n" + DestinationType.SERVER + "registerRequestResponseListener method, clazz name: " + tRequest.getName());
		try {	
			JmsEndpointUtils.registerRequestResponseHandler(tRequest, handler, requestHandlers);
			JmsEndpointUtils.defineRequestReciverListener(tRequest, tResponse, messageListeners, requestHandlers, session);
			JmsEndpointUtils.getMessageReceiver(tRequest, serverReceivers, messageListeners, session, DestinationType.SERVER); 
		} catch (JMSException e) {
			log.error("Server registerNotificationListener Message listener lambda error", e);
			e.printStackTrace();
		}
	}
    
    @Override
	public <T extends Serializable> void registerNotificationListener(Class<T> clazz, NotificationHandler<T> handler) {

		log.info("\n\n" + DestinationType.SERVER + " registerNotificationListener method, clazz name: " + clazz.getName());
		try {		
			JmsEndpointUtils.registerNotificationHandler(clazz, handler, notificationHandlers);
			JmsEndpointUtils.defineNotificationListener(clazz, messageListeners, notificationHandlers);
			JmsEndpointUtils.getMessageReceiver(clazz, serverReceivers, messageListeners, session, DestinationType.SERVER); 
		} catch (JMSException e) {
			log.error("Server registerNotificationListener Message listener lambda error", e);
			e.printStackTrace();
		}
	}

    	
    @Override
	public <T extends Serializable> void sendNotification(T request) {
		
		try {
			Message message = MessageBuilderUtil.buildMessage(request);		
			MessageProducer producer = JmsEndpointUtils.getMessageProducer(request.getClass(), serverProducers, session, DestinationType.CLIENT); 
			producer.send(message); 
		} catch (JMSException e) {
			log.error("Server request method ERROR", e);
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
