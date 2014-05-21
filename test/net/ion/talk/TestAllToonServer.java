package net.ion.talk;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.talk.bot.TestAllBot;
import net.ion.talk.engine.TestAccountManager;
import net.ion.talk.engine.TestParameterMap;
import net.ion.talk.engine.TestEngineContext;
import net.ion.talk.handler.craken.TestAllCDDHandler;
import net.ion.talk.handler.engine.TestAllTalkHandler;
import net.ion.talk.let.TestAllLet;
import net.ion.talk.responsebuilder.TestAllResponseBuilder;
import net.ion.talk.senario.TestAllDeploy;

public class TestAllToonServer extends TestCase {

	public static TestSuite suite(){
		TestSuite result = new TestSuite("Test All") ;
        result.addTest(TestAllEngine.suite());
        
		result.addTest(TestAllResponseBuilder.suite());
        result.addTest(TestAllLet.suite());

        result.addTest(TestAllCDDHandler.suite());
        result.addTest(TestAllTalkHandler.suite());
        
        result.addTest(TestAllBot.suite());
        result.addTest(TestAllDeploy.suite());

		return result;
	}

}
