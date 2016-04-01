package com.gft.bench.client;

import com.gft.bench.events.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.ResultMsg;
import com.gft.bench.SendResult;
import com.gft.bench.endpoints.Endpoint;

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
    public void enterToRoom(String room){
        EnterToRoomEvent event = new EnterToRoomEvent(EventType.ENTER_ROOM, room);
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

        MessageEvent event = new MessageEvent(EventType.MESSAGE, room, message);
        serverEndpoint.sendEvent(event);
        return null;
    }

    @Override
    public ResultMsg receiveMessage(String room) {
        return null;
    }

}
