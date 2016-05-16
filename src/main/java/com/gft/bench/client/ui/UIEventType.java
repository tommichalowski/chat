package com.gft.bench.client.ui;

/**
 * Created by tzms on 3/25/2016.
 */
public enum UIEventType {
    CREATE_USER("-CREATE_USER"), ENTER_ROOM("-ENTER_ROOM"), EXIT_ROOM("-EXIT_ROOM"), CLOSE_APP("-CLOSE_APP"),
    MESSAGE("-MESSAGE");
    
    String type;
    
    private UIEventType(String type) {
		this.type = type;
	}
    
}
