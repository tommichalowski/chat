package com.gft.bench.server;

import java.util.*;

import javax.jms.JMSException;

import com.gft.bench.events.ChatEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.Endpoint;
import com.gft.bench.events.EnterToRoomEvent;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.EventType;

/**
 * Created by tzms on 3/25/2016.
 */
public class ServerImpl implements Server, ChatEventListener {
	
    private static final Log log = LogFactory.getLog(ServerImpl.class);

    private Endpoint chatEndpoint;
    private Set<String> rooms = new HashSet<String>();
    private Map<String, ArrayList> roomsHistory = new TreeMap<String, ArrayList>();


    public ServerImpl(Endpoint chatEndpoint) {
        this.chatEndpoint = chatEndpoint;
        chatEndpoint.setEventListener(this);
        chatEndpoint.listenForEvent();

        //temp
        addRoom("Movies");
        ArrayList<String> history = roomsHistory.get("Movies");
        history.add("Tom: This is first test message!");
        history.add("Jessica: Nice this one is second :)");
        history.add("Tom: fantastic !!!");
    }


    @Override
    public void eventReceived(ChatEvent event) {

        if (event.getType() == EventType.ENTER_ROOM) {
            EnterToRoomEvent enterToRoomEvent = (EnterToRoomEvent) event;
            String room = enterToRoomEvent.getRoom();
            addRoom(room);

            ArrayList<String> roomHistory = roomsHistory.get(room);
            StringBuilder history = new StringBuilder();
            for (String text : roomHistory) {
                history.append(text + "\n");
            }
            EnterToRoomEvent eventResponse = new EnterToRoomEvent(EventType.ENTER_ROOM, room, history.toString());
            chatEndpoint.sendEvent(eventResponse);
        }
    }
    
    @Override
    public void startServer() throws JMSException {  }

    @Override
    public void stopServer() {  }

    @Override
    public Map<String, ArrayList> getRoomsHistory() {
        return roomsHistory;
    }

    @Override
    public Set<String> getRooms() {
        return roomsHistory.keySet();
    }

    @Override
    public void addRoom(String name) {
        if (roomsHistory.containsKey(name) == false) {
            roomsHistory.put(name, new ArrayList<>());
        }
    }

}
