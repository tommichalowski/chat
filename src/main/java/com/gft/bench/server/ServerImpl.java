package com.gft.bench.server;

import java.util.HashSet;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.Endpoint;
import com.gft.bench.events.ChatEventImpl;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.EventType;

/**
 * Created by tzms on 3/25/2016.
 */
public class ServerImpl implements Server, ChatEventListener {
	
    private static final Log log = LogFactory.getLog(ServerImpl.class);
    private static final String EVENT_QUEUE_TO_SERVER = "EVENT.QUEUE.TO.SERVER";
    private static final String HISTORY_QUEUE_TO_CLIENT = "HISTORY.QUEUE.TO.CLIENT";
    
    private Endpoint chatEndpoint;
    private Set<String> rooms = new HashSet<String>();
	
    
    @Override
    public void startServer() throws JMSException {   	
    	listenForEvents();
    }
    
	@Override
	public void listenForEvents() throws JMSException {		
		chatEndpoint.listenForEvent(this, EVENT_QUEUE_TO_SERVER);
	}
    
	@Override
	public void onMessage(Message message) {
		
		if (message instanceof TextMessage) {
    		TextMessage textMsg = (TextMessage) message;
        	
    		try {
				if (textMsg.getText().contains("Join me to room")) {
					String room = textMsg.getText().replace("Join me to room: ", "");
					addRoom(room);   //event.getData());
				  
					ChatEventImpl event = new ChatEventImpl(EventType.ENTER_ROOM, "You joined to room: " + room);
					chatEndpoint.sendEvent(event, HISTORY_QUEUE_TO_CLIENT);
				}
			} catch (JMSException e) {
				log.error(e.getMessage());
			}
    	}
	}
    
    @Override
    public void stopServer() {

    }

    @Override
    public Set<String> getRooms() {
        return rooms;
    }

    @Override
    public void addRoom(String name) {
        rooms.add(name);
    }

    @Override
    public void setEndpoint(Endpoint chatEndpoint) {
        this.chatEndpoint = chatEndpoint;
    }

}
