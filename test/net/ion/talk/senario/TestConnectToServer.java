package net.ion.talk.senario;

import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.SetUtil;
import net.ion.nradon.Radon;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Realm;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.Realm.RealmBuilder;
import net.ion.radon.aclient.websocket.WebSocket;
import net.ion.radon.aclient.websocket.WebSocketListener;
import net.ion.radon.aclient.websocket.WebSocketTextListener;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.Configuration;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.radon.core.security.ChallengeAuthenticator;
import net.ion.talk.TalkEngine;
import net.ion.talk.ToonServer;
import net.ion.talk.let.EchoHandler;

public class TestConnectToServer extends TestCase {

	private ToonServer tserver;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.tserver = ToonServer.testWithLoginLet();
		tserver.startRadon().talkEngine().registerHandler(new EchoHandler());

		tserver.readSession().tranSync(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/servers/" + wsession.workspace().repository()).property("host", InetAddress.getLocalHost().getHostAddress()).property("port", 9000);
				return null;
			}
		});
	}
	
	@Override
	public void tearDown() throws Exception {
		tserver.stop(); 
		super.tearDown();
	}

	public void testLogin() throws Exception {
		NewClient nc = tserver.mockClient().real();
		Realm realm = new RealmBuilder().setPrincipal("emanon").setPassword("emanon").build();
		Response response = nc.prepareGet("http://" + InetAddress.getLocalHost().getHostAddress() + ":9000/auth/login").setRealm(realm).execute().get();

		String wsaddress = response.getTextBody();

		final AtomicReference<String> received = new AtomicReference<String>();
		final CountDownLatch count = new CountDownLatch(1);
		WebSocket websocket = nc.createWebSocket(wsaddress, new WebSocketTextListener() {
			@Override
			public void onClose(WebSocket arg0) {
			}

			@Override
			public void onError(Throwable arg0) {
			}

			@Override
			public void onOpen(WebSocket arg0) {
			}

			@Override
			public void onFragment(String arg0, boolean arg1) {

			}

			@Override
			public void onMessage(String message) {
				received.set(message);
				count.countDown();
			}
		});

		websocket.sendTextMessage("Hello");
		count.await();

		assertEquals("Hello", received.get());

	}

}
