package com.gft.bench.old;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;

public class ChatClientOld implements MessageListener {

    private static final Log log = LogFactory.getLog(ChatClientOld.class);
    private static final String EXTERNAL_BROKER_URL = "tcp://localhost:61616";

    private TopicConnection topicConnection = null;
	private TopicSession publisherSession = null;
    private TopicPublisher publisher = null;


    public ChatClientOld() {

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(EXTERNAL_BROKER_URL);

        try {
            topicConnection = connectionFactory.createTopicConnection();
            topicConnection.start();

            topicConnection.setExceptionListener(exception -> log.error("Exception on listening"));
        } catch (JMSException e) {
            log.error(e.getStackTrace());
        }
    }

    public void runPublisher(String topicName) {

        try {
            publisherSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = publisherSession.createTopic(topicName);
            publisher = publisherSession.createPublisher(topic);

        } catch (JMSException e) {
            log.error(e.getStackTrace());
        }
    }

    public void runSubscriber(String topicName) {
        try {
            TopicSession subscriberSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = subscriberSession.createTopic(topicName);

            TopicSubscriber subscriber = subscriberSession.createSubscriber(topic);
            subscriber.setMessageListener(this);

        } catch (JMSException e) {
            log.error(e.getStackTrace());
        }
    }

    /* Receive Messages From Topic Subscriber */
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            log.info("Message received: " + text);
        } catch (JMSException jmse) {
            log.error(jmse.getStackTrace());
        }
    }

    /* Create and Send Message Using Publisher */
    public void writeMessage(String text) throws JMSException {
        TextMessage message = publisherSession.createTextMessage(text.trim());
        publisher.publish(message);
    }

    /* Close the JMS Connection */
    public void close() throws JMSException {
        topicConnection.close();
    }

    public void setPublisherSession(TopicSession publisherSession) {
        this.publisherSession = publisherSession;
    }

    public void setPublisher(TopicPublisher publisher) {
        this.publisher = publisher;
    }

    
    public TopicConnection getTopicConnection() {
		return topicConnection;
	}

	public TopicSession getPublisherSession() {
		return publisherSession;
	}

	public TopicPublisher getPublisher() {
		return publisher;
	}

//    public void runBroker() {
//
//        // configure the broker
//        try {
//            BrokerService broker = new BrokerService();
//            broker.setBrokerId("AMQ-BROKER-TEST");
//            broker.setDeleteAllMessagesOnStartup(true);
//            broker.addConnector(BROKER_URL);
//            broker.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
