package com.gft.bench.client.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.old.CmdLineTool;

public class DisplayImpl implements Display {

    @SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(CmdLineTool.class);
    private static Scanner scanner;
    private static PrintStream printer;

    
    public DisplayImpl(InputStream inputStream, PrintStream printStream) {
    	scanner = new Scanner(inputStream);
    	printer = printStream;
    }

    
    @Override
	public UIEvent handleInput() {
    	return handleInput(null);
    }
    
    
    @Override
    public UIEvent handleInput(UIEventType expectedEventType) {
    	
    	String str = scanner.nextLine();
    	
    	UIEvent uiEvent = new UIEvent();
    	uiEvent.message = str;
        
    	UIEventType userEventType = UIEventType.valueOf(str.toUpperCase()); //TODO: check 
        if (userEventType != null) {
        	uiEvent.eventType = userEventType;
        } else if (expectedEventType != null) {
        	uiEvent.eventType = expectedEventType;
        } else {
			uiEvent.eventType = UIEventType.MESSAGE;
		}

        return uiEvent;
    }

    
    //public void print(OutputStream output) {
    //public void print(UIEvent uiEvent) {
    @Override
	public void print(String message) {	
    	printer.println(message);
    }
	
}
