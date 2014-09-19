package net.ion.talk.handler.engine;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.talk.handler.craken.TestNotifySendHandler;
import net.ion.talk.handler.craken.TestTalkMessageHandler;
import net.ion.talk.handler.craken.TestUserInAndOutRoomHandler;
import net.ion.talk.script.TestTalkMessage;
import net.ion.talk.script.TestWhisperMessage;


public class TestAllTalkHandler extends TestCase {

	public static TestSuite suite() {
		TestSuite result = new TestSuite("Test All Talk Handler");

//        result.addTestSuite(TestInitScriptHandler.class);
        result.addTestSuite(TestServerHandler.class);
		result.addTestSuite(TestUserConnectionHandler.class);
		result.addTestSuite(TestWebSocketTalkMessageHandler.class);

		result.addTestSuite(TestTalkMessage.class);
		result.addTestSuite(TestWhisperMessage.class);
		return result;
	}
}
