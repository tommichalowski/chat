package com.gft.bench.server;

import java.util.LinkedList;
import java.util.Set;

import com.gft.bench.events.ChatEventListener;
import com.gft.bench.exceptions.ChatException;

public interface Server extends ChatEventListener {

    public static final String NEW_ROOM_CREATED = "New room has been created: ";
    public static final String NEW_PERSON_JOINED = "New person has joined room: ";

    void startServer();

    void stopServer() throws ChatException;;

    LinkedList<String> getRoomHistory(String room);

	Set<String> getRooms();

    void addRoom(String name);
}
