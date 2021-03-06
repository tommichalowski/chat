package com.gft.bench.it;

import java.util.ArrayList;
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
import com.gft.bench.events.listeners.business.CreateUserListener;
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

    
    

    //@Test
    public void createUserRequestShouldBeReceivedByServer() throws Exception {
    	
    	startInMemoryBroker();
    	
    	ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
		Server server = new ServerImpl(serverEndpoint);
		disposables.add(() -> server.stopServer());
        
        ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
        ChatClient chatClient = new ChatClientImpl(clientEndpoint);
        disposables.add(() -> chatClient.stopClient());
        
        //CreateUserListener listener = (CreateUserListener) Mockito.spy(server.getEventListener(CreateUserEvent.class));
        CreateUserListener listener = Mockito.spy(new CreateUserListener());
        server.registerListener(CreateUserEvent.class, listener);
        
        String userName = "Tomasz_Test";
        CompletableFuture<CreateUserEvent> future = chatClient.createUser(userName);
        //BusinessEvent result = future.get();
        
        TimeUnit.SECONDS.sleep(1);
        Mockito.verify(listener, Mockito.times(1)).onEvent(Mockito.isA(CreateUserEvent.class));
    }
    
    
    //@Test
    public void createUserShouldReturnSuccessStatus() throws Exception {
    	
    	startInMemoryBroker();
    	
    	ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
		Server server = new ServerImpl(serverEndpoint);
		disposables.add(() -> server.stopServer());
        
        ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
        ChatClient chatClient = new ChatClientImpl(clientEndpoint);
        disposables.add(() -> chatClient.stopClient());
        
        String userName = "Tomasz_Test";
        CompletableFuture<CreateUserEvent> future = chatClient.createUser(userName);

        CreateUserEvent result = future.get();

        //Assert.assertEquals(RequestResult.SUCCESS, result.getResult());
        //Assert.assertEquals("Should have responded with expected message.", userName, result.getUserName());
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
        
        String userName = "Tomasz_Test";
        
        CompletableFuture<CreateUserEvent> futureUser1 = chatClient.createUser(userName);
        futureUser1.get();
        
        CompletableFuture<CreateUserEvent> futureUser2 = chatClient.createUser(userName);
        CreateUserEvent result2 = futureUser2.get();

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

        CountDownLatch latch = new CountDownLatch(1);
        clientEndpoint.registerNotificationListener(RoomChangedEvent.class, notification -> {
    		log.info("\n\nAction on client received RoomChangedEvent.class\n\n");
    		latch.countDown();
    	});
        
        String room = "Music";
        String userName = "Ania";
        CompletableFuture<CreateUserEvent> future = chatClient.createUser(userName);
        future.get();
        
        chatClient.enterToRoom(userName, room);
        
        boolean success = latch.await(3, TimeUnit.SECONDS);
    	Assert.assertTrue(success); 
    }
    
    
    @Test
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
