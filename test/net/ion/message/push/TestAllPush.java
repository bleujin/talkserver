package net.ion.message.push;

import net.ion.message.push.sender.APNSSenderTest;
import net.ion.message.push.sender.GCMSenderTest;
import net.ion.message.push.sender.GCMTest;
import net.ion.message.push.sender.JAVAPNSTest;
import net.ion.message.push.sender.SenderTest;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllPush extends TestCase {

	
	public static TestSuite suite(){
		TestSuite  suite = new TestSuite() ;
		
		suite.addTestSuite(APNSSenderTest.class);
		suite.addTestSuite(GCMSenderTest.class);
		suite.addTestSuite(GCMTest.class);
		suite.addTestSuite(JAVAPNSTest.class);
		suite.addTestSuite(SenderTest.class);
		
		return suite ;
	} 
}
