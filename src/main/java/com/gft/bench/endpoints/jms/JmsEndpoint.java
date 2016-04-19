package com.gft.bench.endpoints.jms;

/**
 * Created by tzms on 3/25/2016.
 */
public interface JmsEndpoint {

    static final String MESSAGE_QUEUE_TO_SERVER = "MESSAGE.QUEUE.TO.SERVER";
    static final String MESSAGE_TO_SERVER = "message_to_server";
    static final String ROOM_NAME = "room_name";
    static final String EVENT_QUEUE_TO_SERVER = "EVENT.QUEUE.TO.SERVER";
    static final String EVENT_QUEUE_TO_CLIENT = "EVENT.QUEUE.TO.CLIENT";
    static final String ENTER_ROOM_REQUEST = "enter_room_request";
    static final String ENTER_ROOM_CONFIRMED = "enter_room_confirmed";
}
