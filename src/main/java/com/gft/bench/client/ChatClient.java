package com.gft.bench.client;

import java.util.concurrent.CompletableFuture;

import com.gft.bench.events.business.BusinessEvent;
import com.gft.bench.exceptions.ChatException;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ChatClient {
	
	CompletableFuture<BusinessEvent> createUser(String userName);
	
	/** Sends request for joining to the specified room.
	 * If room already exists principal is joined to this room, else new room is created.  
     *  
     * @return CompletableFuture<BusinessEvent> object generated for this request. 
     */
	CompletableFuture<BusinessEvent> enterToRoom(String userName, String room);
    
	public void asyncEventReceived(BusinessEvent event);
	
	void sendMessageToRoom(String userName, String room, String message);
    
    void exitRoom(String room);
    
    void stopClient() throws ChatException;
}
