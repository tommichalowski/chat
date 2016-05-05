package com.gft.bench.it;

import static org.hamcrest.CoreMatchers.containsString;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;

import org.apache.activemq.broker.BrokerService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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
import com.gft.bench.events.ResultMsg;
import com.gft.bench.exceptions.ChatException;
import com.gft.bench.server.Server;
import com.gft.bench.server.ServerImpl;

/**
 * Created by tzms on 4/4/2016.
 */
public class ServerImplIT {

    private static final String BROKER_URL = "tcp://localhost:62618"; // "tcp://localhost:8161";
    
    private BrokerService broker = null;
//    private Server server;
//    private ChatClient chatClient;
    
//    @BeforeClass
//    public static void runBroker() throws Exception {
//        startBroker();     
//    }
//
//    @AfterClass
//    public static void stopBrokerIfRunning() throws Exception {
//    	if (broker != null && broker.isStarted()) {
//    		broker.stop();
//    	}
//    }
    
    
    @Before
    public void runBroker() throws Exception {
        startBroker();     
    }

//    @After
//    public void stopBrokerIfRunning() throws Exception {
//    	if (broker != null && broker.isStarted()) {
//    		broker.stop();
//    	}
//    }
    
    
    private void startBroker() throws Exception {
    	broker = new BrokerService();
    	broker.setBrokerId("AMQ-BROKER-TEST");
    	broker.setDeleteAllMessagesOnStartup(true);
    	broker.addConnector(BROKER_URL);
    	broker.start();
    }

    
    @Test
    public void shouldRespondToCorrectClient() {
    	
    	try {
	    	ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
	        @SuppressWarnings("unused")
			Server server = new ServerImpl(serverEndpoint);
	        
	        ClientEndpoint clientEndpoint1 = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
	        ChatClient chatClient1 = new ChatClientImpl(clientEndpoint1);
	        
	//        ClientEndpoint clientEndpoint2 = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
	//        ChatClient chatClient2 = new ChatClientImpl(clientEndpoint2);
	        
	        String userName = "Tomasz_Test";
	        CompletableFuture<DataEvent> future = chatClient1.createUser(userName);
	        
	        DataEvent result = future.get();
	        Assert.assertEquals("Should have responded with expected message.", userName, result.getUserName());
	        
	        TimeUnit.SECONDS.sleep(1);
	//                
	//        future.thenApply(result -> {
	//        	System.out.println("Create user result: " + result.getUserName());
	//        	Assert.assertEquals("Should have responded with expected message.", "Bad", result.getUserName());
	//        	return result;
	//        });
		} catch (Exception e) {
			System.out.println("ERROR on test!!!");
			e.printStackTrace();
		}
    }
    
    
//    @Test
//    public void enteringToNewRoomShouldResultWithSuccessStatus() throws Exception {
//
//        ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
//        @SuppressWarnings("unused")
//		Server server = new ServerImpl(serverEndpoint);
//        
//        ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
//        ChatClient chatClient = new ChatClientImpl(clientEndpoint);
//
//        String room = "Music";
//        ResultMsg enterToRoomResult = chatClient.enterToRoom(room);
//
//        Assert.assertEquals("Should respond with create room success request result.",       
//        		            RequestResult.SUCCESS, enterToRoomResult.getResult());
//        
//        Assert.assertThat("Should have responded with expected message.",
//        		          enterToRoomResult.getMessage(), containsString(Server.NEW_ROOM_CREATED + room));
//    }
//    
//    
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
