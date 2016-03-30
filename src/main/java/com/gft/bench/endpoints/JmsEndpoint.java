package com.gft.bench.endpoints;

import com.gft.bench.events.ChatEvent;

/**
 * Created by tzms on 3/25/2016.
 */
public class JmsEndpoint implements Endpoint {

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
