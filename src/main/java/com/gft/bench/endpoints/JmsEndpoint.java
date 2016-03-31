package com.gft.bench.endpoints;

import javax.jms.*;

import com.gft.bench.events.EnterToRoomEvent;
import com.gft.bench.events.EventType;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.ChatEventListener;

/**
 * Created by tzms on 3/25/2016.
 */
public abstract class JmsEndpoint implements Endpoint, MessageListener {

    protected static final String EVENT_QUEUE_TO_SERVER = "EVENT.QUEUE.TO.SERVER";
    protected static final String EVENT_QUEUE_TO_CLIENT = "EVENT.QUEUE.TO.CLIENT";
    protected static final String ENTER_ROOM_REQUEST = "enter_room_request";
    protected static final String ENTER_ROOM_CONFIRMED = "enter_room_confirmed";

    protected final String brokerUrl;
    protected ActiveMQConnectionFactory connectionFactory;
    protected Connection connection;
    protected Session session;
    protected ChatEventListener messageListener;


    public JmsEndpoint(String brokerUrl) throws JMSException {
        this.brokerUrl = brokerUrl;
        connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }


    @Override
    public void setEventListener(ChatEventListener messageListener) {
        this.messageListener = messageListener;
    }
}
