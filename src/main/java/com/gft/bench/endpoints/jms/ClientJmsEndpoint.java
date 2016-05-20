package com.gft.bench.endpoints.jms;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.ClientEndpoint;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.DataEvent;
import com.gft.bench.exceptions.ChatException;

/**
 * Created by tzms on 3/31/2016.
 * @param <T>
 * @param <T>
 */
public class ClientJmsEndpoint implements ClientEndpoint, JmsEndpoint, MessageListener {

	private static final Log log = LogFactory.getLog(ClientJmsEndpoint.class);
    protected final String brokerUrl;
    private Connection connection;
    protected Session session;
    MessageProducer producer;
    private Destination clientMessageQueue;
    protected ChatEventListener messageListener;
    //private EventListener eventListener;

    
    public ClientJmsEndpoint(String brokerUrl) throws ChatException {
        
    	this.brokerUrl = brokerUrl;
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        try {
        	connection = connectionFactory.createConnection();
			connection.start();
	        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	        Destination destination = session.createQueue(MESSAGE_QUEUE_TO_SERVER);
            producer = session.createProducer(destination);
		} catch (JMSException e) {
			throw new ChatException("Can NOT create JMS connection!", e);
		}
    }

    
    @Override
    public void setEventListener(ChatEventListener messageListener) {
        this.messageListener = messageListener;
    }
    
//	@Override
//	public void setEventListener(EventListener listener) {
//		this.eventListener = listener;
//	}
    
    
    @Override
    public void onMessage(Message message) {
		try {
			DataEvent event = EventBuilderUtil.buildEvent(message);
			log.info("Client received event: " + event.getType() + "; UserName: " + event.getUserName()); 
			messageListener.asyncEventReceived(event);
			//eventListener.onEvent(event, listener);
		} catch (JMSException e) {
			log.error("\nOnMessage ERROR in client!\n\n\n");
			e.printStackTrace();
		}
    }
    
    
    @Override
    public void listenForEvent() {
        try {
            clientMessageQueue = session.createQueue(MESSAGE_QUEUE_TO_CLIENT);
            MessageConsumer consumer = session.createConsumer(clientMessageQueue);
            consumer.setMessageListener(this);
            log.info("Client is listening...");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    
    @Override
    public void sendEvent(DataEvent event) {
    	try {
    		TextMessage textMsg = EventBuilderUtil.buildTextMessage(event);
    		if (event.getType().isRequestResponse()) {
    			Destination tempDest = session.createTemporaryQueue();
    			MessageConsumer responseConsumer = session.createConsumer(tempDest);
    			responseConsumer.setMessageListener(this);
    			textMsg.setJMSReplyTo(tempDest);
    		}
            producer.send(textMsg);
        } catch (JMSException e) {
            e.printStackTrace();
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
    
//    @Override
//    public CompletableFuture<ChatEvent> receiveEvent(EventType eventType) throws RequestException {
//    	
//    	CompletableFuture<ChatEvent> result = new CompletableFuture<ChatEvent>();
//    	
//    	try {
//	    	if (eventType == EventType.ENTER_ROOM) {
//				Destination serverEventQueue = session.createQueue(EVENT_QUEUE_TO_CLIENT);
//			    MessageConsumer consumer = session.createConsumer(serverEventQueue);
//			    
//			    consumer.setMessageListener(m -> {
//			    	ChatEvent chatEvent = null;
//					try {
//						chatEvent = processMessage(m);
//						result.complete(chatEvent);
//					} catch (RequestException e) {
//						log.error("Reciving message on messageListener error: " + e);
//					}
//			    });
//			    
//			    return result;
//			}
//    	} catch (JMSException e) {
//			throw new RequestException(e);
//		}
//		
//    	throw new RequestException("Can NOT receive message for: " + eventType);
//    }
         
}
