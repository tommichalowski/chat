package com.gft.bench.events;

import java.io.Serializable;

import javax.jms.Destination;

//public class Envelope<T> implements Serializable {
public class Envelope implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//public T data;
	public Destination replyTo;
	public String eventId;
	
//	public class Envelope
//    {
//        public string CorrelationId { get; set; }
//        public string SendTo { get; set; }
//        public string ReplyTo { get; set; }
//        public byte[] Body { get; set; }
//        public string BodyType { get; set; }
//    }
}
