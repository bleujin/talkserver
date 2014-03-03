package net.ion.talk;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.talk.account.TestAccount;
import net.ion.talk.bot.TestEchoBot;
import net.ion.talk.handler.TestAllHandler;
import net.ion.talk.let.TestAllLet;
import net.ion.talk.let.TestScriptExecLet;
import net.ion.talk.responsebuilder.TestAllResponseBuilder;

public class TestAllCrakenServer extends TestCase {

	public static TestSuite suite() {
		TestSuite result = new TestSuite();
		result.addTestSuite(TestParameterMap.class);
		result.addTestSuite(TestTalkEngine.class);
		result.addTestSuite(TestEchoBot.class);
		result.addTestSuite(TestAccount.class);
		result.addTest(TestAllHandler.suite());
		result.addTest(TestAllLet.suite());
		result.addTest(TestAllResponseBuilder.suite());

		return result;
	}

}
