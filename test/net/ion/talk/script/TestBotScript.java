package net.ion.talk.script;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import sun.org.mozilla.javascript.internal.NativeObject;
import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.util.Debug;
import net.ion.radon.aclient.NewClient;


public class TestBotScript extends TestCase{
	
	private BotScript bs;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		RepositoryEntry r = RepositoryEntry.test() ;
		ReadSession rsession = r.login() ;
		NewClient nc = NewClient.create() ;
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(2) ;
		this.bs = BotScript.create(rsession, ses, nc) ;
	}
	@Override
	protected void tearDown() throws Exception {
		bs.stop() ;
		super.tearDown();
	}
	
	public void testReflection() throws Exception {
		
		bs.readDir(new File("./bot")) ;
		
		Object object = bs.packages().get("dice") ;
		NativeObject no = (NativeObject)object;
		Debug.line(no.getIds(), no.getIds()[0].getClass(), no.getFunctionPrototype(no));
		
		
		Method[] methods = object.getClass().getDeclaredMethods();
		for (Method m : methods) {
			Debug.line(object.getClass(), m.getName(), m.getParameterTypes());
		}
	}
	
	
	public void testExistFunction() throws Exception {
		bs.readDir(new File("./bot")) ;
		assertEquals(true, bs.existFunction("system", "whenIN")) ;
		assertEquals(true, bs.existFunction("system", "whenOUT")) ;
		assertEquals(false, bs.existFunction("wowe", "whenIN")) ;
		assertEquals(false, bs.existFunction("wowe", "whenOUT")) ;
	}
	

}
