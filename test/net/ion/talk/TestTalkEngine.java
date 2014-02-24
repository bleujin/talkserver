package net.ion.talk;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.ion.craken.node.ReadSession;
import net.ion.framework.util.Debug;
import net.ion.nradon.WebSocketConnection;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.handler.TalkHandler;

public class TestTalkEngine extends TestCase {

	private TalkEngine engine;
	WebSocketConnection bleujin = FakeWebSocketConnection.create("bleujin");

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.engine = TalkEngine.test();
		engine.registerHandler(new DummyHandler()) ;
	}
	
	@Override
	protected void tearDown() throws Exception {
        engine.onStop();
		super.tearDown();
	}
	
	public void testUse() throws Exception {
		engine.onOpen(bleujin) ;
		engine.onMessage(bleujin, "hello") ;
		engine.onClose(bleujin) ;
	}
	
	
	public void testConnectionManger() {
		engine.onOpen(bleujin) ;

		assertTrue(engine.connManger().findBy(bleujin) != null) ;
		assertTrue(engine.connManger().findBy("bleujin") != null) ;
        engine.onMessage(bleujin, "hello") ;
		engine.onClose(bleujin) ;
		assertTrue(engine.connManger().findBy(bleujin) == null) ;
		assertTrue(engine.connManger().findBy("bleujin") == null) ;
    }
	
}

class DummyHandler implements TalkHandler {

	@Override
	public void onClose(TalkEngine tengine, UserConnection uconn) {
		
		Assert.assertEquals("bleujin", uconn.id()) ;
	}

	@Override
	public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmessage) {
        Debug.line(tengine, uconn, rsession, tmessage);
        Assert.assertEquals("bleujin", uconn.id()) ;
	}

	@Override
	public Reason onConnected(TalkEngine tengine, UserConnection uconn) {
		Assert.assertEquals("bleujin", uconn.id()) ;
		return Reason.OK ;
	}

	@Override
	public void onEngineStart(TalkEngine tengine) {
			
	}

	@Override
	public void onEngineStop(TalkEngine tengine) {
		
	}
}
