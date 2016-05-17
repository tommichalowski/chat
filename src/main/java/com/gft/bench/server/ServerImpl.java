package com.gft.bench.server;

import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.ServerEndpoint;
import com.gft.bench.events.DataEvent;
import com.gft.bench.events.EventType;
import com.gft.bench.events.MessageEvent;
import com.gft.bench.events.RequestResult;
import com.gft.bench.exceptions.ChatException;



/**
 * Created by tzms on 3/25/2016.
 */
public class ServerImpl implements Server {
	
    private static final Log log = LogFactory.getLog(ServerImpl.class);
    private static final int ROOM_HISTORY_MAX_SIZE = 10;
    private ServerEndpoint chatEndpoint;
    private ConcurrentHashMap<String, LinkedList<String>> roomsHistory = new ConcurrentHashMap<String, LinkedList<String>>();
    private ConcurrentSkipListSet<String> usersLogins = new ConcurrentSkipListSet<>();


    public ServerImpl(ServerEndpoint chatEndpoint) {
        this.chatEndpoint = chatEndpoint;
        chatEndpoint.setEventListener(this);
        chatEndpoint.listenForEvent();
    }


    @Override
    public void asyncEventReceived(DataEvent event) {

    	log.info("eventReceived thread: " + Thread.currentThread().getId());
    	
    	if (event.getType() == EventType.CREATE_USER) {
    		MessageEvent messageEvent = (MessageEvent) event;
    		log.info("Creating user: " + messageEvent.getUserName());
    		boolean addedNewUser = usersLogins.add(messageEvent.getUserName());
    		if (addedNewUser) {
    			messageEvent.setResult(RequestResult.SUCCESS);
    		} else {
    			messageEvent.setResult(RequestResult.ERROR);
    		}   		
    		chatEndpoint.sendEvent(messageEvent);
    		
    	} else if (event.getType() == EventType.ENTER_ROOM) {
    		MessageEvent messageEvent = (MessageEvent) event;
    		
    		synchronized (usersLogins) { //added due to check if userName exist when creating room
	    		if (usersLogins.contains(messageEvent.getUserName())) {
		            log.info("\nCreating room: " + messageEvent.getRoom() + ", by user: " + messageEvent.getUserName());
		            LinkedList<String> roomHistory = addRoom(messageEvent.getRoom(), messageEvent.getUserName());
		            messageEvent.setData(formatRoomHistory(roomHistory));
		            messageEvent.setResult(RequestResult.SUCCESS);
	    		} else {
	    			messageEvent.setResult(RequestResult.ERROR);
	    		}
			}
            chatEndpoint.sendEvent(messageEvent);

        } else if (event.getType() == EventType.MESSAGE) {
            MessageEvent messageEvent = (MessageEvent) event;
            LinkedList<String> roomHistory = getRoomHistory(messageEvent.getRoom());
            roomHistory.add(messageEvent.getData());
            log.info("Room history: " + roomHistory);
            
            chatEndpoint.sendEvent(messageEvent);
        }
    }
    
    
    @Override
    public LinkedList<String> getRoomHistory(String room) {
        return roomsHistory.get(room);
    }

    @Override
    public Set<String> getRooms() {
        return roomsHistory.keySet();
    }

    @Override
    public LinkedList<String> addRoom(String room, String userName) {
    	
    	//roomsHistory.putIfAbsent
    	synchronized (roomsHistory) {
    		LinkedList<String> roomHistory;
	        if (roomsHistory.containsKey(room)) {
	            roomHistory = roomsHistory.get(room);  
	            roomHistory.add(userName + NEW_PERSON_JOINED + room);
	            maintainRoomHistorySize(roomHistory);
	        } else {
	            roomHistory = new LinkedList<>();
	            roomHistory.add(userName + NEW_ROOM_CREATED + room);
	            roomsHistory.put(room, roomHistory);
	        }
	        
	        return roomHistory;
		}
    }
    
    
    @Override
    public void stopServer() throws ChatException { 
    	try {
    		log.info("Stopping server.");
			chatEndpoint.cleanup();
			log.info("Server stopped.");
		} catch (JMSException e) {
			throw new ChatException(e);
		}
    }
    
    
//    private boolean doesUserExists(String userName) {
//    	usersLogins.contains(userName);
//    }
    
    
    private void maintainRoomHistorySize(LinkedList<String> roomHistory) {
    	if (roomHistory != null && roomHistory.size() >= ROOM_HISTORY_MAX_SIZE) {
        	roomHistory.remove();
    	}
    }
    
    private String formatRoomHistory(LinkedList<String> roomHistory) {
    	//String.valueOf(roomHistory);
    	StringBuffer history = new StringBuffer();
    	for (String str : roomHistory) {
    		history = history.append(str + "\n");
    	}
    	return history.toString();
    }


	@Override
	public void messageReceived(DataEvent event) {
		// TODO Auto-generated method stub
	}

}
