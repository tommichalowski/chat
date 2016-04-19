package com.gft.bench.exceptions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChatException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ChatException.class);
	
	public ChatException(String message, Throwable cause) {
		super(message, cause);
		if (log.isErrorEnabled()) {
			log.error(message, cause);
		}
	}
	
	public ChatException(String message) {
		super(message);
		if (log.isErrorEnabled()) {
			log.error(message);
		}
	}
	
	public ChatException(Throwable cause) {
		super(cause);
		if (log.isErrorEnabled()) {
			log.error(cause);
		}
	}
}
