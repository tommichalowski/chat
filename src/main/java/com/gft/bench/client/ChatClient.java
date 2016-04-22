package com.gft.bench.client;

import javax.jms.JMSException;

import com.gft.bench.events.ResultMsg;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ChatClient {

	/** Sends request for joining to the specified room.
	 * If room already exists principal is joined to this room, else new room is created.  
     *  
     * <P>This call blocks until a result message is produced and returned 
     * or until the timeout occur.
 
     * @return ResultMsg object generated for this request. 
     * Result contains resulting status and message.
     */
	ResultMsg enterToRoom(String room);
	
	
	
    void enterToRoomWithoutConfirmation(String room) throws JMSException;
    
    ResultMsg exitRoom(String room);
    
    void sendMessageToRoom(String room, String message);
    
    ResultMsg receiveMessage(String room);
}
