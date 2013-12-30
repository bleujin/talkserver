package net.ion.talk;

import net.ion.script.rhino.RhinoResponse;
import net.ion.talk.let.TestBaseLet;

public class TestToonSession extends TestBaseLet{

	public void testExecScript() throws Exception {
		
		ToonSession toonSession = ToonSession.test();
		
		RhinoResponse response = toonSession.exec("testScript", "/bleujin");
	}
	
}
