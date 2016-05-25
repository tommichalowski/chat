package com.gft.bench;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.client.ChatClient;
import com.gft.bench.client.ChatClientImpl;
import com.gft.bench.client.ui.CommandProcessor;
import com.gft.bench.client.ui.CommandProcessorImpl;
import com.gft.bench.endpoints.jms.ServerJmsEndpoint;
import com.gft.bench.exceptions.ChatException;
import com.gft.bench.server.Server;
import com.gft.bench.server.ServerImpl;

public class App {

    private static final Log log = LogFactory.getLog(App.class);
    private static final String SERVER_MODE = "-s";
    private static final String BROKER_URL = "tcp://localhost:61616";
    
    public static void main(String[] args) {

        if (args != null && args.length > 0 && args[0].equals(SERVER_MODE)) {
        	try {
                ServerJmsEndpoint jmsEndpoint = new ServerJmsEndpoint(BROKER_URL);
                @SuppressWarnings("unused")
				Server server = new ServerImpl(jmsEndpoint);
            } catch (ChatException e) {
                log.error(e.getMessage());
            } 
        } else {
            try {
                ChatClient chatClient = new ChatClientImpl();
                CommandProcessor cp = new CommandProcessorImpl(chatClient);
                cp.processCommands();
                
//                String userName = "Tomi";
//                
//                CompletableFuture<DataEvent> createUserFuture = chatClient.createUser(userName);
//                
//                CompletableFuture<DataEvent> enterToRoomFuture = createUserFuture.thenCompose(result -> { 
//                	log.info("Create user result: " + result.getUserName());
//                	CompletableFuture<DataEvent> future = chatClient.enterToRoom(result.getUserName(), "Movies");
//                	return future;
//                });
//                
//                enterToRoomFuture.thenAccept(event -> log.info("Enter to room result: " + event.getRoom()));
//                
//                log.info("I'm still ready to work!!!\n");

            } catch (ChatException e) {
                log.error(e.getStackTrace());
                System.exit(0);
            }
        }
    }
}
