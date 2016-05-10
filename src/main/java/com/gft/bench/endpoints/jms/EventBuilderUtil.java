package com.gft.bench.endpoints.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQTextMessage;

import com.gft.bench.events.DataEvent;
import com.gft.bench.events.EventType;
import com.gft.bench.events.MessageEvent;
import com.gft.bench.events.RequestResult;

public class EventBuilderUtil implements JmsEndpoint {

	
    public static TextMessage buildTextMessage(DataEvent event) throws JMSException {
    	
    	TextMessage textMsg = new ActiveMQTextMessage();
    	textMsg.setText(event.getData());
        textMsg.setStringProperty(EVENT_TYPE, event.getType().toString());
        textMsg.setStringProperty(USER_NAME, event.getUserName());
        textMsg.setStringProperty(ROOM_NAME, event.getRoom());
        if (event.getResult() != null) {
        	textMsg.setStringProperty(REQUEST_RESULT, event.getResult().toString());
        }
        textMsg.setJMSCorrelationID(event.getEventId());
        
        if (event.getType().isRequestResponse()) {
        	textMsg.setJMSReplyTo(event.getReplyTo());
    	}
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
	    	if (textMsg.getStringProperty(REQUEST_RESULT) != null) {
	    		event.setResult( RequestResult.valueOf(textMsg.getStringProperty(REQUEST_RESULT)) );
	    	}
	    	event.setEventId(textMsg.getJMSCorrelationID());
	    	
	    	if (eventType.isRequestResponse()) {
	    		event.setReplyTo(textMsg.getJMSReplyTo());
	    	}
    	}
    	
    	return event;
    }
    
}
