package it.com.gft.bench;

import com.gft.bench.client.ChatClient;
import com.gft.bench.client.ChatClientImpl;
import com.gft.bench.endpoints.ClientJmsEndpoint;
import com.gft.bench.endpoints.Endpoint;
import com.gft.bench.endpoints.ServerJmsEndpoint;
import com.gft.bench.server.Server;
import com.gft.bench.server.ServerImpl;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

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
    public void shouldBeAbleToSendMessage() throws Exception {

        Endpoint serverEndpoint = new ServerJmsEndpoint(BROKER_URL);
        Server server = new ServerImpl(serverEndpoint);

        Endpoint clientEndpoint = new ClientJmsEndpoint(BROKER_URL);
        ChatClient chatClient = new ChatClientImpl(clientEndpoint);

        String room = "Movies";
        chatClient.enterToRoom(room);

        try {
            Thread.sleep(9000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        LinkedList<String> roomHistory = server.getRoomHistory(room);
        Assert.assertNotNull("Should have found room: " + room, roomHistory);
        Assert.assertTrue("Should contain exactly one message in room: " + room, roomHistory.size() == 1);
        Assert.assertEquals("Should have responded with same message", server.NEW_ROOM_CREATED + room, roomHistory.pollFirst());
    }
}
