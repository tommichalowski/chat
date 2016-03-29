package com.gft.bench;

import com.gft.bench.events.ChatEvent;

/**
 * Created by tzms on 3/25/2016.
 */
public interface ChatEndpoint {

    String getEndpointUrl();

    void sendEvent(ChatEvent event);
}
