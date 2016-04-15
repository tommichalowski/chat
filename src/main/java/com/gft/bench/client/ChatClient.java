package com.gft.bench.client;

import javax.jms.JMSException;

import com.gft.bench.ResultMsg;
import com.gft.bench.SendResult;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ChatClient {

	ResultMsg enterToRoomRequest(String room);
	
    void enterToRoomWithoutConfirmation(String room) throws JMSException;
    
    ResultMsg exitRoom(String room);
    
    SendResult sendMessageToRoom(String room, String message);
    
    ResultMsg receiveMessage(String room);
}
