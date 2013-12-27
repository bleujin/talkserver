package net.ion.talk.let;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.client.IAradonRequest;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.Configuration;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.radon.core.security.ChallengeAuthenticator;
import net.ion.talk.TalkEngine;
import net.ion.talk.ToonServer;

import org.restlet.Response;
import org.restlet.data.Method;

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
		assertEquals(true, r.getEntityAsText().startsWith("ws://61.250.201.157:9000/websocket/emanon/")) ;
	}

}
