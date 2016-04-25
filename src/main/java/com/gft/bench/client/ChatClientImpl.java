package com.gft.bench.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.ClientEndpoint;
import com.gft.bench.endpoints.TransportLayer;
import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.EnterToRoomRequest;
import com.gft.bench.events.EventType;
import com.gft.bench.events.RequestResult;
import com.gft.bench.events.ResultMsg;
import com.gft.bench.exceptions.ChatException;

/**
 * Created by tzms on 3/25/2016.
 */
public class ChatClientImpl implements ChatClient, ChatEventListener {

    private static final Log log = LogFactory.getLog(ChatClientImpl.class);
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final int TIMEOUT = 5; 
    
    private ClientEndpoint clientEndpoint;
  
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
	    //clientEndpoint.listenForEvent(); 
    }
    

    @Override
    public CompletableFuture<ResultMsg> enterToRoom(String room) {
    	
    	ChatEvent event = new EnterToRoomRequest(EventType.ENTER_ROOM, room);
    	CompletableFuture<ResultMsg> future = clientEndpoint.request(event);
    	
//    	future.thenApplyAsync(rm -> {
//    		log.debug("Enter to room answer received: " + rm.getMessage());
//    		return rm.getMessage();
//    	});
	
    	future.thenAccept(result -> log.debug("Result: " + result.getMessage()));
	
    	//if (future.isDone()) {
    	
//		ResultMsg resultMsg = null;
//		try {
//			resultMsg = future.get(TIMEOUT, TimeUnit.SECONDS);
//		} catch (InterruptedException | ExecutionException | TimeoutException e) {
//			log.error("Logging exception on enterToRoomRequest: \n" + e.getStackTrace());
//			future.cancel(true);
//			return new ResultMsg("Can NOT connect to room!\n", RequestResult.ERROR);
//		}

		return future;
	}
	
	
    @Override
    public void enterToRoomWithoutConfirmation(String room){
        EnterToRoomRequest event = new EnterToRoomRequest(EventType.ENTER_ROOM, room);
        log.info("Enter to room from client: " + event.toString());
        clientEndpoint.sendEvent(event);
    }

    @Override
    public void eventReceived(ChatEvent event) {
        log.info("Client reveived message: \n" + event);
//        try {
//			responses.put((T) event);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
    }
       

    @Override
    public ResultMsg exitRoom(String room) {
        return null;
    }

    @Override
    public void sendMessageToRoom(String room, String message) {

        //MessageEvent event = new MessageEvent(EventType.MESSAGE, room, message);
        //clientEndpoint.sendEvent(event);
    }

    @Override
    public ResultMsg receiveMessage(String room) {
        return null;
    }

}
