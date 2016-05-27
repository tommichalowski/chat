package com.gft.bench.endpoints;


public interface RequestHandler <TRequest, TResponse> {
	
 	 TResponse onMessage(TRequest request);
 }