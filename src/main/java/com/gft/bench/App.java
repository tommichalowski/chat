package com.gft.bench;

import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.client.ChatClient;
import com.gft.bench.client.ChatClientImpl;
import com.gft.bench.endpoints.Endpoint;
import com.gft.bench.endpoints.JmsEndpoint;
import com.gft.bench.server.ServerImpl;
import com.gft.bench.server.Server;

public class App {

	private static final Log log = LogFactory.getLog(App.class);
	private static final String BROKER_URL = "tcp://localhost:61616";
	private static final String SERVER_MODE = "-s";
	
    public static void main(String[] args) {
    	
		Endpoint chatEndpoint = new JmsEndpoint(BROKER_URL);
		
		if (args != null && args.length > 0 && args[0].equals(SERVER_MODE)) {
			Server server = new ServerImpl();
			server.setEndpoint(chatEndpoint);
			try {
				server.startServer();
			} catch (JMSException e) {
				log.error(e.getMessage());
				System.exit(0);
			}
		} else {			
			ChatClient chatClient = new ChatClientImpl();

			try {
				chatClient.connectToEndpoint(chatEndpoint);
				chatClient.enterToRoom("games");
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}

		//chatClient.sendMessageToRoom("");
        //ChatClient chatClient = new ChatClient();
        //chatClient.runPublisher(TOPIC);
        //chatClient.runSubscriber(TOPIC);
        
//        CmdLineTool cmd = new CmdLineTool();
//
//        while (true) {
//        	ResultMsg resultMsg = cmd.readLine();
//        	
//        	try {
//	        	if (ResultEnum.NORMAL.equals(resultMsg.getResult())) {
//					chatClient.writeMessage(resultMsg.getMessage());
//	        	} else if (ResultEnum.EXIT.equals(resultMsg.getResult())) {
//	        		chatClient.close();
//	        		System.exit(0);
//	        	}
//        	} catch (JMSException e) {
//				log.error(e.getStackTrace());
//			}
//        }
    }
}
