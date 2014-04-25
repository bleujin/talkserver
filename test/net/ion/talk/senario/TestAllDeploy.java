package net.ion.talk.senario;

import junit.framework.TestSuite;

public class TestAllDeploy extends TestSuite {

	public static TestSuite suite(){
		TestSuite result = new TestSuite("Test All Deploy") ;
	
		result.addTestSuite(TestConnectToServer.class);
		
		return result ;
	} 
	
}
