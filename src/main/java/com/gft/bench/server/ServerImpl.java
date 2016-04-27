package com.gft.bench.server;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.ServerEndpoint;
import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.EnterToRoomRequest;
import com.gft.bench.events.EventType;
import com.gft.bench.events.MessageEvent;
import com.gft.bench.events.RequestResult;



/**
 * Created by tzms on 3/25/2016.
 */
public class ServerImpl implements Server {
	
    private static final Log log = LogFactory.getLog(ServerImpl.class);

    private ServerEndpoint chatEndpoint;
    private Map<String, LinkedList<String>> roomsHistory = new TreeMap<String, LinkedList<String>>();
//    private Map<String, ArrayList> roomsHistory = new TreeMap<String, ArrayList>();


    public ServerImpl(ServerEndpoint chatEndpoint) {
        this.chatEndpoint = chatEndpoint;
        chatEndpoint.setEventListener(this);
        chatEndpoint.listenForEvent();

        //temp
        //addRoom("Movies");
        //LinkedList<String> history = roomsHistory.get("Movies");
        //history.add("Tom: This is first test message!");
        //history.add("Jessica: Nice this one is second :)");
        //history.add("Tom: fantastic !!!");
        //history.pollFirst();
    }


    @Override
    public void eventReceived(ChatEvent event) {

    	System.out.println("eventReceived thread: " + Thread.currentThread().getId());
    	
        if (event.getType() == EventType.ENTER_ROOM) {
            EnterToRoomRequest enterToRoomEvent = (EnterToRoomRequest) event;
            String room = enterToRoomEvent.getRoom();
            addRoom(room);

            LinkedList<String> roomHistory = getRoomHistory(room);
            EnterToRoomRequest eventResponse = new EnterToRoomRequest(EventType.ENTER_ROOM, room, roomHistory.toString(),
            		RequestResult.SUCCESS);
            chatEndpoint.sendEvent(eventResponse);

        } else if (event.getType() == EventType.MESSAGE) {
            MessageEvent messageEvent = (MessageEvent) event;
            LinkedList<String> roomHistory = getRoomHistory(messageEvent.getRoom());
            roomHistory.add(messageEvent.getMessage());
            log.info("Room history: " + roomHistory);
            
            chatEndpoint.sendEvent(messageEvent);
        }
    }
    
    @Override
    public void startServer() throws JMSException {  }

    @Override
    public void stopServer() {  }

    @Override
    public LinkedList<String> getRoomHistory(String room) {
        return roomsHistory.get(room);
    }

    @Override
    public Set<String> getRooms() {
        return roomsHistory.keySet();
    }

    @Override
    public void addRoom(String name) {
    	
    	try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
        if (roomsHistory.containsKey(name)) {
            LinkedList<String> roomHistory = roomsHistory.get(name);
            roomHistory.add(NEW_PERSON_JOINED + name);
        } else {
            LinkedList<String> roomHistory = new LinkedList<>();
            roomHistory.add(NEW_ROOM_CREATED + name);
            roomsHistory.put(name, roomHistory);
        }
    }

}
