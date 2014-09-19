package net.ion.talk.toonweb;

import junit.framework.TestCase;
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.client.StubServer;

public class TestToonWebResource extends TestCase {

	public void testIndexHtml() throws Exception {
		StubServer ss = StubServer.create(ToonWebResourceLet.class) ;
		StubHttpResponse response = ss.request("/toonweb/").get() ;
		
		assertTrue(response.contentsString().length() > 0);
	}
}
