package com.gft.bench.endpoints.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQTextMessage;

import com.gft.bench.events.DataEvent;
import com.gft.bench.events.EventType;
import com.gft.bench.events.MessageEvent;

public class EventBuilderUtil implements JmsEndpoint {

	
    public static TextMessage buildTextMessage(DataEvent event) throws JMSException {
    	
        //TextMessage textMsg = session.createTextMessage(event.getData());
    	TextMessage textMsg = new ActiveMQTextMessage();
    	textMsg.setText(event.getData());
        textMsg.setStringProperty(EVENT_TYPE, event.getType().toString());
        textMsg.setStringProperty(USER_NAME, event.getUserName());
        textMsg.setStringProperty(ROOM_NAME, event.getRoom());
        //textMsg.setJMSReplyTo(event.getReplyTo());
        return textMsg;
    }
    
    
    public static DataEvent buildEvent(Message message) throws JMSException {
    	
    	MessageEvent event = null;
    	
    	if (message instanceof TextMessage) {
	    	TextMessage textMsg = (TextMessage) message;
	    	EventType eventType = EventType.valueOf(textMsg.getStringProperty(EVENT_TYPE));
	    	event = new MessageEvent(eventType);
	    	event.setData(textMsg.getText());
	    	event.setUserName(textMsg.getStringProperty(USER_NAME));
	    	event.setRoom(textMsg.getStringProperty(ROOM_NAME));
	    	//event.setReplyTo(textMsg.getJMSReplyTo());
    	}
    	
    	return event;
    }
	
//    public static DataEvent buildEvent(EventType eventType, TextMessage textMsg) throws JMSException {
//    	
//    	DataEvent event = null;
//    	
//    	switch (eventType) {
//		case CREATE_USER: event = new MessageEvent(EventType.CREATE_USER, textMsg.getStringProperty(USER_NAME));
//						  event.setReplyTo(textMsg.getJMSReplyTo());
//						  break;
//		case ENTER_ROOM: event = new EnterToRoomRequest(EventType.ENTER_ROOM, textMsg.getText());
//						 break;
//		case EXIT_ROOM: break;
//		case MESSAGE: event = new MessageEvent(EventType.MESSAGE, textMsg.getStringProperty(ROOM_NAME));
//					  break;
//		default:
//			throw new RequestException("Not supported event type: " + eventType);
//		}
//    	
//    	return event;
//    }
    
}
