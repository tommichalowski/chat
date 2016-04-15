package com.gft.bench.client;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.ResultMsg;
import com.gft.bench.ResultType;
import com.gft.bench.SendResult;
import com.gft.bench.endpoints.Endpoint;
import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.EnterToRoomRequest;
import com.gft.bench.events.EventType;

/**
 * Created by tzms on 3/25/2016.
 */
public class ChatClientImpl implements ChatClient, ChatEventListener {

    private static final Log log = LogFactory.getLog(ChatClientImpl.class);

    private Endpoint serverEndpoint;

    public ChatClientImpl(Endpoint serverEndpoint) {
        this.serverEndpoint = serverEndpoint;
        serverEndpoint.setEventListener(this);
        serverEndpoint.listenForEvent();
    }

    
	@Override
	public ResultMsg enterToRoomRequest(String room) {
		
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<Object> future = executorService.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				log.info("Asynchronous Callable");
				TimeUnit.SECONDS.sleep(5);
		        return "Callable Result";
			}
		});
		
		ResultMsg resultMsg = null;
		
		try {
			String result = (String) future.get();
			resultMsg = new ResultMsg(result, ResultType.NORMAL);
			//EnterToRoomRequest enterToRoomRequest = (EnterToRoomRequest) future.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
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
