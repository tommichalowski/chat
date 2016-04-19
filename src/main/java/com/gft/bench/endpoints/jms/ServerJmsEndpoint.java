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
import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.EnterToRoomRequest;
import com.gft.bench.events.EventType;
import com.gft.bench.events.MessageEvent;
import com.gft.bench.events.RequestResult;

/**
 * Created by tzms on 3/31/2016.
 */
public class ServerJmsEndpoint implements ServerEndpoint, JmsEndpoint, MessageListener {

    private static final Log log = LogFactory.getLog(ServerJmsEndpoint.class);
    protected final String brokerUrl;
    protected ActiveMQConnectionFactory connectionFactory;
    protected Connection connection;
    protected Session session;
    protected ChatEventListener messageListener;

    
    public ServerJmsEndpoint(String brokerUrl) throws JMSException {
        this.brokerUrl = brokerUrl;
        connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }


    @Override
    public void sendEvent(ChatEvent event) {

        if (event.getType() == EventType.ENTER_ROOM) {
            try {
                Destination destination = session.createQueue(EVENT_QUEUE_TO_CLIENT);
                MessageProducer producer = session.createProducer(destination);
                TextMessage textMsg = session.createTextMessage(((EnterToRoomRequest) event).getData());
                textMsg.setBooleanProperty(ENTER_ROOM_CONFIRMED, true);
                log.info("Server responds with message: \n" + textMsg.getText());
                producer.send(textMsg);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void listenForEvent() {
        try {
            Destination serverEventQueue = session.createQueue(EVENT_QUEUE_TO_SERVER);
            MessageConsumer eventConsumer = session.createConsumer(serverEventQueue);
            eventConsumer.setMessageListener(this);

            Destination serverMessageQueue = session.createQueue(MESSAGE_QUEUE_TO_SERVER);
            MessageConsumer messageConsumer = session.createConsumer(serverMessageQueue);
            messageConsumer.setMessageListener(this);

            log.info("Server is listening...");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message.getBooleanProperty(ENTER_ROOM_REQUEST)) {
                if (message instanceof TextMessage) {
                    TextMessage textMsg = (TextMessage) message;
                    EnterToRoomRequest event = new EnterToRoomRequest(EventType.ENTER_ROOM, textMsg.getText());
                    messageListener.eventReceived(event);
                }
            } else if (message.getBooleanProperty(MESSAGE_TO_SERVER)) {
                if (message instanceof TextMessage) {
                    TextMessage textMsg = (TextMessage) message;
                    MessageEvent event = new MessageEvent(EventType.MESSAGE, 
                    		RequestResult.SUCCESS, textMsg.getStringProperty(ROOM_NAME), textMsg.getText());
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

}
