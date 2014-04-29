package net.ion.talk.script;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

import org.restlet.representation.Representation;

import sun.org.mozilla.javascript.internal.NativeObject;
import sun.org.mozilla.javascript.internal.Scriptable;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.TestBaseCrud;
import net.ion.framework.db.Rows;
import net.ion.framework.db.bean.ResultSetHandler;
import net.ion.framework.rest.HTMLFormater;
import net.ion.framework.util.Debug;

public class TestTalkScript extends TestBaseCrud {
	private ScheduledExecutorService ses;
	private TalkScript ts;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.ses = Executors.newScheduledThreadPool(2);
		this.ts = TalkScript.create(session, ses);
		ts.readDir(new File("./script"));
	}

	public void testListPackage() throws Exception {
		Map<String, Object> pkgs = ts.packages();
		Debug.line(ts.fullFnNames());
	}

	
	public void testRunScript() throws Exception {
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/bleujin").property("name", "bleujin") ;
				wsession.pathBy("/hero").property("name", "hero") ;
				return null;
			}
		}) ;
		
		String script = "session.root().children().toAdRows('name') ;" ;
		Rows rows = ts.viewRows(session, script) ;
		
		Representation rep = rows.toHandle(new HTMLFormater()) ;
		Debug.line(rep.getText()); 
	}
	
	public void testDate() throws Exception {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine sengine = manager.getEngineByName("JavaScript");

		ScriptContext bindings = new SimpleScriptContext();
		bindings.setAttribute("session", session, ScriptContext.ENGINE_SCOPE);
		Object result = sengine.eval("new Date().getTime().toString();", bindings) ;
		Date d = new Date() ;
		d.setTime(Long.parseLong(result.toString())) ;
		Debug.line(new Date().getTime(), result, d) ; 
	}
}
