package com.gft.bench.server;

import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.ServerEndpoint;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.DataEvent;
import com.gft.bench.events.EventListener;
import com.gft.bench.events.EventType;
import com.gft.bench.events.MessageEvent;
import com.gft.bench.events.RequestResult;
import com.gft.bench.exceptions.ChatException;



/**
 * Created by tzms on 3/25/2016.
 */
public class ServerImpl implements Server, ChatEventListener {
	
    private static final Log log = LogFactory.getLog(ServerImpl.class);
    private static final int ROOM_HISTORY_MAX_SIZE = 10;
    private ServerEndpoint chatEndpoint;
    @SuppressWarnings("rawtypes")
	private ConcurrentHashMap<Class, EventListener> eventListeners = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, LinkedList<String>> roomsHistory = new ConcurrentHashMap<String, LinkedList<String>>();
    private ConcurrentSkipListSet<String> usersLogins = new ConcurrentSkipListSet<>();


    public ServerImpl(ServerEndpoint chatEndpoint) throws ChatException {
        this.chatEndpoint = chatEndpoint;
        //this.chatEndpoint.setEventListeners(this); 
        
        //registerListener(CreateUserEvent.class, new CreateUserListener());
        //registerListener(RoomChangedEvent.class, new RoomChangedListener());
        //registerListener(ChatMessageEvent.class, new ChatMessageListener());
    }

    
    @Override
	public <T> void registerListener(Class<T> clazz, EventListener<T> listener) {
		eventListeners.put(clazz, listener);
	}
	
	@Override
	public <T> void notifyListeners(Class<T> clazz, T event) { //TODO: ??? make it static, then no need to set this class instance in endpoint ?
		
		@SuppressWarnings("unchecked")
		EventListener<T> eventListener = eventListeners.get(clazz);
		eventListener.onEvent(event);
	}
	

	@Override
	public <T> EventListener<T> getEventListener(Class<T> clazz) {
		
		@SuppressWarnings("unchecked")
		EventListener<T> eventListener = eventListeners.get(clazz);
		return eventListener;
	}
	

    //@Override
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
    		//chatEndpoint.sendEvent(messageEvent);
    		
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
           // chatEndpoint.sendEvent(messageEvent);

        } else if (event.getType() == EventType.MESSAGE) {
            MessageEvent messageEvent = (MessageEvent) event;
            LinkedList<String> roomHistory = getRoomHistory(messageEvent.getRoom());
            roomHistory.add(messageEvent.getData());
            log.info("Room history: " + roomHistory);
            
            //chatEndpoint.sendEvent(messageEvent);
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
    
    
    private void maintainRoomHistorySize(LinkedList<String> roomHistory) {
    	if (roomHistory != null && roomHistory.size() >= ROOM_HISTORY_MAX_SIZE) {
        	roomHistory.remove();
    	}
    }
    
    private String formatRoomHistory(LinkedList<String> roomHistory) {

    	StringBuffer history = new StringBuffer();
    	for (String str : roomHistory) {
    		history = history.append(str + "\n");
    	}
    	return history.toString();
    }

}
