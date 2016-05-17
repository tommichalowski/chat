package com.gft.bench.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.JMSException;

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
    private ConcurrentHashMap<String, CompletableFuture<DataEvent>> futureMessageMap = new ConcurrentHashMap<String, CompletableFuture<DataEvent>>();
  
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
	public CompletableFuture<DataEvent> createUser(String userName) {
		
		DataEvent event = new MessageEvent(EventType.CREATE_USER, userName);
		CompletableFuture<DataEvent> future = requestAsync(event);
    	return future;
	}
	
	
    @Override
    public CompletableFuture<DataEvent> enterToRoom(String userName, String room) {
    	
    	DataEvent event = new MessageEvent(EventType.ENTER_ROOM, userName, room);
    	CompletableFuture<DataEvent> future = requestAsync(event);
    	return future;
	}
	
    
    @Override
    public void sendMessageToRoom(String userName, String room, String message) {
       
    	MessageEvent event = new MessageEvent(EventType.MESSAGE, userName, room, message);
        clientEndpoint.sendEvent(event);
    }
    
    
    private CompletableFuture<DataEvent> requestAsync(DataEvent event) {
    	
    	CompletableFuture<DataEvent> future = new CompletableFuture<DataEvent>();
		futureMessageMap.put(event.getEventId(), future);
		log.info("Putting new future for event id: " + event.getEventId());
		clientEndpoint.sendEvent(event);
		return future;
    }
    

    @Override
    public void asyncEventReceived(DataEvent event) {
    	
    	for (String eventId : futureMessageMap.keySet()) {
    		log.info("Future map keys: " + eventId);
    	}
    	
    	if (event != null && event.getEventId() != null) {
	        CompletableFuture<DataEvent> completableFuture = futureMessageMap.remove(event.getEventId());
	        if (completableFuture != null) {
		        completableFuture.complete(event);
	        }
    	}
    }
    
    
    @Override
    public void messageReceived(DataEvent event) {
    	
    }
       

    @Override
    public ResultMsg exitRoom(String room) {
        return null;
    }
    
    
    @Override
    public void stopClient() throws ChatException { 
    	try {
			clientEndpoint.cleanup();
		} catch (JMSException e) {
			throw new ChatException(e);
		}
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
