package com.gft.bench.endpoints.jms;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.ClientEndpoint;
import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.ChatEventListener;
import com.gft.bench.events.EnterToRoomRequest;
import com.gft.bench.events.EventType;
import com.gft.bench.events.MessageEvent;
import com.gft.bench.events.RequestResult;
import com.gft.bench.events.ResultMsg;
import com.gft.bench.exceptions.ChatException;
import com.gft.bench.exceptions.RequestException;

/**
 * Created by tzms on 3/31/2016.
 * @param <T>
 * @param <T>
 */
public class ClientJmsEndpoint implements ClientEndpoint, JmsEndpoint, MessageListener {

	private static final Log log = LogFactory.getLog(ClientJmsEndpoint.class);
    private final BlockingQueue<ResultMsg> responses = new ArrayBlockingQueue<ResultMsg>(10);
    protected final String brokerUrl;
    protected ActiveMQConnectionFactory connectionFactory;
    protected Connection connection;
    protected Session session;
    protected ChatEventListener messageListener;

    
    public ClientJmsEndpoint(String brokerUrl) throws ChatException {
        this.brokerUrl = brokerUrl;
        connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        try {
			connection = connectionFactory.createConnection();
			connection.start();
	        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			throw new ChatException("Can NOT create JMS connection!", e);
		}
    }

    
    @Override
    public Future<ResultMsg> request(ChatEvent event) { 
    
    	ExecutorService executorService = Executors.newSingleThreadExecutor();
	    Future<ResultMsg> future = executorService.submit(new Callable<ResultMsg>() {
			@Override
			public ResultMsg call() throws RequestException  {
				sendEvent(event);
				Message receivedMessage = receiveMessage(event);
				ResultMsg resultMsg = processMessage(receivedMessage);
				//ResultMsg resultMsg = (ResultMsg) getResponseWhenCame();
		        return resultMsg;
			}
		});
	    
	    return future;
    }

    
    @Override
    public Message receiveMessage(ChatEvent event) throws RequestException {
    	
    	Message receivedMessage = null;
    	try {
	    	if (event.getType() == EventType.ENTER_ROOM) {
				Destination serverEventQueue = session.createQueue(EVENT_QUEUE_TO_CLIENT);
			    MessageConsumer consumer = session.createConsumer(serverEventQueue);
			    receivedMessage = consumer.receive(); 
			    return receivedMessage;
			}
    	} catch (JMSException e) {
			throw new RequestException(e);
		}
		
    	throw new RequestException("Can NOT receive message for: " + event.getType());
    }
    
    
    private ResultMsg processMessage(Message message) throws RequestException {
    	
    	ResultMsg resultMsg = new ResultMsg("Request error", RequestResult.ERROR);
    	
    	try {
	    	if (message.getBooleanProperty(ENTER_ROOM_CONFIRMED)){
	            if (message instanceof TextMessage) {
	                TextMessage textMsg = (TextMessage) message;
	                resultMsg = new ResultMsg(textMsg.getText(), RequestResult.SUCCESS);
	            }
	    	}
    	} catch (JMSException e) {
			throw new RequestException(e);
		}
    	
    	return resultMsg;
    }
    
    
    @Override
    public void sendEvent(ChatEvent event) {

        if (event.getType() == EventType.ENTER_ROOM) {
            try {
                Destination destination = session.createQueue(EVENT_QUEUE_TO_SERVER);
                MessageProducer producer = session.createProducer(destination);
                TextMessage textMsg = session.createTextMessage(((EnterToRoomRequest) event).getRoom());
                textMsg.setBooleanProperty(ENTER_ROOM_REQUEST, true);
                log.debug("Sending request to server \n");
                producer.send(textMsg);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        } else if (event.getType() == EventType.MESSAGE) {
            try {
                Destination destination = session.createQueue(MESSAGE_QUEUE_TO_SERVER);
                MessageProducer producer = session.createProducer(destination);
                TextMessage textMsg = session.createTextMessage(((MessageEvent) event).getData());
                textMsg.setStringProperty(ROOM_NAME, ((MessageEvent) event).getRoom());
                textMsg.setBooleanProperty(MESSAGE_TO_SERVER, true);
                log.info("Sending message from client, room: " + textMsg.getStringProperty(ROOM_NAME) + "; Data: " + textMsg.getText());
                producer.send(textMsg);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void listenForEvent() {
        try {
            Destination serverEventQueue = session.createQueue(EVENT_QUEUE_TO_CLIENT);
            MessageConsumer consumer = session.createConsumer(serverEventQueue);
            consumer.setMessageListener(this);  //receive();
            log.info("Client is listening...");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message.getBooleanProperty(ENTER_ROOM_CONFIRMED)){
                if (message instanceof TextMessage) {
                    TextMessage textMsg = (TextMessage) message;
                    ResultMsg resultMsg = new ResultMsg(textMsg.getText(), RequestResult.SUCCESS);
                    //EnterToRoomRequest event = new EnterToRoomRequest(EventType.ENTER_ROOM, textMsg.getText());
                    try {
						responses.put((ResultMsg) resultMsg);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                    //messageListener.eventReceived(event);
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void setEventListener(ChatEventListener messageListener) {
        this.messageListener = messageListener;
    }
    
    @Override
    public ResultMsg getResponseWhenCame() throws InterruptedException {
        return this.responses.take();
    }

}
