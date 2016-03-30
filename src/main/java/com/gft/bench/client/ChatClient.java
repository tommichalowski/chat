package com.gft.bench.client;

import javax.jms.JMSException;

import com.gft.bench.ResultMsg;
import com.gft.bench.SendResult;
import com.gft.bench.endpoints.Endpoint;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ChatClient {

    void enterToRoom(String room) throws JMSException;
    
    ResultMsg exitRoom(String room);
    
    SendResult sendMessageToRoom(String room);
    
    ResultMsg receiveMessage(String room);
    
    void setEndpoint(Endpoint serverEndpoint);
}
