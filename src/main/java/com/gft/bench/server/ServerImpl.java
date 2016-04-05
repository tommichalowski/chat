package com.gft.bench.server;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.Endpoint;
import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.EnterToRoomEvent;
import com.gft.bench.events.EventType;
import com.gft.bench.events.MessageEvent;

/**
 * Created by tzms on 3/25/2016.
 */
public class ServerImpl implements Server, ChatEventListener {
	
    private static final Log log = LogFactory.getLog(ServerImpl.class);

    private Endpoint chatEndpoint;
    private Map<String, LinkedList<String>> roomsHistory = new TreeMap<String, LinkedList<String>>();
//    private Map<String, ArrayList> roomsHistory = new TreeMap<String, ArrayList>();


    public ServerImpl(Endpoint chatEndpoint) {
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
            EnterToRoomEvent enterToRoomEvent = (EnterToRoomEvent) event;
            String room = enterToRoomEvent.getRoom();
            room = "failure test";
            addRoom(room);

            LinkedList<String> roomHistory = getRoomHistory(room);
            EnterToRoomEvent eventResponse = new EnterToRoomEvent(EventType.ENTER_ROOM, room, roomHistory.toString());
            chatEndpoint.sendEvent(eventResponse);

        } else if (event.getType() == EventType.MESSAGE) {
            MessageEvent messageEvent = (MessageEvent) event;
            LinkedList<String> roomHistory = getRoomHistory(messageEvent.getRoom());
            roomHistory.add(messageEvent.getData());
            log.info("Room history: " + roomHistory);
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
