package net.ion.talk.engine;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.ion.craken.node.ReadSession;
import net.ion.message.sms.sender.SMSSender;
import net.ion.radon.aclient.NewClient;
import net.ion.talk.FakeWebSocketConnection;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;
import net.ion.talk.account.AccountManager;
import net.ion.talk.bot.BotManager;
import net.ion.talk.handler.TalkHandler;

public class TestEngineContext extends TestCase {

	private TalkEngine engine;
    FakeWebSocketConnection bleujin = FakeWebSocketConnection.create("bleujin");

	@Override
	protected void setUp() throws Exception {
		this.engine = TalkEngine.testCreate().registerHandler(new DummyHandler());
	}

	@Override
	protected void tearDown() throws Exception {
        engine.readSession().workspace().repository().shutdown();
        engine.stopEngine();
	}

	public void testContext() throws Exception {
		assertNotNull(engine.contextAttribute(NewClient.class)) ;
		assertNotNull(engine.contextAttribute(SMSSender.class)) ;
		assertNotNull(engine.contextAttribute(BotManager.class)) ;
		
		engine.startEngine() ;
		assertNotNull(engine.contextAttribute(AccountManager.class)) ;
	}
	
	public void testUse() throws Exception {
		engine.onOpen(bleujin);
		engine.onMessage(bleujin, "hello");
		engine.onClose(bleujin);
	}

	public void testConnectionManger() throws InterruptedException {
		engine.onOpen(bleujin);

		assertTrue(engine.findConnection("bleujin") != UserConnection.NOTFOUND);
		engine.onMessage(bleujin, "hello");
		engine.onClose(bleujin);
		assertTrue(engine.findConnection("bleujin") == UserConnection.NOTFOUND);
//        new InfinityThread().startNJoin();
	}

}

class DummyHandler implements TalkHandler {

	@Override
	public void onClose(TalkEngine tengine, UserConnection uconn) {

		Assert.assertEquals("bleujin", uconn.id());
	}

	@Override
	public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmessage) {
//		Debug.line(tengine, uconn, rsession, tmessage);
//		Assert.assertEquals("bleujin", uconn.id());
	}

	@Override
	public Reason onConnected(TalkEngine tengine, UserConnection uconn) {
		Assert.assertEquals("bleujin", uconn.id());
		return Reason.OK;
	}

	@Override
	public void onEngineStart(TalkEngine tengine) {

	}

	@Override
	public void onEngineStop(TalkEngine tengine) {

	}
}
