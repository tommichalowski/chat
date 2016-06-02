package com.gft.bench.endpoints;

import javax.jms.Message;

public interface MessageHandler {

	void onMessage(Message message);
	boolean accept(String CorrelationId);
}
