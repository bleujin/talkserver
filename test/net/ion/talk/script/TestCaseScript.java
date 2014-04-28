package net.ion.talk.script;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import net.ion.craken.node.crud.TestBaseCrud;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectId;
import net.ion.radon.core.let.MultiValueMap;
import net.ion.radon.core.representation.JsonObjectRepresentation;
import net.ion.talk.ParameterMap;
import net.ion.talk.responsebuilder.TalkResponseBuilder;

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
		final String scriptId = new ObjectId().toString() ;
		final String returnType = "json" ;
		
		Representation rep = ts.callFn("test/refTos", params, new ScriptSuccessHandler<Representation>() {
			@Override
			public Representation success(Object result) {
		        if ("json".equals(returnType)){
		        	return new JsonObjectRepresentation(TalkResponseBuilder.makeResponse(scriptId, result));
		        } else if("string".equals(returnType)) {
		        	return new StringRepresentation(TalkResponseBuilder.makeResponse(scriptId, result));
		        } else {
		        	return new InputRepresentation((InputStream) result);
		        }
			}
		}, 
		new ScriptExceptionHandler<Representation>() {
			@Override
			public Representation ehandle(Exception ex, String fullFnName, ParameterMap params) {
                return new JsonObjectRepresentation(TalkResponseBuilder.failResponse(ex));
			}
		}) ;
		
		Debug.line(rep, rep.getText());
	}
	
	public void testWhenStringArray() throws Exception {
		ParameterMap params = ParameterMap.BLANK ;
		
		Object obj = ts.callFn("test/refTos", params) ;
		assertEquals(true, obj == null);
	}
	
	public void xtestIfLangErrorWhenReloaded() throws Exception {
		ParameterMap params = ParameterMap.BLANK ;
		
		while(true){
			Thread.sleep(1000);
			Debug.line(ts.callFn("test/langError", params));
		}
	}
}
