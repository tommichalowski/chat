package com.gft.bench.client;

import com.gft.bench.endpoints.ClientEndpoint;
import com.gft.bench.endpoints.TransportLayer;
import com.gft.bench.endpoints.jms.ClientJmsEndpoint;
import com.gft.bench.exceptions.ChatException;

public class ClientEnpointFactory {

	public static ClientEndpoint getEndpoint(TransportLayer layer, String brokerUrl) throws ChatException {
		switch (layer) {
			case JMS:
				return new ClientJmsEndpoint(brokerUrl);
			default:
				throw new ChatException("Can NOT get client enpoint instance for layer: " + layer + 
						" and broker URL: " + brokerUrl);
		}
	}
}
