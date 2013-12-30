package net.ion.talk.let;

import net.ion.radon.client.IAradonRequest;
import org.restlet.Response;
import org.restlet.data.Method;

import java.net.InetAddress;

public class TestLoginLet extends TestBaseLet {

    @Override
	protected void setUp() throws Exception {
		super.setUp();
		tserver.startAradon();
	}
	
	public void testBasicAuth() throws Exception {
		IAradonRequest request = tserver.mockClient().fake().createRequest("/auth/login", "bleujin", "1234");
		assertEquals(401, request.handle(Method.GET).getStatus().getCode());
		tserver.verifier().addUser("bleujin", "1234") ;
		assertEquals(200, request.handle(Method.GET).getStatus().getCode());
	}

	public void testGetWebsocketURL() throws Exception {
		IAradonRequest request = tserver.mockClient().fake().createRequest("/auth/login", "emanon", "emanon");
		Response r = request.handle(Method.GET);
		assertEquals(true, r.getEntityAsText().startsWith("ws://"+ InetAddress.getLocalHost().getHostAddress()+":9000/websocket/emanon/")) ;
	}

}
