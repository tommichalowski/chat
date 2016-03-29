package com.gft.bench;

import java.util.List;

import javax.jms.JMSException;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ChatClientNew {

    void connectToEndpoint(ChatEndpoint endpoint) throws JMSException;
    List<ResultMsg> enterToRoom(String room) throws JMSException;
    ResultMsg exitRoom(String room);
    SendResult sendMessageToRoom(String room);
    ResultMsg receiveMessage(String room);
}
