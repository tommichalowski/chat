package com.gft.bench.unitTest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.gft.bench.endpoints.Endpoint;
import com.gft.bench.events.ChatEvent;
import com.gft.bench.events.EnterToRoomRequest;
import com.gft.bench.events.EventType;
import com.gft.bench.server.Server;
import com.gft.bench.server.ServerImpl;

public class ServerImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testServerImpl() {
		fail("Not yet implemented");
	}

	@Test
	public void testEnterRoomEventReceived() {

		String roomName = "test-room";
		Endpoint chatEndpointMock = mock(Endpoint.class);

		Server server = new ServerImpl(chatEndpointMock);
		Server serverSpy = spy(server);
		
		ChatEvent event = new EnterToRoomRequest(EventType.ENTER_ROOM, roomName);
		serverSpy.eventReceived(event);
		
		verify(serverSpy, times(1)).addRoom(roomName);
		assertTrue("Room: " + roomName + "hasn't been created!", server.getRooms().contains(roomName));
		verify(chatEndpointMock, times(1)).sendEvent(any(ChatEvent.class));
	}

	@Test
	public void testStartServer() {
		fail("Not yet implemented");
	}

	@Test
	public void testStopServer() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRoomHistory() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRooms() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddRoom() {
		fail("Not yet implemented");
	}

}
