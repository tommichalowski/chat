package com.gft.bench.client.ui;

public interface Display {

	UIEvent handleInput();

	UIEvent handleInput(UIEventType expectedEventType);
	
	void print(String message);
}