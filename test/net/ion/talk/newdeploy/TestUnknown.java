package net.ion.talk.newdeploy;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.talk.engine.TestAccountManager;
import net.ion.talk.engine.TestParameterMap;
import net.ion.talk.engine.TestEngineContext;

public class TestUnknown extends TestCase {

	public static TestSuite suite() {
		TestSuite result = new TestSuite();
		result.addTestSuite(TestParameterMap.class);
		result.addTestSuite(TestEngineContext.class);
		result.addTestSuite(TestAccountManager.class);
		return result;
	}
}
