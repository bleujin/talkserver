package net.ion.talk;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.talk.account.TestAccountManager;
import net.ion.talk.bot.TestAllBot;
import net.ion.talk.handler.TestAllHandler;
import net.ion.talk.let.TestAllLet;
import net.ion.talk.responsebuilder.TestAllResponseBuilder;

public class TestAllToonServer extends TestCase {

	public static TestSuite suite(){
		TestSuite result = new TestSuite("Test All") ;
        result.addTestSuite(TestParameterMap.class) ;
//        result.addTestSuite(TestMailer.class) ;
		result.addTestSuite(TestTalkEngine.class) ;
        result.addTestSuite(TestAccountManager.class);
        
        result.addTest(TestAllHandler.suite());
        result.addTest(TestAllBot.suite());
        result.addTest(TestAllLet.suite());
		result.addTest(TestAllResponseBuilder.suite());

		return result;
	}

}
