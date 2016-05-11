package com.gft.bench.it;

import java.util.concurrent.CompletableFuture;

import org.apache.activemq.broker.BrokerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import com.gft.bench.client.ChatClient;
import com.gft.bench.client.ChatClientImpl;
import com.gft.bench.client.ClientEnpointFactory;
import com.gft.bench.endpoints.ClientEndpoint;
import com.gft.bench.endpoints.ServerEndpoint;
import com.gft.bench.endpoints.TransportLayer;
import com.gft.bench.endpoints.jms.ServerJmsEndpoint;
import com.gft.bench.events.DataEvent;
import com.gft.bench.events.RequestResult;
import com.gft.bench.server.Server;
import com.gft.bench.server.ServerImpl;

/**
 * Created by tzms on 4/4/2016.
 */
public class ServerImplIT {

	private static final Log log = LogFactory.getLog(ServerImplIT.class);
    private static final String BROKER_URL = "tcp://localhost:62618";
       
    
    private BrokerService startBroker() throws Exception {
    	
    	BrokerService broker = new BrokerService();
    	broker.setBrokerId("AMQ-BROKER-TEST");
    	broker.setDeleteAllMessagesOnStartup(true);
    	broker.addConnector(BROKER_URL);
    	broker.start();
    	return broker;
    }
    
    private void stopBrokerIfRunning(BrokerService broker) throws Exception {
		
    	if (broker != null && broker.isStarted()) {
			broker.stop();
		}
    }

    
    @Test
    public void createUserShouldReturnSuccessStatus() throws Exception {
    	
    	BrokerService broker = startBroker();
    	
    	ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
		Server server = new ServerImpl(serverEndpoint);
        
        ClientEndpoint clientEndpoint1 = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
        ChatClient chatClient1 = new ChatClientImpl(clientEndpoint1);
        
        String userName = "Tomasz_Test";
        CompletableFuture<DataEvent> future = chatClient1.createUser(userName);

        DataEvent result = future.get();

        Assert.assertEquals(RequestResult.SUCCESS, result.getResult());
        Assert.assertEquals("Should have responded with expected message.", userName, result.getUserName());
                
        chatClient1.stopClient();
        server.stopServer();
        stopBrokerIfRunning(broker);
    }
    
    
    @Test
    public void createUserShouldReturnErrorStatusDueToNotUniqueUserName() throws Exception {
    	
    	BrokerService broker = startBroker();
    	
    	ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
		Server server = new ServerImpl(serverEndpoint);
        
        ClientEndpoint clientEndpoint1 = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
        ChatClient chatClient1 = new ChatClientImpl(clientEndpoint1);
        
        String userName = "Tomasz_Test";
        
        CompletableFuture<DataEvent> futureUser1 = chatClient1.createUser(userName);
        futureUser1.get();
        
        CompletableFuture<DataEvent> futureUser2 = chatClient1.createUser(userName);
        DataEvent result2 = futureUser2.get();

        Assert.assertEquals(RequestResult.ERROR, result2.getResult());
                
        chatClient1.stopClient();
        server.stopServer();
        stopBrokerIfRunning(broker);
    }
    
    
    @Test
    public void enteringToNewRoomShouldResultWithSuccessStatus() throws Exception {

    	BrokerService broker = startBroker();
    	
        ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
		Server server = new ServerImpl(serverEndpoint);
        
        ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
        ChatClient chatClient = new ChatClientImpl(clientEndpoint);

        String room = "Music";
        String userName = "Ania";
        CompletableFuture<DataEvent> future = chatClient.createUser(userName);
        DataEvent result = future.get();
        
        future = chatClient.enterToRoom(userName, room);
        result = future.get();
        
        log.info("Room history: \n" + result.getData());
        Assert.assertEquals(RequestResult.SUCCESS, result.getResult());
        
        chatClient.stopClient();
        server.stopServer();
        stopBrokerIfRunning(broker);
    }
    
    
    @Test
    public void enteringToNewRoomShouldResultWithErrorStatusWhenUserDoesntExist() throws Exception {
    	
    	BrokerService broker = null;
    	Server server = null;
    	ChatClient chatClient = null;
    	
    	try {
	    	broker = startBroker();
	    	
	        ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
			server = new ServerImpl(serverEndpoint);
	        
	        ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
	        chatClient = new ChatClientImpl(clientEndpoint);
	
	        String room = "Music";
	        String userName = "Ania";
	        CompletableFuture<DataEvent> future = chatClient.enterToRoom(userName, room);
	        DataEvent result = future.get();
	        
	        Assert.assertEquals(RequestResult.ERROR, result.getResult());	
		} finally {
			chatClient.stopClient();
	        server.stopServer();
	        stopBrokerIfRunning(broker);
		}
        
    }
    
    
//    @Test
//    public void enteringToNewRoomShouldResultWithErrorStatus() throws Exception {
//        
//        ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
//        ChatClient chatClient = new ChatClientImpl(clientEndpoint);
//
//        String room = "Movies";
//        ResultMsg enterToRoomResult = chatClient.enterToRoom(room);
//
//        Assert.assertEquals("Should respond with create room error request result.",       
//        		            RequestResult.ERROR, enterToRoomResult.getResult());
//    }
//    
//    
    
//    @Test
//    public void shouldCreateNewRoomOnServer() throws Exception {
//
//        ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
//        Server server = new ServerImpl(serverEndpoint);
//        
//        ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
//        ChatClient chatClient = new ChatClientImpl(clientEndpoint);
//
//        String room = "Movie";
//        chatClient.enterToRoomRequest(room);
//
//        await().until( roomExists(server, room) );
//        
//        List<String> roomHistory = server.getRoomHistory(room);
//        Assert.assertNotNull("Should have found room: " + room, roomHistory);
//        Assert.assertTrue("Should contain exactly one message in room: " + room, roomHistory.size() == 1);
//        Assert.assertEquals("Should have responded with expected message", Server.NEW_ROOM_CREATED + room, roomHistory.get(0));
//    }
    
    
//    private Callable<Boolean> roomExists(Server server, String room) {
//        return new Callable<Boolean>() {
//              public Boolean call() throws Exception {
//            	  return server.getRoomHistory(room) != null;
//              }
//        };
//    }
    
//    private Callable<Boolean> roomExists2(ChatClientImpl c) {
//        return new Callable<Boolean>() {
//              public Boolean call() throws Exception {
//            	  return c.IsLastEnterRoomFinished();
//              }
//        };  
//    }
    
//    private Callable<Integer> numberOfMessagesInRoom(Server server, String room) {
//        return new Callable<Integer>() {
//              public Integer call() throws Exception {
//            	  if (server.getRoomHistory(room) == null) { 
//            		  return 0; 
//            	  }
//                  return server.getRoomHistory(room).size();
//              }
//        };
//    }
    
}
