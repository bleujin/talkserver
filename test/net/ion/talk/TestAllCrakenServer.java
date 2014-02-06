package net.ion.talk;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.talk.let.TestScriptExecLet;

public class TestAllCrakenServer extends TestCase {

	
	public static TestSuite suite(){
		TestSuite result = new TestSuite() ;
		result.addTestSuite(TestParameterMap.class) ;
		result.addTestSuite(TestTalkEngine.class) ;

		result.addTestSuite(TestScriptExecLet.class) ;
		return result ;
	}
}
