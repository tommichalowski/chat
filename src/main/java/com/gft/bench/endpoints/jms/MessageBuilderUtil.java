package com.gft.bench.endpoints.jms;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.springframework.util.SerializationUtils;

public class MessageBuilderUtil {

	
	public static <T extends Serializable> Message buildMessage(T data) throws JMSException {
		
		byte[] serializedData = SerializationUtils.serialize(data);
		
		ActiveMQBytesMessage message = new ActiveMQBytesMessage();
		message.writeBytes(serializedData);
		
		return message;
	}
	
	
	public static <T extends Serializable> Message buildMessage(T data, Destination replyTo, String correlationId) throws JMSException {
		
		byte[] serializedData = SerializationUtils.serialize(data);
		
		ActiveMQBytesMessage message = new ActiveMQBytesMessage();
		message.writeBytes(serializedData);
		message.setJMSReplyTo(replyTo);
		message.setCorrelationId(correlationId);
		
		return message;
	}
	
	
	public static <T extends Serializable> T buildEvent(Message message) throws JMSException {
		
		if (message instanceof BytesMessage) {
			BytesMessage msg = (BytesMessage) message;
			byte[] byteArr = new byte[(int) msg.getBodyLength()];
			msg.readBytes(byteArr);
			
			@SuppressWarnings("unchecked")
			T event = (T) SerializationUtils.deserialize(byteArr);
			return event;
		}
		
		return null;	
	}

}
