package com.gft.bench;

import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.client.ChatClient;
import com.gft.bench.client.ChatClientImpl;
import com.gft.bench.endpoints.jms.ServerJmsEndpoint;
import com.gft.bench.events.RequestResult;
import com.gft.bench.events.ResultMsg;
import com.gft.bench.exceptions.ChatException;
import com.gft.bench.server.Server;
import com.gft.bench.server.ServerImpl;

public class App {

    private static final Log log = LogFactory.getLog(App.class);
    private static final String SERVER_MODE = "-s";
    private static final String BROKER_URL = "tcp://localhost:61616";
    
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
                ChatClient chatClient = new ChatClientImpl();
                ResultMsg enterToRoomResult = chatClient.enterToRoom("Movies");
                
                log.info("Enter to room result: " + enterToRoomResult.getMessage());
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
            } catch (ChatException e) {
                log.error(e.getStackTrace());
                System.exit(0);
            }
        }
    }
}
