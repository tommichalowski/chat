package com.gft.bench.server;

import java.util.LinkedList;
import java.util.Set;

import com.gft.bench.events.ChatEventListener;
import com.gft.bench.exceptions.ChatException;

public interface Server extends ChatEventListener {

    public static final String NEW_ROOM_CREATED = " has created new room: ";
    public static final String NEW_PERSON_JOINED = " has joined to room: ";

    void stopServer() throws ChatException;;

    LinkedList<String> getRoomHistory(String room);

	Set<String> getRooms();

	LinkedList<String> addRoom(String room, String userName);
}
