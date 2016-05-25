package com.gft.bench.events;

import java.io.Serializable;

import javax.jms.Destination;

//public class Envelope<T> implements Serializable {
public class Envelope implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public byte[] data;
	public Destination replyTo;
	public String eventId;
	
//        public string correlationId 
//        public string sendTo 
//        public byte[] body 
//        public string bodyType 
}
