package com.gft.bench.endpoints;

import javax.jms.Message;

public class JmsMessageHandler implements MessageHandler {

	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean accept(String CorrelationId) {
		// TODO Auto-generated method stub
		return false;
	}

}
