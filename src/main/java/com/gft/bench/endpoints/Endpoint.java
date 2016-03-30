package com.gft.bench.endpoints;

import com.gft.bench.events.ChatEvent;

/**
 * Created by tzms on 3/25/2016.
 */
public interface Endpoint {

    String getEndpointUrl();

    void sendEvent(ChatEvent event);
}
