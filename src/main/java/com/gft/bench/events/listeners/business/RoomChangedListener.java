package com.gft.bench.events.listeners.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.client.ui.Display;
import com.gft.bench.events.EventListener;
import com.gft.bench.events.business.RoomChangedEvent;

public class RoomChangedListener implements EventListener<RoomChangedEvent> {

	private static final Log log = LogFactory.getLog(RoomChangedListener.class);
	private Display display;
	
	@Override
	public void onEvent(RoomChangedEvent event) {
		log.info("\n\nIn RoomChangedListener");
		if (display != null) {
			display.print("RoomChangedListener");
		}
	}

}
