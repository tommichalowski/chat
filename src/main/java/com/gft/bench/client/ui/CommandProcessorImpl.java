package com.gft.bench.client.ui;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.client.ChatClient;
import com.gft.bench.events.business.BusinessEvent;

public class CommandProcessorImpl implements CommandProcessor {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(CommandProcessorImpl.class);
	
	Display display = new DisplayImpl(System.in, System.out);
	ChatClient chatClient;
	
	
	public CommandProcessorImpl(ChatClient chatClient) {
		this.chatClient = chatClient;
	}
	

	@Override
	public void processCommands() {
		
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(() -> {
			
			CompletableFuture<BusinessEvent> future = null; 	
			
			while (true) {
				UIEvent event = display.handleInput();
				UIEventType eventType = event.eventType;
				String msg = event.message;	
				
				switch (eventType) {
					case CREATE_USER:
						future = chatClient.createUser(msg);
						break;
					case ENTER_ROOM:
						future = chatClient.enterToRoom("UserName", "Movies");
					default:
						break;
				}
					
				future.thenAccept(result -> {
					display.print(result.getData());
				});
			}
		});
		
	}


	@Override
	public void print(String message) {
		display.print(message);
	}
	
}
