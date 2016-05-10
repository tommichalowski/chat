package com.gft.bench.client;

import java.util.concurrent.CompletableFuture;

import com.gft.bench.events.DataEvent;
import com.gft.bench.events.ResultMsg;
import com.gft.bench.exceptions.ChatException;

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
	//ResultMsg enterToRoom(String room);
	
	CompletableFuture<DataEvent> createUser(String userName);
	
	CompletableFuture<DataEvent> enterToRoom(String userName, String room);
    
    //CompletableFuture<DataEvent> sendMessageToRoom(String room, String message);
    
    ResultMsg exitRoom(String room);
    
    void stopClient() throws ChatException;
}
