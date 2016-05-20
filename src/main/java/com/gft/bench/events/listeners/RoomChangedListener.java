package com.gft.bench.events.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.client.ui.Display;
import com.gft.bench.events.EventListener;
import com.gft.bench.events.notification.RoomChanged;

public class RoomChangedListener implements EventListener<RoomChanged> {

	private static final Log log = LogFactory.getLog(RoomChangedListener.class);
	private Display display;
	
	@Override
	public void onEvent(RoomChanged event) {
		log.info("\n\nIn RoomChangedListener");
		if (display != null) {
			display.print("RoomChangedListener");
		}
	}

}
