package net.ion.talk.responsebuilder;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllResponseBuilder extends TestCase {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();

		suite.addTestSuite(TestExpression.class);
		suite.addTestSuite(TestMakeResponse.class);
		suite.addTestSuite(TestResponseWrapper.class);

		return suite;
	}
}
