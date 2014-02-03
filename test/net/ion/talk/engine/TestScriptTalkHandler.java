package net.ion.talk.engine;

import net.ion.framework.util.Debug;
import net.ion.talk.FakeConnection;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkMessage;
import net.ion.talk.let.ScriptTalkHandler;
import junit.framework.TestCase;

public class TestScriptTalkHandler extends TestCase {

	private TalkEngine engine;
	FakeConnection bleujin = FakeConnection.create("bleujin");

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.engine = TalkEngine.test() ;
		final ScriptTalkHandler handler = new ScriptTalkHandler();
		engine.registerHandler(handler) ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	public void testRunScript() throws Exception {
		String script = "session.tranSync(function(wsession){" +
		"	wsession.pathBy('/bleujin').property('name', params.asString('name')).property('age', params.asInt('age'));" +
		"}) ;" +
		"" +
		"session.pathBy('/bleujin').toRows('name, age').toString();" ;
		
		TalkMessage tmessage = TalkMessage.fromScript(script);
		
		engine.onOpen(bleujin) ;
		engine.onMessage(bleujin, tmessage.toPlainMessage()) ;
		
		String recentMsg = bleujin.recentMsg() ;
		Debug.line(recentMsg) ;
	}
	
}
