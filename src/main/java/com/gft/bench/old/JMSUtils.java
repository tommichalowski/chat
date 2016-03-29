package com.gft.bench.old;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.TopicConnection;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JMSUtils {

	private static final Log log = LogFactory.getLog(JMSUtils.class);
	private static final JMSProvider DEFAULT_JMS_PROVIDER = JMSProvider.ACTIVE_MQ;
	public static final String DEFAULT_BROKER_URL = "tcp://localhost:61616";
	
	ConnectionFactory connectionFactory = null;
	private Map<String, Connection> connectionsMap = new HashMap<>();
	//private TopicConnection topicConnection = null;
	
	
	public ConnectionFactory getConnectionFactory() throws JMSException {
		return getConnectionFactory(DEFAULT_JMS_PROVIDER, DEFAULT_BROKER_URL);
	}
	
	public ConnectionFactory getConnectionFactory(JMSProvider jMSProvider, String brokerUrl) throws JMSException {
		
		switch (jMSProvider) {
			case ACTIVE_MQ:
				return new ActiveMQConnectionFactory(brokerUrl);
			default:
				throw new JMSException("No such JMS provider!");
		}
	}
	
	public enum JMSProvider {
		ACTIVE_MQ;
	}
	
	
	public Connection getTopicConnection() {
		return getTopicConnection(DEFAULT_JMS_PROVIDER, DEFAULT_BROKER_URL);
	}
	
	public Connection getTopicConnection(JMSProvider jMSProvider, String brokerUrl) {

		if (connectionsMap.containsKey(brokerUrl)) {
			return connectionsMap.get(brokerUrl);
		}
		
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        TopicConnection topicConnection = null;
        
        try {
            topicConnection = connectionFactory.createTopicConnection();
            topicConnection.start();
            connectionsMap.put(brokerUrl, topicConnection);
            
            topicConnection.setExceptionListener(exception -> log.error("Exception on listening"));
        } catch (JMSException e) {
            log.error(e.getStackTrace());
        }
        
        return topicConnection;
    }
	
	
}
