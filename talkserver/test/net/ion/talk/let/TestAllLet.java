package net.ion.talk.let;

import net.ion.bleujin.template.TestScriptTemplate;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllLet extends TestCase {

	public static TestSuite suite(){
		TestSuite result = new TestSuite() ;
		
		result.addTestSuite(TestLoginLet.class) ;
		result.addTestSuite(TestLoginWebSocket.class) ;
		
		result.addTestSuite(TestScriptLet.class) ;
		result.addTestSuite(TestServerHandler.class) ;
		result.addTestSuite(TestScriptExecLet.class) ;
		
		
		return result ;
	}
}
