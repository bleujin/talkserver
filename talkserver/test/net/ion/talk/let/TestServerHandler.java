package net.ion.talk.let;

import java.net.InetAddress;

import junit.framework.TestCase;
import net.ion.craken.node.ReadSession;
import net.ion.framework.util.Debug;
import net.ion.radon.core.context.OnEventObject.AradonEvent;
import net.ion.talk.TalkEngine;
import net.ion.talk.ToonServer;

public class TestServerHandler extends TestCase {

	private ToonServer tserver;
	private ReadSession session;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.tserver = ToonServer.testWithLoginLet().start() ;
	}
	
	
	public void testIsOnServer() throws Exception {
		ServerHandler serverHandler = tserver.talkEngine().handler(ServerHandler.class); 
		assertTrue(serverHandler.registered(InetAddress.getLocalHost().getHostName())) ;

		tserver.talkEngine().onStop() ;
		
		
		
		assertFalse(serverHandler.registered(InetAddress.getLocalHost().getHostName())) ;
	}

	@Override
	protected void tearDown() throws Exception {
		tserver.stop();
		super.tearDown();
	}
	
	public void xtestIPAddress() throws Exception {
		ServerHandler sh = ServerHandler.test();
		assertEquals("61.250.201.157", sh.serverHost()) ;
		
		InetAddress address = InetAddress.getLocalHost();
		Debug.line(address.getHostName()) ;
	}
	
}
