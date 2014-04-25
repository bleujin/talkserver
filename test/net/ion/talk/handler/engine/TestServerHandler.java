package net.ion.talk.handler.engine;

import java.net.InetAddress;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.talk.TalkEngine;

public class TestServerHandler extends TestCase {

	private TalkEngine engine;

	@Override
	protected void setUp() throws Exception {
		this.engine = TalkEngine.testCreate().registerHandler(ServerHandler.test()) ;
		engine.startEngine() ;
	}
	
	@Override
	public void tearDown() throws Exception {
		engine.stopEngine(); 
		super.tearDown();
	}

	public void testIsOnServer() throws Exception {

		ServerHandler serverHandler = engine.handler(ServerHandler.class);
		assertTrue(serverHandler.registered(InetAddress.getLocalHost().getHostName()));
	}

	public void xtestIPAddress() throws Exception {
		ServerHandler sh = ServerHandler.test();
		assertEquals("61.250.201.157", sh.serverHost());

		InetAddress address = InetAddress.getLocalHost();
		Debug.line(address.getHostName());
	}

}
