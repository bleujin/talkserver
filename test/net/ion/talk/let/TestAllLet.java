package net.ion.talk.let;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.talk.handler.engine.TestServerHandler;

public class TestAllLet extends TestCase {

	public static TestSuite suite(){
		TestSuite result = new TestSuite() ;
		
		result.addTestSuite(TestLoginLet.class) ;
		result.addTestSuite(TestLoginWebSocket.class) ;
        result.addTestSuite(TestScriptEditLet.class) ;
        result.addTestSuite(TestScriptExecLet.class) ;
		result.addTestSuite(TestServerHandler.class) ;
		result.addTestSuite(TestScriptExecLet.class) ;
        result.addTestSuite(TestStaticFileLet.class);
        result.addTestSuite(TestEmbedBotLet.class);
		
		
		return result ;
	}
}
