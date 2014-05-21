package net.ion.talk;

import java.util.concurrent.atomic.AtomicReference;

import net.ion.framework.util.Debug;
import net.ion.talk.TalkEngine;
import net.ion.talk.UserConnection;
import net.ion.talk.account.Account;
import net.ion.talk.account.AccountHandler;
import net.ion.talk.account.AccountManager;
import net.ion.talk.account.Account.Type;
import net.ion.talk.responsebuilder.TalkResponse;
import junit.framework.TestCase;

public class TestConnections extends TestCase {

	private TalkEngine tengine;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.tengine = TalkEngine.testCreate().init().startEngine().clearHandler();
	}

	@Override
	protected void tearDown() throws Exception {
		this.tengine.stopEngine();
		super.tearDown();
	}

	public void testConnectBefore() throws Exception {
		FakeWebSocketConnection b1 = FakeWebSocketConnection.create("bleujin");
		FakeWebSocketConnection b2 = FakeWebSocketConnection.create("bleujin");

		tengine.onOpen(b1);
		assertEquals(true, tengine.findConnection("bleujin").inner() == b1);

		tengine.onOpen(b2);
		tengine.onClose(b1); // by client called
		assertEquals(true, tengine.findConnection("bleujin").inner() == b2);
	}

	public void testSendMessage() throws Exception {
		FakeWebSocketConnection b1 = FakeWebSocketConnection.create("bleujin");
		tengine.onOpen(b1);

		tengine.findConnection("bleujin").sendMessage("Hello");
		assertEquals("Hello", b1.recentMsg());
	}

	public void testConnectDopple() throws Exception {
		FakeWebSocketConnection b1 = FakeWebSocketConnection.create("bleujin");
		FakeWebSocketConnection b2 = FakeWebSocketConnection.create("bleujin");

		tengine.onOpen(b1);
		tengine.onOpen(b2);

		tengine.findConnection("bleujin").sendMessage("Hello");

		assertEquals("Hello", b1.recentMsg());
		assertEquals("Hello", b2.recentMsg());
	}

	public void testProxy() throws Exception {
		FakeWebSocketConnection b1 = FakeWebSocketConnection.create("bleujin");
		tengine.onOpen(b1);
		AccountManager am = tengine.context().getAttributeObject(AccountManager.class.getCanonicalName(), AccountManager.class);
		
//		final AtomicReference<String> recevied = new AtomicReference<String>() ;
//		am.defineHandle(Type.PROXY, new AccountHandler() {
//			@Override
//			public Account create(AccountManager am, String userId, UserConnection uconn) {
//				return new Account("ryun", Type.PROXY) {
//					@Override
//					public void onMessage(String notifyId) {
//						recevied.set(notifyId);
//					}
//				};
//			}
//		});

		Account account = am.newAccount("bleujin");
		assertEquals(Type.PROXY, account.type());
	}
}
