package com.gft.bench.events.listeners.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.business.RoomChangedEvent;

public class JmsRoomChangedListener implements MessageListener {

	private static final Log log = LogFactory.getLog(JmsRoomChangedListener.class);
	private ChatEventListener chatEventListener;
	
	
	@Override
    public void onMessage(Message message) {

		try {
			if (message instanceof TextMessage) {
				TextMessage textMsg = (TextMessage) message;
				RoomChangedEvent event = new RoomChangedEvent();
				event.room = textMsg.getText();

				log.info("JmsRoomChangedListener, room: " + event.room);
				chatEventListener.notifyListeners(RoomChangedEvent.class, event);
			}
		} catch (JMSException e) {
			log.warn("JmsRoomChangedListener received message of NOT TextMessage type!");
		}
    }
}
