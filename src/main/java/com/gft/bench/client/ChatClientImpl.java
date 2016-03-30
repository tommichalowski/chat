package com.gft.bench.client;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.ResultMsg;
import com.gft.bench.SendResult;
import com.gft.bench.endpoints.Endpoint;
import com.gft.bench.events.ChatEventImpl;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.EventType;

/**
 * Created by tzms on 3/25/2016.
 */
public class ChatClientImpl implements ChatClient, ChatEventListener {
	
	private static final Log log = LogFactory.getLog(ChatClientImpl.class);
	private static final String EVENT_QUEUE_TO_SERVER = "EVENT.QUEUE.TO.SERVER";
	private static final String HISTORY_QUEUE_TO_CLIENT = "HISTORY.QUEUE.TO.CLIENT";
	
	private Endpoint serverEndpoint;

    
    @Override
    public void enterToRoom(String room) throws JMSException {

    	ChatEventImpl event = new ChatEventImpl(EventType.ENTER_ROOM, "Join me to room: " + room);
    	serverEndpoint.sendEvent(event, EVENT_QUEUE_TO_SERVER);
    	listenForEvents();
    }
    
	@Override
	public void listenForEvents() throws JMSException {
		serverEndpoint.listenForEvent(this, HISTORY_QUEUE_TO_CLIENT);
	}
	
	@Override
	public void onMessage(Message message) {

		if (message instanceof TextMessage) {
    		TextMessage textMsg = (TextMessage) message;
    		try {
				log.info("Client received msg: " + textMsg.getText());
			} catch (JMSException e) {
				log.error(e.getMessage());
			}
		}
	}

    @Override
    public ResultMsg exitRoom(String room) {
        return null;
    }

    @Override
    public SendResult sendMessageToRoom(String room) {
        return null;
    }

    @Override
    public ResultMsg receiveMessage(String room) {
        return null;
    }

	@Override
	public void setEndpoint(Endpoint serverEndpoint) {
		this.serverEndpoint = serverEndpoint;
	}
	
}
