package com.gft.bench.camel;

import java.util.HashSet;
import java.util.Set;

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

import com.gft.bench.ChatEndpoint;
import com.gft.bench.Server;

/**
 * Created by tzms on 3/25/2016.
 */
public class CamelServer implements Server, MessageListener { //ChatEventListener {
	
    private static final Log log = LogFactory.getLog(CamelServer.class);
    private static final String EVENT_QUEUE_TO_SERVER = "EVENT.QUEUE.TO.SERVER";
    private static final String HISTORY_QUEUE_TO_CLIENT = "HISTORY.QUEUE.TO.CLIENT";
    private ChatEndpoint chatEndpoint;
    private ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Set<String> rooms = new HashSet<String>();
	
    
    @Override
    public void startServer() throws JMSException {
    	
    	connectionFactory = new ActiveMQConnectionFactory(chatEndpoint.getEndpointUrl());
    	connection = connectionFactory.createConnection();
    	connection.start();
    	session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    	
    	Destination serverEventQueue = session.createQueue(EVENT_QUEUE_TO_SERVER);
    	MessageConsumer consumer = session.createConsumer(serverEventQueue);
    	consumer.setMessageListener(this);
    	
//        try {
//            topicConnection = connectionFactory.createTopicConnection();
//            topicConnection.start();
//
//            topicConnection.setExceptionListener(exception -> log.error("Exception on listening"));
//        } catch (JMSException e) {
//            log.error(e.getStackTrace());
//        }
    }
    
    
	@Override
	public void onMessage(Message message) {
		
		if (message instanceof TextMessage) {
    		TextMessage textMsg = (TextMessage) message;
        	
    		try {
				if (textMsg.getText().contains("Join me to room")) {
					String room = textMsg.getText().replace("Join me to room: ", "");
					addRoom(room);   //event.getData());
				  
					Destination destination = session.createQueue(HISTORY_QUEUE_TO_CLIENT);
					MessageProducer producer = session.createProducer(destination);
				  
					TextMessage testMsg = session.createTextMessage("Hello World You joined to room: " + room);
					producer.send(testMsg);
					//log.info("Server - message send: " + testMsg.getText());
				}
			} catch (JMSException e) {
				log.error(e.getMessage());
			}
    	}
	}
    
//	@Override
//	public ChatEvent listenForEvent() throws JMSException {
//		
//		Destination serverEventQueue = session.createQueue(EVENT_QUEUE_TO_SERVER);
//    	MessageConsumer consumer = session.createConsumer(serverEventQueue);
//    	consumer.setMessageListener(listener);
//    	// Here we receive the message. By default this call is blocking, which means it will wait for a message to arrive on the queue.
//    	Message message = consumer.receive();
//    	
//    	ChatEvent event = null;
//    	if (message instanceof TextMessage) {
//    		TextMessage textMsg = (TextMessage) message;
//    		event = new ChatEventImpl(EventType.ENTER_ROOM, textMsg.getText());
//    	}
//    	
//		return event;
//	}
	
    
//    @Override
//    public void onEvent(ChatEvent event) throws JMSException {
//        
//    	log.info("Event received: " + event);
//        
//    	if (event.getType() == EventType.ENTER_ROOM) {
//            addRoom(event.getData());
//    		addRoom("movies");
//            
//            //QUEUE.TO.CLIENT
//           // Connection connection = connectionFactory.createConnection();
//            //connection.start();
//           // Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//            Destination destination = session.createQueue(HISTORY_QUEUE_TO_CLIENT);
//            MessageProducer producer = session.createProducer(destination);
//            
//            TextMessage message = session.createTextMessage("Hello World");
//            producer.send(message);
//            log.info("Server - message send: " + message.getText());
//        }
//    }
    
    @Override
    public void stopServer() {

    }

    @Override
    public Set<String> getRooms() {
        return rooms;
    }

    @Override
    public void addRoom(String name) {
        rooms.add(name);
    }

    @Override
    public void setEndpoint(ChatEndpoint chatEndpoint) {
        this.chatEndpoint = chatEndpoint;
    }


}
