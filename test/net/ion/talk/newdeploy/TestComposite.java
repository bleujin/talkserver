package net.ion.talk.newdeploy;

import junit.framework.Assert;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.craken.node.ReadSession;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;
import net.ion.talk.engine.TestAccountManager;
import net.ion.talk.handler.TalkHandler;

public class TestComposite extends TestCase {

	public static TestSuite suite() {
		TestSuite result = new TestSuite();

		result.addTestSuite(TestHeart.class);
		result.addTestSuite(TestAccountManager.class);

		return result;
	}

}

class DummyHandler implements TalkHandler {

	@Override
	public void onClose(TalkEngine tengine, UserConnection uconn) {

		Assert.assertEquals("bleujin", uconn.id());
	}

	@Override
	public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmessage) {
		// Debug.line(tengine, uconn, rsession, tmessage);
		// Assert.assertEquals("bleujin", uconn.id());
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