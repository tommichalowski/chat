package com.gft.bench.endpoints;

import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.EnterToRoomEvent;
import com.gft.bench.events.EventType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;

/**
 * Created by tzms on 3/31/2016.
 */
public class ServerJmsEndpoint extends JmsEndpoint {

    private static final Log log = LogFactory.getLog(ServerJmsEndpoint.class);


    public ServerJmsEndpoint(String brokerUrl) throws JMSException {
        super(brokerUrl);
    }


    @Override
    public void sendEvent(ChatEvent event) {

        if (event.getType() == EventType.ENTER_ROOM) {
            try {
                Destination destination = session.createQueue(EVENT_QUEUE_TO_CLIENT);
                MessageProducer producer = session.createProducer(destination);
                TextMessage textMsg = session.createTextMessage(((EnterToRoomEvent) event).getData());
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
            MessageConsumer consumer = session.createConsumer(serverEventQueue);
            consumer.setMessageListener(this);
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
                    EnterToRoomEvent event = new EnterToRoomEvent(EventType.ENTER_ROOM, textMsg.getText());
                    messageListener.eventReceived(event);
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
