package com.gft.bench.endpoints;

import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.EnterToRoomRequest;
import com.gft.bench.events.EventType;
import com.gft.bench.events.MessageEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;

/**
 * Created by tzms on 3/31/2016.
 */
public class ClientJmsEndpoint extends JmsEndpoint {

    private static final Log log = LogFactory.getLog(ClientJmsEndpoint.class);


    public ClientJmsEndpoint(String brokerUrl) throws JMSException {
        super(brokerUrl);
    }


    @Override
    public void sendEvent(ChatEvent event) {

        if (event.getType() == EventType.ENTER_ROOM) {
            try {
                Destination destination = session.createQueue(EVENT_QUEUE_TO_SERVER);
                MessageProducer producer = session.createProducer(destination);
                TextMessage textMsg = session.createTextMessage(((EnterToRoomRequest) event).getRoom());
                textMsg.setBooleanProperty(ENTER_ROOM_REQUEST, true);
                producer.send(textMsg);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        } else if (event.getType() == EventType.MESSAGE) {
            try {
                Destination destination = session.createQueue(MESSAGE_QUEUE_TO_SERVER);
                MessageProducer producer = session.createProducer(destination);
                TextMessage textMsg = session.createTextMessage(((MessageEvent) event).getData());
                textMsg.setStringProperty(ROOM_NAME, ((MessageEvent) event).getRoom());
                textMsg.setBooleanProperty(MESSAGE_TO_SERVER, true);
                log.info("Sending message from client, room: " + textMsg.getStringProperty(ROOM_NAME) + "; Data: " + textMsg.getText());
                producer.send(textMsg);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void listenForEvent() {
        try {
            Destination serverEventQueue = session.createQueue(EVENT_QUEUE_TO_CLIENT);
            MessageConsumer consumer = session.createConsumer(serverEventQueue);
            consumer.setMessageListener(this);  //receive();
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
                    TextMessage textMsg = (TextMessage) message;
                    EnterToRoomRequest event = new EnterToRoomRequest(EventType.ENTER_ROOM, textMsg.getText());
                    messageListener.eventReceived(event);
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
