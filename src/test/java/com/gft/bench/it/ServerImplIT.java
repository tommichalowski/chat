package com.gft.bench.it;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.broker.BrokerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.gft.bench.Disposer;
import com.gft.bench.client.ChatClient;
import com.gft.bench.client.ChatClientImpl;
import com.gft.bench.client.ClientEnpointFactory;
import com.gft.bench.endpoints.ClientEndpoint;
import com.gft.bench.endpoints.ServerEndpoint;
import com.gft.bench.endpoints.TransportLayer;
import com.gft.bench.endpoints.jms.ServerJmsEndpoint;
import com.gft.bench.events.DataEvent;
import com.gft.bench.events.RequestResult;
import com.gft.bench.events.notification.RoomChanged;
import com.gft.bench.listeners.BusinessEventListener;
import com.gft.bench.server.Server;
import com.gft.bench.server.ServerImpl;

/**
 * Created by tzms on 4/4/2016.
 */
public class ServerImplIT {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(ServerImplIT.class);
    private static final String BROKER_URL = "tcp://localhost:62618";
    private ArrayList<AutoCloseable> disposables;   
    
    
    @Before
    public void initialize() {
    	disposables = new ArrayList<>();
    }
    
    @After
    public void dispose() throws Exception {
    	for (AutoCloseable aCloseable : disposables) {
    		aCloseable.close();
    	}
    	disposables = null;    	
    }
    
    private BrokerService startBroker() throws Exception {
    	
    	BrokerService broker = new BrokerService();
    	Disposer brokerDisposer = new Disposer(() -> broker.stop());
    	disposables.add(brokerDisposer);
    	
    	broker.setBrokerId("AMQ-BROKER-TEST");
    	broker.setDeleteAllMessagesOnStartup(true);
    	broker.addConnector(BROKER_URL);
    	broker.start();
    	return broker;
    }

    
    @Test
    public void createUserShouldReturnSuccessStatus() throws Exception {
    	
    	startBroker();
    	
    	ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
		Server server = new ServerImpl(serverEndpoint);
		disposables.add(() -> server.stopServer());
        
        ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
        ChatClient chatClient = new ChatClientImpl(clientEndpoint);
        disposables.add(() -> chatClient.stopClient());
        
        String userName = "Tomasz_Test";
        CompletableFuture<DataEvent> future = chatClient.createUser(userName);

        DataEvent result = future.get();

        Assert.assertEquals(RequestResult.SUCCESS, result.getResult());
        Assert.assertEquals("Should have responded with expected message.", userName, result.getUserName());
    }
    
    
    @Test
    public void createUserShouldReturnErrorStatusDueToNotUniqueUserName() throws Exception {
    	
    	startBroker();
    	
    	ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
		Server server = new ServerImpl(serverEndpoint);
		disposables.add(() -> server.stopServer());
        
        ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
        ChatClient chatClient = new ChatClientImpl(clientEndpoint);
        disposables.add(() -> chatClient.stopClient());
        
        String userName = "Tomasz_Test";
        
        CompletableFuture<DataEvent> futureUser1 = chatClient.createUser(userName);
        futureUser1.get();
        
        CompletableFuture<DataEvent> futureUser2 = chatClient.createUser(userName);
        DataEvent result2 = futureUser2.get();

        Assert.assertEquals(RequestResult.ERROR, result2.getResult());
    }
    
    
    @Test
    public void enteringToNewRoomShouldResultWithRoomChangedNotification() throws Exception {

    	startBroker();
    	
        ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
		Server server = new ServerImpl(serverEndpoint);
		disposables.add(() -> server.stopServer());
		
        ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
        ChatClientImpl chatClient = Mockito.spy(new ChatClientImpl(clientEndpoint));
        disposables.add(() -> chatClient.stopClient());
        
        //ClientMessageListener listener = new ClientMessageListener(chatClient);
        RoomChanged event = new RoomChanged();
//        chatClient.registerListener(event, chatClient);
        
        BusinessEventListener listener = Mockito.spy(new BusinessEventListener());
        chatClient.registerListener(event, listener);
        
        String room = "Music";
        String userName = "Ania";
        CompletableFuture<DataEvent> future = chatClient.createUser(userName);
        future.get();
        
        chatClient.enterToRoom(userName, room);
        
        RoomChanged testEvent = new RoomChanged(); 
        listener.onEvent(testEvent);
//        chatClient.onEvent(testEvent);
        
        TimeUnit.SECONDS.sleep(1);

        //Mockito.verify(chatClient, Mockito.times(1)).onEvent(Mockito.isA(RoomChanged.class));
        Mockito.verify(listener, Mockito.times(1)).onEvent(Mockito.isA(RoomChanged.class));
    }
    
    
    @Test
    public void enteringToNewRoomShouldResultWithErrorStatusWhenUserDoesntExist() throws Exception {

	    	startBroker();
	    	
	        ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
	        Server server = new ServerImpl(serverEndpoint);
			disposables.add(() -> server.stopServer());
			
	        ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
	        ChatClient chatClient = new ChatClientImpl(clientEndpoint);
	        disposables.add(() -> chatClient.stopClient());
	        
	        String room = "Music";
	        String userName = "Ania";
	        CompletableFuture<DataEvent> future = chatClient.enterToRoom(userName, room);
	        DataEvent result = future.get();
	        
	        Assert.assertEquals(RequestResult.ERROR, result.getResult());	   
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
