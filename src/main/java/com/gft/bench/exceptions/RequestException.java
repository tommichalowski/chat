package com.gft.bench.exceptions;

import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RequestException extends JMSException {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(RequestException.class);
	
	public RequestException(JMSException ex) {
		super(ex.getMessage());
		
		if (log.isErrorEnabled()) {
			log.error(ex.getMessage(), this);
		}
	}
	
	public RequestException(String message) {
		super(message);
		if (log.isErrorEnabled()) {
			log.error(message);
		}
	}
	
}
