package net.ion.talk;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.ion.craken.node.ReadSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.WebSocketConnection;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.handler.TalkHandler;

public class TestTalkEngine extends TestCase {

	private TalkEngine engine;
    FakeWebSocketConnection bleujin = FakeWebSocketConnection.create("bleujin");

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.engine = TalkEngine.test();
		engine.registerHandler(new DummyHandler());
	}

	@Override
	protected void tearDown() throws Exception {
        engine.readSession().workspace().repository().shutdown();
        engine.onStop();
		super.tearDown();
	}

	public void testUse() throws Exception {
		engine.onOpen(bleujin);
		engine.onMessage(bleujin, "hello");
		engine.onClose(bleujin);
	}

    public void testHeartBeatOne() throws InterruptedException {

        TalkEngine.HEARTBEAT_WATING = 1000;
        TalkEngine.HEARTBEAT_KILLING = 2000;
        engine.onOpen(bleujin);
        Thread.sleep(1500);
        assertEquals(false, bleujin.isClosed());
        Thread.sleep(1000);
        assertEquals(true, bleujin.isClosed());
    }
//
//    public void testHeartBeatTwo() throws InterruptedException {
//
//        TalkEngine.HEARTBEAT_WATING = 1;
//        TalkEngine.HEARTBEAT_KILLING = 1;
//        engine.onOpen(bleujin);
//
//        for(int i=0;i<10000000;i++){
//            engine.onMessage(bleujin, "hello");
//        }
//        Debug.line("haha");
//        Thread.sleep(1500);
//        assertEquals(false, bleujin.isClosed());
//        Thread.sleep(1000);
//        assertEquals(true, bleujin.isClosed());
//    }

    public void testHeartBeatShake() throws InterruptedException {

        TalkEngine.HEARTBEAT_WATING = 1000;
        TalkEngine.HEARTBEAT_KILLING = 2000;
        engine.onOpen(bleujin);
        Thread.sleep(1500);
        assertEquals(false, bleujin.isClosed());
        engine.onMessage(bleujin, "Hello");
        Thread.sleep(1500);
        assertEquals(false, bleujin.isClosed());
        Thread.sleep(1000);
        assertEquals(true, bleujin.isClosed());
    }


	public void testConnectionManger() throws InterruptedException {
		engine.onOpen(bleujin);

		assertTrue(engine.connManger().findBy(bleujin) != null);
		assertTrue(engine.connManger().findBy("bleujin") != null);
		engine.onMessage(bleujin, "hello");
		engine.onClose(bleujin);
		assertTrue(engine.connManger().findBy(bleujin) == null);
		assertTrue(engine.connManger().findBy("bleujin") == null);
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
