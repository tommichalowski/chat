package com.gft.bench;

import java.util.concurrent.CompletableFuture;

import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.client.ChatClient;
import com.gft.bench.client.ChatClientImpl;
import com.gft.bench.endpoints.jms.ServerJmsEndpoint;
import com.gft.bench.events.DataEvent;
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
            } catch (JMSException e) {
                log.error(e.getMessage());
            } 
        } else {
            try {
                ChatClient chatClient = new ChatClientImpl();
                
                String userName = "Tomi";
                
                CompletableFuture<DataEvent> future = chatClient.createUser(userName);
                
                future.thenApply(result -> {
                	log.info("Create user result: " + result.getUserName());
                	return result;
                }).thenApply(result -> chatClient.enterToRoom(userName, "Movies")).
                thenApply(restult -> { 
                	log.info("Enter room resultL " + restult.get);
                	return restult;
                });
                	
//                CompletableFuture<ChatEvent> enterToRoomFuture = chatClient.enterToRoom("Movies");
//                
//                enterToRoomFuture.thenApply(result -> {
//                	log.info("Enter to room result: " + result.getMessage());
//                	return result;
//                });
//                
//                CompletableFuture<ChatEvent> futureMessage = chatClient.sendMessageToRoom("Movies", "This is my message.");
//                futureMessage.thenApply(result -> {
//                	log.info("Message result: " + result.getMessage());
//                	return result;
//                });
                
                log.info("I'm still ready to work!!!\n");
                
                
                
                
//                if (enterToRoomResult.getResult() == RequestResult.ERROR) {
//                	System.exit(0);
//                }
                
              //  CmdLineTool cmd = new CmdLineTool();

              //  while (true) {
                  //  ResultMsg resultMsg = cmd.readLine();

                   // if (ResultType.NORMAL.equals(resultMsg.getResult())) {
                    //    chatClient.sendMessageToRoom("Movies", resultMsg.getMessage());
                   // } else if (ResultType.EXIT.equals(resultMsg.getResult())) {
//                      chatClient.close();
                   //     System.exit(0);
                   // }

              //  }
            } catch (ChatException e) {
                log.error(e.getStackTrace());
                System.exit(0);
            }
        }
    }
}
