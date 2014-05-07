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

	public void testHeartBeatOne() throws Exception {
		engine.heartBeat().delaySecond(1) ;
		engine.startEngine() ;
		engine.onOpen(bleujin);
		Thread.sleep(2000);
		assertEquals(false, bleujin.isClosed());
		Thread.sleep(1500);
		assertEquals(true, bleujin.isClosed());
	}

    public void testHeartBeatShake() throws Exception {
		engine.heartBeat().delaySecond(1) ;
		
		engine.startEngine() ;
        engine.onOpen(bleujin);
        Thread.sleep(1500);
        assertEquals(false, bleujin.isClosed());
        engine.onMessage(bleujin, "Hello");
        Thread.sleep(1500);
        assertEquals(false, bleujin.isClosed());
        Thread.sleep(2000);
        assertEquals(true, bleujin.isClosed());
    }


}