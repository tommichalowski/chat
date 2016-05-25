package com.gft.bench.endpoints.jms;

import com.gft.bench.events.business.ChatMessageEvent;
import com.gft.bench.events.business.CreateUserEvent;
import com.gft.bench.events.business.RoomChangedEvent;

/**
 * Created by tzms on 3/25/2016.
 */
public interface JmsEndpoint {

	static final String CREATE_USER_QUEUE = CreateUserEvent.class.getName();
	static final String ROOM_CHANGED_QUEUE = RoomChangedEvent.class.getName();
	static final String CHAT_MESSAGE_QUEUE = ChatMessageEvent.class.getName();
	
    static final String MESSAGE_QUEUE_TO_SERVER = "MESSAGE.QUEUE.TO.SERVER";
    static final String MESSAGE_QUEUE_TO_CLIENT = "MESSAGE.QUEUE.TO.CLIENT";  
    static final String MESSAGE_REQUEST = "message_request";
    static final String MESSAGE_CONFIRMED = "message_confirmed";
    static final String EVENT_TYPE = "event_type";
    static final String USER_NAME = "user_name";
    static final String ROOM_NAME = "room_name";
    static final String REQUEST_RESULT = "request_result";
    static final String EVENT_QUEUE_TO_SERVER = "EVENT.QUEUE.TO.SERVER";
    static final String EVENT_QUEUE_TO_CLIENT = "EVENT.QUEUE.TO.CLIENT";
    static final String ENTER_ROOM_REQUEST = "enter_room_request";
    static final String ENTER_ROOM_CONFIRMED = "enter_room_confirmed";
    static final String CREATE_USER_REQUEST = "create_user_request";
    static final String CREATE_USER_CONFIRMED = "create_user_confirmed";
    
}
