package net.ion.talk.newdeploy;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.talk.TestParameterMap;
import net.ion.talk.TestTalkEngine;
import net.ion.talk.account.TestAccountManager;

public class TestUnknown extends TestCase {

	public static TestSuite suite() {
		TestSuite result = new TestSuite();
		result.addTestSuite(TestParameterMap.class);
		result.addTestSuite(TestTalkEngine.class);
		result.addTestSuite(TestAccountManager.class);
		return result;
	}
}
