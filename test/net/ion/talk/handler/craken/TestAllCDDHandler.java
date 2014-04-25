package net.ion.talk.handler.craken;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllCDDHandler extends TestCase {

	public static TestSuite suite(){
		TestSuite result = new TestSuite("Test All CDD Handler") ;
		
		
		result.addTestSuite(TestNotifySendHandler.class);
		result.addTestSuite(TestTalkMessageHandler.class);
		result.addTestSuite(TestUserInAndOutRoomHandler.class);
		
		return result ;
	}
}
