package com.gft.bench;

import org.junit.Assert;
import org.junit.Before;
//import static junit.framework.Assert.*;
import org.junit.Test;

import com.gft.bench.old.ChatClientOld;


/**
 * Created by tzms on 3/23/2016.
 */
public class ChatClientTest {


	private ChatClientOld chatClient = null;
//
//    //@Mock
//    private TopicSession publisherSession = Mockito.mock(TopicSession.class);
//    private TopicPublisher publisher = Mockito.mock(TopicPublisher.class);
//
//    private static final String TEST_TOPIC = "test.topic";


	@Before
	public void setUp() {
		chatClient = new ChatClientOld();
	}
	
	@Test
	public void testCreate() {
		//chatClient = new ChatClient();
		//Assert.assertNotNull(chatClient.getTopicConnection());
	}
	
    @Test
    public void runPublisher() throws Exception {
    	
    }

    @Test
    public void runSubscriber() throws Exception {

    }

    @Test
    public void onMessage() throws Exception {

    }

    @Test
    public void writeMessage() throws Exception {
        //MockitoAnnotations.initMocks(ChatClientTest.class);
//        chatClient.setPublisherSession(publisherSession);
//        chatClient.setPublisher(publisher);
//        chatClient.writeMessage("test    ");
//        TextMessage textMessage = new ActiveMQTextMessage();
//
//        Mockito.when(publisherSession.createTextMessage("test")).thenReturn(textMessage);
//
//        Mockito.verify(publisher, Mockito.times(1)).publish(textMessage);
//        textMessage.getText().equals("test   ");

    }

    @Test
    public void close() throws Exception {

    }
}