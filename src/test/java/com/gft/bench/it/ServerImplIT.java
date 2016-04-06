package com.gft.bench.it;

import static com.jayway.awaitility.Awaitility.await;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gft.bench.client.ChatClient;
import com.gft.bench.client.ChatClientImpl;
import com.gft.bench.endpoints.ClientJmsEndpoint;
import com.gft.bench.endpoints.Endpoint;
import com.gft.bench.endpoints.ServerJmsEndpoint;
import com.gft.bench.server.Server;
import com.gft.bench.server.ServerImpl;

/**
 * Created by tzms on 4/4/2016.
 */
public class ServerImplIT {

    private static final String BROKER_URL = "tcp://localhost:62617";

    @Before
    public void runBroker() {
        try {
            BrokerService broker = new BrokerService();
            broker.setBrokerId("AMQ-BROKER-TEST");
            broker.setDeleteAllMessagesOnStartup(true);
            broker.addConnector(BROKER_URL);
            broker.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldCreateNewRoom() throws Exception {

        Endpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
        Server server = new ServerImpl(serverEndpoint);

        Endpoint clientEndpoint = new ClientJmsEndpoint(BROKER_URL);
        ChatClient chatClient = new ChatClientImpl(clientEndpoint);

        String room = "Movies";
        chatClient.enterToRoom(room);

        await().until( roomExists(server, room) );
        //await().until( roomExists2(chatClient) );
        //TimeUnit.SECONDS.sleep(5);

        List<String> roomHistory = server.getRoomHistory(room);
        Assert.assertNotNull("Should have found room: " + room, roomHistory);
        Assert.assertTrue("Should contain exactly one message in room: " + room, roomHistory.size() == 1);
        Assert.assertEquals("Should have responded with same message", Server.NEW_ROOM_CREATED + room, roomHistory.get(0));
    }
    
    
    private Callable<Boolean> roomExists(Server server, String room) {
        return new Callable<Boolean>() {
              public Boolean call() throws Exception {
            	  return server.getRoomHistory(room) != null;
              }
        };
    }
    
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
