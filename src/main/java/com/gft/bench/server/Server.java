package com.gft.bench.server;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import com.gft.bench.endpoints.RequestHandler;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.exceptions.ChatException;

public interface Server extends ChatEventListener {

    public static final String NEW_ROOM_CREATED = " has created new room: ";
    public static final String NEW_PERSON_JOINED = " has joined to room: ";

    <TRequest extends Serializable, TResponse extends Serializable> void registerRequestResponseListener(
    		Class<TRequest> tRequest, Class<TResponse> tResponse, RequestHandler<TRequest, TResponse> handler);
    
    ConcurrentSkipListSet<String> getUsersLogins();
    
    LinkedList<String> getRoomHistory(String room);

	Set<String> getRooms();

	LinkedList<String> addRoom(String room, String userName);
	
	String formatRoomHistory(LinkedList<String> roomHistory);
	
	void stopServer() throws ChatException;
}

