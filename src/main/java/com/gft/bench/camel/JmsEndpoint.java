package com.gft.bench.camel;

import com.gft.bench.ChatEndpoint;
import com.gft.bench.events.ChatEvent;

/**
 * Created by tzms on 3/25/2016.
 */
public class JmsEndpoint implements ChatEndpoint {

    private final String brokerUrl;

    public JmsEndpoint(String brokerUrl){
        this.brokerUrl = brokerUrl;
    }

    @Override
    public String getEndpointUrl() {
        return brokerUrl;
    }

    @Override
    public void sendEvent(ChatEvent event) {

    }

}
