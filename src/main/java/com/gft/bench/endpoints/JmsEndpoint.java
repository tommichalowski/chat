package com.gft.bench.endpoints;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.ChatEventListener;

/**
 * Created by tzms on 3/25/2016.
 */
public class JmsEndpoint implements Endpoint {

    private final String brokerUrl;
    private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	
    public JmsEndpoint(String brokerUrl) throws JMSException{
        this.brokerUrl = brokerUrl;
        connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
    	connection = connectionFactory.createConnection();
    	connection.start();
    	session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @Override
    public String getEndpointUrl() {
        return brokerUrl;
    }

	@Override
	public void listenForEvent(ChatEventListener listener, String eventName) throws JMSException {
		Destination serverEventQueue = session.createQueue(eventName);
    	MessageConsumer consumer = session.createConsumer(serverEventQueue);
    	consumer.setMessageListener(listener);
	}
	
    @Override
    public void sendEvent(ChatEvent event, String eventName) throws JMSException {
    	Destination destination = session.createQueue(eventName);
		MessageProducer producer = session.createProducer(destination);
		TextMessage testMsg = session.createTextMessage(event.getData());
		producer.send(testMsg);
    }

}
