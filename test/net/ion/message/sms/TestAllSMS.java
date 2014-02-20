package net.ion.message.sms;

import net.ion.message.sms.message.PhoneMessageTest;
import net.ion.message.sms.sender.SMSSenderTest;
import net.ion.message.sms.util.MessageIDTest;
import net.ion.message.sms.util.SimpleJavaTest;
import net.ion.message.sms.util.TestAradonServer;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllSMS extends TestCase {

	public static TestSuite suite(){
		TestSuite suite = new TestSuite() ;
		
		suite.addTestSuite(PhoneMessageTest.class);
		suite.addTestSuite(SMSSenderTest.class);
		suite.addTestSuite(MessageIDTest.class);
		suite.addTestSuite(SimpleJavaTest.class);
		
		return suite ;
	} 
}
