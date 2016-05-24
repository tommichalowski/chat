package com.gft.bench.events.listeners.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.client.ChatClientImpl;
import com.gft.bench.client.ui.Display;
import com.gft.bench.events.EventListener;
import com.gft.bench.events.business.CreateUserEvent;

public class CreateUserListener implements EventListener<CreateUserEvent> {

	private static final Log log = LogFactory.getLog(CreateUserListener.class);
	private Display display;
	private ChatClientImpl chatClient;
	
	@Override
	public void onEvent(CreateUserEvent event) {
		
		log.info("\n\nIn CreateUserListener \n");
		
		if (display != null) {
			display.print("CreateUserListener");
		}
		
		if (chatClient != null) {
//			chatClient.asyncEventReceived(event);
		}
	}

}
