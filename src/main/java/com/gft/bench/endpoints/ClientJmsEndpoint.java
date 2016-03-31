package com.gft.bench.endpoints;

import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.EnterToRoomEvent;
import com.gft.bench.events.EventType;

import javax.jms.*;

/**
 * Created by tzms on 3/31/2016.
 */
public class ClientJmsEndpoint extends JmsEndpoint {

    public ClientJmsEndpoint(String brokerUrl) throws JMSException {
        super(brokerUrl);
    }


    @Override
    public void sendEvent(ChatEvent event) {

        if (event.getType() == EventType.ENTER_ROOM) {
            try {
                Destination destination = session.createQueue("EVENT.QUEUE.TO.SERVER");
                MessageProducer producer = session.createProducer(destination);
                TextMessage textMsg = session.createTextMessage(((EnterToRoomEvent) event).getRoom());
                textMsg.setBooleanProperty("enter_to_room", true);
                producer.send(textMsg);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void listenForEvent() { //ChatEventListener listener) {
        try {
            Destination serverEventQueue = session.createQueue("EVENT.QUEUE.TO.CLIENT");
            MessageConsumer consumer = session.createConsumer(serverEventQueue);
            consumer.setMessageListener(this);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message.getBooleanProperty("client_entered_to_room")){
                if (message instanceof TextMessage) {
                    TextMessage textMsg = (TextMessage) message;
                    EnterToRoomEvent event = new EnterToRoomEvent(EventType.ENTER_ROOM, "You joined to room: " + textMsg.getText());
                    messageListener.eventReceived(event);
                }
            }

//            if (message instanceof TextMessage) {
//                TextMessage textMsg = (TextMessage) message;
//
//                try {
//                    if (textMsg.getText().contains("Join me to room")) {
//                        String room = textMsg.getText().replace("Join me to room: ", "");
//                        addRoom(room);   //event.getData());
//
//                        EnterToRoomEvent event = new EnterToRoomEvent(EventType.ENTER_ROOM, "You joined to room: " + room);
//                        chatEndpoint.sendEvent(event, HISTORY_QUEUE_TO_CLIENT);
//                    }
//                } catch (JMSException e) {
//                    log.error(e.getMessage());
//                }
//            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
