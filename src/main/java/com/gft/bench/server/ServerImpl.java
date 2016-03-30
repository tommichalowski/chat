package com.gft.bench.server;

import java.util.HashSet;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.Endpoint;
import com.gft.bench.events.ChatEventListener;

/**
 * Created by tzms on 3/25/2016.
 */
public class ServerImpl implements Server, ChatEventListener { //MessageListener {
	
    private static final Log log = LogFactory.getLog(ServerImpl.class);
    private static final String EVENT_QUEUE_TO_SERVER = "EVENT.QUEUE.TO.SERVER";
    private static final String HISTORY_QUEUE_TO_CLIENT = "HISTORY.QUEUE.TO.CLIENT";
    private Endpoint chatEndpoint;
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
    	
    	listenForEvent();
        //connection.setExceptionListener(exception -> log.error("Exception on listening"));
    }
    
	@Override
	public void listenForEvent() throws JMSException {
		
		Destination serverEventQueue = session.createQueue(EVENT_QUEUE_TO_SERVER);
    	MessageConsumer consumer = session.createConsumer(serverEventQueue);
    	consumer.setMessageListener(this);
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
				  
					TextMessage testMsg = session.createTextMessage("You joined to room: " + room);
					producer.send(testMsg);
					//log.info("Server - message send: " + testMsg.getText());
				}
			} catch (JMSException e) {
				log.error(e.getMessage());
			}
    	}
	}
    
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
    public void setEndpoint(Endpoint chatEndpoint) {
        this.chatEndpoint = chatEndpoint;
    }

}
