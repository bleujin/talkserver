package net.ion.talk.script;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.ion.craken.node.crud.TestBaseCrud;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.talk.ParameterMap;
import net.ion.talk.let.ScriptExecLet;

public class TestCaseScript extends TestBaseCrud {
	private ScheduledExecutorService ses;
	private TalkScript ts;

	
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.ses = Executors.newScheduledThreadPool(2) ;
		this.ts = TalkScript.create(session, ses) ;
		ts.readDir(new File("./script"), true) ;
	}
	
	public void testWhenNullReturnInExecScript() throws Exception {
		ParameterMap params = ParameterMap.BLANK ;

		JsonObject rep = ts.callFn("test/refTos", params, ScriptExecLet.jsonHandler()).getAsJsonObject() ;
		Debug.line(rep, rep);
	}
	
	public void testWhenStringArray() throws Exception {
		ParameterMap params = ParameterMap.BLANK ;
		
		Object obj = ts.callFn("test/refTos", params) ;
		assertEquals(true, obj == null);
	}
	
	public void xtestIfLangErrorWhenReloaded() throws Exception {
		ParameterMap params = ParameterMap.BLANK ;
		Executors.newFixedThreadPool(3) ;
		
		while(true){
			Thread.sleep(1000);
			Debug.line(ts.callFn("test/langError", params));
		}
	}
	
	public void testThreadPool() throws Exception {
		
		ExecutorService es = Executors.newScheduledThreadPool(2) ;
		Callable<Void> call = new Callable<Void>() {
			@Override
			public Void call() {
				int i = 0 ;
				boolean loop = true ;
				while(loop){
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					i++ ;
				}
				return null;
			}
		};

		es.submit(call) ;
		es.submit(call) ;
		es.submit(call) ;
		
		es.shutdown() ;
		es.awaitTermination(2, TimeUnit.SECONDS) ;
		es.shutdownNow() ;
		
	}
	
	
}
