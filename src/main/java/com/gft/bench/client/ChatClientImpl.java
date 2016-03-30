package com.gft.bench.client;

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

import com.gft.bench.ResultMsg;
import com.gft.bench.SendResult;
import com.gft.bench.endpoints.Endpoint;

/**
 * Created by tzms on 3/25/2016.
 */
public class ChatClientImpl implements ChatClient, MessageListener {
	
	private static final Log log = LogFactory.getLog(ChatClientImpl.class);
	private static final String EVENT_QUEUE_TO_SERVER = "EVENT.QUEUE.TO.SERVER";
	private static final String HISTORY_QUEUE_TO_CLIENT = "HISTORY.QUEUE.TO.CLIENT";
	
	private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	
    @Override
    public void connectToEndpoint(Endpoint endpoint) throws JMSException {
    	connectionFactory = new ActiveMQConnectionFactory(endpoint.getEndpointUrl());
    	connection = connectionFactory.createConnection();
    	connection.start();
    	session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }
    
    @Override
    public void enterToRoom(String room) throws JMSException {

        Destination serverEventQueue = session.createQueue(EVENT_QUEUE_TO_SERVER);
        MessageProducer producer = session.createProducer(serverEventQueue);
        TextMessage enterRoomMsg = session.createTextMessage("Join me to room: " + room);
        producer.send(enterRoomMsg);   	
        
    	Destination clientHistoryQueue = session.createQueue(HISTORY_QUEUE_TO_CLIENT);
    	MessageConsumer consumer = session.createConsumer(clientHistoryQueue);
    	consumer.setMessageListener(this);
    }
    
	@Override
	public void onMessage(Message message) {

		if (message instanceof TextMessage) {
    		TextMessage textMsg = (TextMessage) message;
    		try {
				log.info("Client received msg: " + textMsg.getText());
			} catch (JMSException e) {
				log.error(e.getMessage());
			}
		}
	}

    @Override
    public ResultMsg exitRoom(String room) {
        return null;
    }

    @Override
    public SendResult sendMessageToRoom(String room) {
        return null;
    }

    @Override
    public ResultMsg receiveMessage(String room) {
        return null;
    }

}
