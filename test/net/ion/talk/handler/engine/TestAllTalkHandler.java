package net.ion.talk.handler.engine;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.talk.handler.craken.TestNotifySendHandler;
import net.ion.talk.handler.craken.TestTalkMessageHandler;
import net.ion.talk.handler.craken.TestUserInAndOutRoomHandler;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 24. Time: 오후 6:29 To change this template use File | Settings | File Templates.
 */
public class TestAllTalkHandler extends TestCase {

	public static TestSuite suite() {
		TestSuite result = new TestSuite("Test All Talk Handler");

//        result.addTestSuite(TestInitScriptHandler.class);
        result.addTestSuite(TestServerHandler.class);
		result.addTestSuite(TestUserConnectionHandler.class);
		result.addTestSuite(TestWebSocketTalkMessageHandler.class);

		return result;
	}
}
