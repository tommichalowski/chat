package com.gft.bench.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.ClientEndpoint;
import com.gft.bench.endpoints.TransportLayer;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.DataEvent;
import com.gft.bench.events.EventType;
import com.gft.bench.events.MessageEvent;
import com.gft.bench.events.ResultMsg;
import com.gft.bench.exceptions.ChatException;

/**
 * Created by tzms on 3/25/2016.
 */
public class ChatClientImpl implements ChatClient, ChatEventListener {

    private static final Log log = LogFactory.getLog(ChatClientImpl.class);
    private static final String BROKER_URL = "tcp://localhost:61616";
    //private static final int TIMEOUT = 5; 
    
    private ClientEndpoint clientEndpoint;
    private ConcurrentHashMap<String, CompletableFuture<DataEvent>> futureMessageMap = new ConcurrentHashMap<>();
  
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
    public CompletableFuture<DataEvent> enterToRoom(String room) {
    	
//    	ChatEvent event = new EnterToRoomRequest(EventType.ENTER_ROOM, room);
//    	clientEndpoint.sendEvent(event);
    	
    	CompletableFuture<DataEvent> future = null;
//    	try {
//			future = clientEndpoint.receiveEvent(event.getType());		
//		} catch (RequestException e) {
//			log.error("Logging exception on enterToRoomRequest:", e);
//		}

		return future;
	}
    
    
	@Override
	public CompletableFuture<DataEvent> createUser(String userName) {
		
		DataEvent event = new MessageEvent(EventType.CREATE_USER, userName);
		CompletableFuture<DataEvent> future = clientEndpoint.request(event);
        futureMessageMap.put("MessageId", future);
        log.info("\n\nFuturesMap: " + futureMessageMap.toString() + "\n\n");
        return future;
	}
	
    
    @Override
    public CompletableFuture<DataEvent> sendMessageToRoom(String room, String message) {

        MessageEvent event = new MessageEvent(EventType.MESSAGE, message, room);
        CompletableFuture<DataEvent> future = clientEndpoint.request(event);
        futureMessageMap.put("MessageId", future);
        return future;
    }
	

    @Override
    public void eventReceived(DataEvent event) {
        log.info("Client received message: " + event.getData());
        log.info("Client received userName: " + event.getUserName());
        CompletableFuture<DataEvent> completableFuture = futureMessageMap.get("MessageId");
        if (completableFuture == null) {
        	log.info("future is null!!!!!!");
        } else {
        	log.info("Future not null :)");
        }
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
