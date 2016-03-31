package com.gft.bench.server;

import java.util.*;

import javax.jms.JMSException;

import com.gft.bench.endpoints.Endpoint;

public interface Server {

    void startServer() throws JMSException;

    void stopServer();

    Map<String, LinkedList> getRoomsHistory();

	Set<String> getRooms();

    void addRoom(String name);

//    void setEndpoint(Endpoint chatEndpoint);
}
