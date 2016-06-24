package com.gft.bench.it;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
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
import com.gft.bench.events.business.CreateUserEvent;
import com.gft.bench.events.business.RoomChangedEvent;
import com.gft.bench.events.business.RoomHistoryNotification;
import com.gft.bench.events.handlers.CreateUserHandler;
import com.gft.bench.server.Server;
import com.gft.bench.server.ServerImpl;

/**
 * Created by tzms on 4/4/2016.
 */
public class ServerImplIT {

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
    
    private BrokerService startInMemoryBroker() throws Exception {
    	
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
    public void createUserRequestShouldSucceed() throws Exception {
    	
    	startInMemoryBroker();
    	
    	ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
		Server server = new ServerImpl(serverEndpoint);
		disposables.add(() -> server.stopServer());
        
        ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
        ChatClient chatClient = new ChatClientImpl(clientEndpoint);
        disposables.add(() -> chatClient.stopClient());

        server.registerRequestResponseListener(CreateUserEvent.class, CreateUserEvent.class, new CreateUserHandler(server));
        
//        server.registerRequestResponseListener(CreateUserEvent.class, CreateUserEvent.class, request -> {
//        	
//        	boolean addedNewUser = server.getUsersLogins().add(request.userName);
//    		if (addedNewUser) {
//    			return new CreateUserEvent(request.userName);
//    		} else {
//    			return new CreateUserEvent("Failed"); //TODO: what to do if failed???
//    		}
//        });
        
        String userName = "Tomasz_Test";
        CompletableFuture<CreateUserEvent> future = chatClient.createUser(userName);
        CreateUserEvent response = future.get(3, TimeUnit.SECONDS);

        Assert.assertEquals(userName, response.userName);
    }
        
    
    //@Test
    public void createUserShouldReturnErrorStatusDueToNotUniqueUserName() throws Exception {
    	
    	startInMemoryBroker();
    	
    	ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
		Server server = new ServerImpl(serverEndpoint);
		disposables.add(() -> server.stopServer());
        
        ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
        ChatClient chatClient = new ChatClientImpl(clientEndpoint);
        disposables.add(() -> chatClient.stopClient());
        
        server.registerRequestResponseListener(CreateUserEvent.class, CreateUserEvent.class, new CreateUserHandler(server));
        
        String userName = "Tomasz_Test";
        CompletableFuture<CreateUserEvent> futureUser1 = chatClient.createUser(userName);
        futureUser1.get(3, TimeUnit.SECONDS);
        
        CompletableFuture<CreateUserEvent> futureUser2 = chatClient.createUser(userName);
        CreateUserEvent result2 = futureUser2.get(3, TimeUnit.SECONDS);

        Assert.assertEquals("Error", "TODO");
        //Assert.assertEquals(RequestResult.ERROR, result2.getResult());
    }
    
    
    @Test
    public void enteringToNewRoomShouldResultWithRoomChangedNotification() throws Exception {

    	startInMemoryBroker();
    	
        ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
		Server server = new ServerImpl(serverEndpoint);
		disposables.add(() -> server.stopServer());
		
        ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
        ChatClientImpl chatClient = Mockito.spy(new ChatClientImpl(clientEndpoint));
        disposables.add(() -> chatClient.stopClient());

        server.registerRequestResponseListener(CreateUserEvent.class, CreateUserEvent.class, new CreateUserHandler(server));
        server.registerNotificationListener(RoomChangedEvent.class, event -> { 
        	synchronized (server.getUsersLogins()) { //added due to check if userName exist when creating room
	    		if (server.getUsersLogins().contains(event.user)) {
		            LinkedList<String> roomHistory = server.addRoom(event.room, event.user);
		            RoomHistoryNotification roomChanded = new RoomHistoryNotification(server.formatRoomHistory(roomHistory));
		            server.sendNotification(roomChanded);
	    		}
			}
        });
        
        CountDownLatch latch = new CountDownLatch(1);
        chatClient.registerNotificationListener(RoomHistoryNotification.class, notification -> {
    		latch.countDown();
    	});
        
        String room = "Music";
        String userName = "Tomek";
        CompletableFuture<CreateUserEvent> future = chatClient.createUser(userName);
        future.get(3, TimeUnit.SECONDS);
        
        chatClient.enterToRoom(userName, room);
        
        boolean success = latch.await(3, TimeUnit.SECONDS);
    	Assert.assertTrue(success); 
    	
    	TimeUnit.SECONDS.sleep(1);
    }
    
    
    //@Test
    public void enteringToNewRoomShouldResultWithErrorStatusWhenUserDoesntExist() throws Exception {

    	startInMemoryBroker();
    	
        ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
        Server server = new ServerImpl(serverEndpoint);
		disposables.add(() -> server.stopServer());
		
        ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
        ChatClient chatClient = new ChatClientImpl(clientEndpoint);
        disposables.add(() -> chatClient.stopClient());
        
        CountDownLatch latch = new CountDownLatch(1);
        clientEndpoint.registerNotificationListener(RoomChangedEvent.class, notification -> {
    		log.info("\n\nAction on client received RoomChangedEvent.class\n\n");
    		latch.countDown();
    	});
        
        String room = "Music";
        String userName = "Ania";
        chatClient.enterToRoom(userName, room);
        
        boolean success = latch.await(3, TimeUnit.SECONDS);
    	Assert.assertTrue(success);  
    }

}
