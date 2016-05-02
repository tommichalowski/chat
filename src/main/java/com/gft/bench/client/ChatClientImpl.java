package com.gft.bench.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.ClientEndpoint;
import com.gft.bench.endpoints.TransportLayer;
import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.EnterToRoomRequest;
import com.gft.bench.events.EventType;
import com.gft.bench.events.MessageEvent;
import com.gft.bench.events.ResultMsg;
import com.gft.bench.exceptions.ChatException;
import com.gft.bench.exceptions.RequestException;

/**
 * Created by tzms on 3/25/2016.
 */
public class ChatClientImpl implements ChatClient, ChatEventListener {

    private static final Log log = LogFactory.getLog(ChatClientImpl.class);
    private static final String BROKER_URL = "tcp://localhost:61616";
    //private static final int TIMEOUT = 5; 
    
    private ClientEndpoint clientEndpoint;
    private ConcurrentHashMap<String, CompletableFuture<ChatEvent>> futureMessageMap = new ConcurrentHashMap<>();
  
    /**
     * Constructs a new chat client object with default JMS broker.
     * 
     * @throws ChatException if it fails to create chat client on default broker 
     * due to some internal error. 
     */
    public ChatClientImpl() throws ChatException {
    	this(ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL));
    }
    
    /**
     * Constructs a new chat client object with specified ClientEdnpoint.
     * 
     * @param   endpoint   the client endpoint. It can be created using client endpoint factory.
     * It allow to specify transport layer and target URL.
     */
    public ChatClientImpl(ClientEndpoint endpoint) {
		this.clientEndpoint = endpoint; 
		clientEndpoint.setEventListener(this);
	    clientEndpoint.listenForEvent(); 
    }


    @Override
    public CompletableFuture<ChatEvent> enterToRoom(String room) {
    	
    	ChatEvent event = new EnterToRoomRequest(EventType.ENTER_ROOM, room);
    	clientEndpoint.sendEvent(event);
    	
    	CompletableFuture<ChatEvent> future = null;
    	try {
			future = clientEndpoint.receiveEvent(event.getType());		
		} catch (RequestException e) {
			log.error("Logging exception on enterToRoomRequest:", e);
		}

		return future;
	}
    
    
	@Override
	public CompletableFuture<ChatEvent> createUser(String userName) {
		
		ChatEvent event = new MessageEvent(EventType.CREATE_USER, userName);
		CompletableFuture<ChatEvent> future = clientEndpoint.request(event);
        futureMessageMap.put("MessageId", future);
        return future;
	}
	
    
    @Override
    public CompletableFuture<ChatEvent> sendMessageToRoom(String room, String message) {

        MessageEvent event = new MessageEvent(EventType.MESSAGE, message, room);
        CompletableFuture<ChatEvent> future = clientEndpoint.request(event);
        futureMessageMap.put("MessageId", future);
        return future;
    }
	

    @Override
    public void eventReceived(ChatEvent event) {
        log.info("Client reveived message: " + event.getMessage());
        log.info("Client reveived userName: " + event.getUserName());
        CompletableFuture<ChatEvent> completableFuture = futureMessageMap.get("MessageId");
        completableFuture.complete(event);
    }
       

    @Override
    public ResultMsg exitRoom(String room) {
        return null;
    }
   
    
//	private CompletableFuture<ChatEvent> requestResponse(ChatEvent event) {
//	
//	clientEndpoint.sendEvent(event);
//	
//	CompletableFuture<ChatEvent> future = null;
//	try {
//		future = clientEndpoint.receiveEvent(event.getType());		
//	} catch (RequestException e) {
//		log.error("Logging exception on enterToRoomRequest:", e);
//	}
//
//	return future;
//}

}
