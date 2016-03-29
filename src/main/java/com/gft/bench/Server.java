package com.gft.bench;

import java.util.Set;

import javax.jms.JMSException;

public interface Server {

    void startServer() throws JMSException;

    void stopServer();

	Set<String> getRooms();

    void addRoom(String name);

    void setEndpoint(ChatEndpoint chatEndpoint);	
}
