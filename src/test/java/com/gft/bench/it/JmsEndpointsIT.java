package com.gft.bench.it;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.broker.BrokerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gft.bench.Disposer;
import com.gft.bench.client.ClientEnpointFactory;
import com.gft.bench.endpoints.ClientEndpoint;
import com.gft.bench.endpoints.ServerEndpoint;
import com.gft.bench.endpoints.TransportLayer;
import com.gft.bench.endpoints.jms.ServerJmsEndpoint;
import com.gft.bench.events.business.RoomChangedEvent;
import com.gft.bench.it.dto.AddRequest;
import com.gft.bench.it.dto.AddResponse;

public class JmsEndpointsIT {

	private static final Log log = LogFactory.getLog(JmsEndpointsIT.class);
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
    //public void shouldReceiveResponse() throws Exception {
    public void clientShouldReceiveResponseCorrespondingToRequest() throws Exception {	
    	
    	startInMemoryBroker();
    	
    	ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
    	serverEndpoint.<AddRequest, AddResponse>registerListener(AddRequest.class, request -> {
    		AddResponse response = new AddResponse();
    		response.z = request.x + request.y;
    		return response;
    	});
    	
    	ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
    	AddRequest request = new AddRequest();
    	request.x = 5;
    	request.y = 3;
    	CompletableFuture<AddResponse> future = clientEndpoint.<AddRequest, AddResponse>requestResponse(request);
    	AddResponse response = future.get(1, TimeUnit.SECONDS);

    	Assert.assertThat(response.z, Matchers.is(8));
    }
    
    
    @Test
    public void serverShouldReceiveClientNotification() throws Exception {
    	
    	startInMemoryBroker();
    	
    	ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
    	//TODO: JMS endpoint use RoomChangeEvent, it should create own type only for tests?
    	serverEndpoint.<RoomChangedEvent>registerNotificationListener(RoomChangedEvent.class, notification -> {
    		log.info("\n\nServer should implement what to do on this notification\n\n");
    	});
    	
    	ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
    	RoomChangedEvent roomChangedEvent = new RoomChangedEvent();
    	roomChangedEvent.room = "Movies";
    	clientEndpoint.sendNotification(roomChangedEvent);

    	//TODO: how to chceck that notificationHandler.onMessage() has been invoked ???
    }
    
    
    @Test
    public void clientShouldReceiveServerNotification() throws Exception {
    	
    	startInMemoryBroker();
    	
    	ClientEndpoint clientEndpoint = ClientEnpointFactory.getEndpoint(TransportLayer.JMS, BROKER_URL);
    	clientEndpoint.<RoomChangedEvent>registerNotificationListener(RoomChangedEvent.class, notification -> {
    		log.info("\n\nClient should implement what to do on this notification\n\n");
    	});
    	
    	ServerEndpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
    	RoomChangedEvent roomChangedEvent = new RoomChangedEvent();
    	roomChangedEvent.room = "Movies";
    	serverEndpoint.sendNotification(roomChangedEvent);

    	//TODO: how to chceck that notificationHandler.onMessage() has been invoked ???
    }
    
}
