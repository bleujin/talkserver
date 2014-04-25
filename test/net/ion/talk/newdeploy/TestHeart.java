package net.ion.talk.newdeploy;

import junit.framework.TestCase;
import net.ion.talk.FakeWebSocketConnection;
import net.ion.talk.TalkEngine;

public class TestHeart extends TestCase {

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


}