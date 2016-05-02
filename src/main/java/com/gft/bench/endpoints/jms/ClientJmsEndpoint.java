package com.gft.bench.endpoints.jms;

import java.util.concurrent.CompletableFuture;

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
import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.EnterToRoomRequest;
import com.gft.bench.events.EventType;
import com.gft.bench.events.MessageEvent;
import com.gft.bench.events.RequestResult;
import com.gft.bench.exceptions.ChatException;
import com.gft.bench.exceptions.RequestException;

/**
 * Created by tzms on 3/31/2016.
 * @param <T>
 * @param <T>
 */
public class ClientJmsEndpoint implements ClientEndpoint, JmsEndpoint, MessageListener {

	private static final Log log = LogFactory.getLog(ClientJmsEndpoint.class);
    protected final String brokerUrl;
    protected ActiveMQConnectionFactory connectionFactory;
    protected Connection connection;
    protected Session session;
    private Destination clientEventQueue;
    protected ChatEventListener messageListener;

    
    public ClientJmsEndpoint(String brokerUrl) throws ChatException {
        this.brokerUrl = brokerUrl;
        connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        try {
			connection = connectionFactory.createConnection();
			connection.start();
	        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			throw new ChatException("Can NOT create JMS connection!", e);
		}
    }

    
    @Override
    public CompletableFuture<ChatEvent> request(ChatEvent event) { 
        
    	CompletableFuture<ChatEvent> future = new CompletableFuture<ChatEvent>();
    	sendEvent(event);
    	return future;
    }

    
    @Override
    public CompletableFuture<ChatEvent> receiveEvent(EventType eventType) throws RequestException {
    	
    	CompletableFuture<ChatEvent> result = new CompletableFuture<ChatEvent>();
    	
    	try {
	    	if (eventType == EventType.ENTER_ROOM) {
				Destination serverEventQueue = session.createQueue(EVENT_QUEUE_TO_CLIENT);
			    MessageConsumer consumer = session.createConsumer(serverEventQueue);
			    
			    consumer.setMessageListener(m -> {
			    	ChatEvent chatEvent = null;
					try {
						chatEvent = processMessage(m);
						result.complete(chatEvent);
					} catch (RequestException e) {
						log.error("Reciving message on messageListener error: " + e);
					}
			    });
			    
			    return result;
			}
    	} catch (JMSException e) {
			throw new RequestException(e);
		}
		
    	throw new RequestException("Can NOT receive message for: " + eventType);
    }
    
    
    
    private ChatEvent processMessage(Message message) throws RequestException {
    	
    	ChatEvent resultMsg = null;
    	
    	try {
	    	if (message.getBooleanProperty(ENTER_ROOM_CONFIRMED)){
	            if (message instanceof TextMessage) {
	                TextMessage textMsg = (TextMessage) message;
	                resultMsg = new EnterToRoomRequest(EventType.ENTER_ROOM, "room", textMsg.getText(), RequestResult.SUCCESS);
	            }
	    	} else if (message.getBooleanProperty(MESSAGE_CONFIRMED)) {
	    		if (message instanceof TextMessage) {
	                TextMessage textMsg = (TextMessage) message;
	                resultMsg = new EnterToRoomRequest(EventType.MESSAGE, "room", textMsg.getText(), RequestResult.SUCCESS);
	            }
	    	}
    	} catch (JMSException e) {
			throw new RequestException(e);
		}
    	
    	return resultMsg;
    }
    

    @Override
    public void listenForEvent() {
        try {
            clientEventQueue = session.createQueue(MESSAGE_QUEUE_TO_CLIENT);
            MessageConsumer consumer = session.createConsumer(clientEventQueue);
            consumer.setMessageListener(this);
            log.info("Client is listening...");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message.getBooleanProperty(ENTER_ROOM_CONFIRMED)){
                if (message instanceof TextMessage) {
                    //TextMessage textMsg = (TextMessage) message;
                    //ResultMsg resultMsg = new ResultMsg(textMsg.getText(), RequestResult.SUCCESS);
                    //messageListener.eventReceived(event);
                }
            } else if (message.getBooleanProperty(MESSAGE_CONFIRMED)) {
            	if (message instanceof TextMessage) {
                    TextMessage textMsg = (TextMessage) message;
                    MessageEvent event = new MessageEvent(EventType.MESSAGE, textMsg.getText(), "room", RequestResult.SUCCESS);
                    messageListener.eventReceived(event);
            	}
            } else if (message.getBooleanProperty(CREATE_USER_CONFIRMED)) {
            	if (message instanceof TextMessage) {
                    TextMessage textMsg = (TextMessage) message;
                    MessageEvent event = new MessageEvent(EventType.CREATE_USER, textMsg.getText());
                    messageListener.eventReceived(event);
            	}
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    
    
    @Override
    public void setEventListener(ChatEventListener messageListener) {
        this.messageListener = messageListener;
    }
        
    
    @Override
    public void sendEvent(ChatEvent event) {

        if (event.getType() == EventType.CREATE_USER) {
        	try {
                Destination destination = session.createQueue(EVENT_QUEUE_TO_SERVER);
                MessageProducer producer = session.createProducer(destination);
                TextMessage textMsg = session.createTextMessage(((MessageEvent) event).getUserName());
                textMsg.setStringProperty(USER_NAME, ((MessageEvent) event).getUserName());
                textMsg.setBooleanProperty(CREATE_USER_REQUEST, true);
                textMsg.setJMSReplyTo(clientEventQueue);
                log.debug("Sending request to server \n");
                producer.send(textMsg);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        } else if (event.getType() == EventType.ENTER_ROOM) {
            try {
                Destination destination = session.createQueue(EVENT_QUEUE_TO_SERVER);
                MessageProducer producer = session.createProducer(destination);
                TextMessage textMsg = session.createTextMessage(((EnterToRoomRequest) event).getRoom());
                textMsg.setBooleanProperty(ENTER_ROOM_REQUEST, true);
                textMsg.setJMSReplyTo(clientEventQueue);
                log.debug("Sending request to server \n");
                producer.send(textMsg);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        } else if (event.getType() == EventType.MESSAGE) {
            try {
                Destination destination = session.createQueue(MESSAGE_QUEUE_TO_SERVER);
                MessageProducer producer = session.createProducer(destination);
                TextMessage textMsg = session.createTextMessage(((MessageEvent) event).getMessage());
                textMsg.setStringProperty(ROOM_NAME, ((MessageEvent) event).getRoom());
                textMsg.setBooleanProperty(MESSAGE_REQUEST, true);
                log.info("Sending message from client, room: " + textMsg.getStringProperty(ROOM_NAME) + "; Data: " + textMsg.getText());
                producer.send(textMsg);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
    
}
