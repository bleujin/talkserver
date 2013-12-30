package net.ion.talk;

import java.io.IOException;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadSession;
import net.ion.script.rhino.RhinoResponse;

public class ToonSession {

	
	private RhinoEntry rengine;
	private ReadSession session;

	ToonSession() throws IOException{
		RepositoryEntry rentry = RepositoryEntry.test();
		session = rentry.login();
		rengine = RhinoEntry.test();
	}
	
	public static ToonSession test() throws IOException {
		return new ToonSession();
	}

	public RhinoResponse exec(String name, String path) {
		String script = session.pathBy("/path").property("script").stringValue();
		return rengine.newScript(name).defineScript(script).exec();
	}

}
