package com.gft.bench.endpoints;

import javax.jms.JMSException;
import javax.jms.MessageListener;

import com.gft.bench.client.ChatClientImpl;
import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.ChatEventListener;

/**
 * Created by tzms on 3/25/2016.
 */
public interface Endpoint {


    void listenForEvent();
    
    void sendEvent(ChatEvent event);

    void setEventListener(ChatEventListener messageListener);
}
