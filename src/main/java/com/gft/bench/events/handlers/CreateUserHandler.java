package com.gft.bench.events.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.endpoints.RequestHandler;
import com.gft.bench.events.business.CreateUserEvent;
import com.gft.bench.server.Server;

public class CreateUserHandler implements RequestHandler<CreateUserEvent, CreateUserEvent> {

	private static final Log log = LogFactory.getLog(CreateUserHandler.class);
	
	Server server;
	
	public CreateUserHandler(Server server) {
		this.server = server;
	}
	
	
	@Override
	public CreateUserEvent onMessage(CreateUserEvent request) {
		
		log.info("Creating user: " + request.userName);
		
		boolean addedNewUser = server.getUsersLogins().add(request.userName);
		if (addedNewUser) {
			return new CreateUserEvent(request.userName);
		} else {
			return new CreateUserEvent("Failed");
		}   		
	}
	
}
