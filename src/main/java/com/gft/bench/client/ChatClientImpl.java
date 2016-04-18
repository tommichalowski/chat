package com.gft.bench.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.ResultMsg;
import com.gft.bench.SendResult;
import com.gft.bench.endpoints.ClientEndpoint;
import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.EnterToRoomRequest;
import com.gft.bench.events.EventType;
import com.gft.bench.events.RequestResult;

/**
 * Created by tzms on 3/25/2016.
 */
public class ChatClientImpl implements ChatClient, ChatEventListener {

    private static final Log log = LogFactory.getLog(ChatClientImpl.class);
    
    private ClientEndpoint serverEndpoint;

    public ChatClientImpl(ClientEndpoint serverEndpoint) {
        this.serverEndpoint = serverEndpoint;
        serverEndpoint.setEventListener(this);
       // serverEndpoint.listenForEvent();
    }
    

    @Override
    public ResultMsg enterToRoomRequest(String room) {
    	
    	ChatEvent event = new EnterToRoomRequest(EventType.ENTER_ROOM, room);
    	Future<ResultMsg> future = serverEndpoint.request(event);

		ResultMsg resultMsg = null;
		try {
			resultMsg = future.get();
		} catch (InterruptedException | ExecutionException e) {
			log.error(e.getStackTrace());
			return new ResultMsg("Can NOT connect to room!\n", RequestResult.ERROR);
		}

		return resultMsg;
	}
	
	
    @Override
    public void enterToRoomWithoutConfirmation(String room){
        EnterToRoomRequest event = new EnterToRoomRequest(EventType.ENTER_ROOM, room);
        log.info("Enter to room from client: " + event.toString());
        serverEndpoint.sendEvent(event);
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
    public SendResult sendMessageToRoom(String room, String message) {

        //MessageEvent event = new MessageEvent(EventType.MESSAGE, room, message);
        //serverEndpoint.sendEvent(event);
        return null;
    }

    @Override
    public ResultMsg receiveMessage(String room) {
        return null;
    }

}
