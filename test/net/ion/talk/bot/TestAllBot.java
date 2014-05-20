package net.ion.talk.bot;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.talk.handler.engine.TestServerHandler;
import net.ion.talk.let.*;

public class TestAllBot extends TestCase {

	public static TestSuite suite() {
		TestSuite result = new TestSuite("Test All Bot");

//        result.addTestSuite(TestOldBBot.class);
//        result.addTestSuite(TestOldChatBot.class);

		result.addTestSuite(TestEchoBot.class);
        result.addTestSuite(TestSimCall.class);
        result.addTestSuite(TestSimSimIBot.class);

		return result;
	}
}
