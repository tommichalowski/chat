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

import com.gft.bench.endpoints.ServerEndpoint;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.DataEvent;

/**
 * Created by tzms on 3/31/2016.
 */
public class ServerJmsEndpoint implements ServerEndpoint, JmsEndpoint, MessageListener {

    private static final Log log = LogFactory.getLog(ServerJmsEndpoint.class);
    protected final String brokerUrl;
    protected ActiveMQConnectionFactory connectionFactory;
    protected Connection connection;
    protected Session session;
    MessageConsumer messageConsumer;
    protected ChatEventListener messageListener;

    
    public ServerJmsEndpoint(String brokerUrl) throws JMSException {
        this.brokerUrl = brokerUrl;
        connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
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
            //producer.close();
        } catch (JMSException e) {
        	log.error("ERROR on server create user!!!");
            e.printStackTrace();
        }
    }

    
    @Override
    public void listenForEvent() {
        try {
            Destination serverMessageQueue = session.createQueue(MESSAGE_QUEUE_TO_SERVER);
            messageConsumer = session.createConsumer(serverMessageQueue);
            messageConsumer.setMessageListener(this);
            
            log.info("Server is listening...");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
    		DataEvent event = EventBuilderUtil.buildEvent(message);
    		messageListener.eventReceived(event);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    

    @Override
    public void setEventListener(ChatEventListener messageListener) {
        this.messageListener = messageListener;
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
