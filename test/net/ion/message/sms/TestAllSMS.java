package net.ion.message.sms;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.message.sms.sender.SMSSenderTest;
import net.ion.message.sms.util.SimpleJavaTest;

public class TestAllSMS extends TestCase {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();

		suite.addTestSuite(SMSSenderTest.class);
		suite.addTestSuite(SimpleJavaTest.class);

		return suite;
	}
}
