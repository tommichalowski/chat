package com.gft.bench.events.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.client.ChatClient;
import com.gft.bench.client.ChatClientImpl;
import com.gft.bench.client.ui.Display;
import com.gft.bench.events.EventListener;
import com.gft.bench.events.notification.UserCreated;

public class CreateUserListener implements EventListener<UserCreated> {

	private static final Log log = LogFactory.getLog(CreateUserListener.class);
	private Display display;
	private ChatClientImpl chatClient;
	
	@Override
	public void onEvent(UserCreated event) {
		log.info("\n\nIn CreateUserListener");
		if (display != null) {
			display.print("CreateUserListener");
		}
		
		if (chatClient != null) {
			chatClient.asyncEventReceived(event);
		}
	}

}
