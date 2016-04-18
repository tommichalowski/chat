package com.gft.bench;

import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.client.ChatClient;
import com.gft.bench.client.ChatClientImpl;
import com.gft.bench.endpoints.ClientEndpoint;
import com.gft.bench.endpoints.ClientJmsEndpoint;
import com.gft.bench.endpoints.ServerJmsEndpoint;
import com.gft.bench.events.RequestResult;
import com.gft.bench.server.Server;
import com.gft.bench.server.ServerImpl;

public class App {

    private static final Log log = LogFactory.getLog(App.class);
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String SERVER_MODE = "-s";

    public static void main(String[] args) {

        if (args != null && args.length > 0 && args[0].equals(SERVER_MODE)) {
        	Server server = null;
        	try {
                ServerJmsEndpoint jmsEndpoint = new ServerJmsEndpoint(BROKER_URL);
                server = new ServerImpl(jmsEndpoint);
            } catch (JMSException e) {
                log.error(e.getMessage());
            } finally {
            	if (server != null) { server.stopServer(); }
            	System.exit(0);
			}
        } else {
            try {
                ClientEndpoint jmsEndpoint = new ClientJmsEndpoint(BROKER_URL);
                ChatClient chatClient = new ChatClientImpl(jmsEndpoint);
                
                ResultMsg enterToRoomResult = chatClient.enterToRoomRequest("Thread-test");
                log.info("Enter to room result: " + enterToRoomResult.getMessage());
                //chatClient.enterToRoomWithoutConfirmation("Movies");
                if (enterToRoomResult.getResult() == RequestResult.ERROR) {
                	System.exit(0);
                }
                
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
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
