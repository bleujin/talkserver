package net.ion.talk;

import net.ion.talk.engine.TestAccountManager;
import net.ion.talk.engine.TestEngineContext;
import net.ion.talk.engine.TestParameterMap;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllEngine extends TestCase {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Test All Engine");

		suite.addTestSuite(TestParameterMap.class);
		// result.addTestSuite(TestMailer.class) ;
		suite.addTestSuite(TestEngineContext.class);
		suite.addTestSuite(TestAccountManager.class);

		return suite;
	}
}
