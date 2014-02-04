package net.ion.talk.let;

import java.net.InetAddress;

import junit.framework.TestCase;
import net.ion.craken.node.ReadSession;
import net.ion.framework.util.Debug;
import net.ion.radon.core.context.OnEventObject.AradonEvent;
import net.ion.talk.TalkEngine;
import net.ion.talk.ToonServer;

public class TestServerHandler extends TestBaseLet {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tserver.startRadon() ;
	}
	
	
	public void testIsOnServer() throws Exception {
		ServerHandler serverHandler = tserver.talkEngine().handler(ServerHandler.class);
		assertTrue(serverHandler.registered(InetAddress.getLocalHost().getHostName())) ;

		tserver.talkEngine().onStop() ;
		assertFalse(serverHandler.registered(InetAddress.getLocalHost().getHostName())) ;
	}
	
	public void xtestIPAddress() throws Exception {
		ServerHandler sh = ServerHandler.test();
		assertEquals("61.250.201.157", sh.serverHost()) ;
		
		InetAddress address = InetAddress.getLocalHost();
		Debug.line(address.getHostName()) ;
	}

    @Override
    public void tearDown() throws Exception {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
