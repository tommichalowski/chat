package com.gft.bench.server;

import java.util.LinkedList;
import java.util.Set;

import javax.jms.JMSException;

import com.gft.bench.events.ChatEventListener;

public interface Server extends ChatEventListener {

    public static final String NEW_ROOM_CREATED = "New room has been created: ";
    public static final String NEW_PERSON_JOINED = "New person has joined room: ";

    void startServer() throws JMSException;

    void stopServer();

    LinkedList<String> getRoomHistory(String room);

	Set<String> getRooms();

    void addRoom(String name);
}
