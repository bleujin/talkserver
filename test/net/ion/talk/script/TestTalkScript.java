package net.ion.talk.script;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import sun.org.mozilla.javascript.internal.NativeObject;
import sun.org.mozilla.javascript.internal.Scriptable;
import net.ion.craken.node.crud.TestBaseCrud;
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

}
