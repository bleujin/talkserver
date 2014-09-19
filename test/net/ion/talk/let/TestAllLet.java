package net.ion.talk.let;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.talk.handler.engine.TestServerHandler;
import net.ion.talk.senario.TestConnectToServer;

public class TestAllLet extends TestCase {

	public static TestSuite suite() {
		TestSuite result = new TestSuite("Test All Let");

		result.addTestSuite(TestLoginLet.class);
		result.addTestSuite(TestScriptExecLet.class);

		return result;
	}
}
