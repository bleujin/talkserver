package net.ion.talk.monitor;

import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import junit.framework.TestCase;

public class TestTalkMonitor extends TestCase{

	public void testReplaceAll() throws Exception {
		String source = "/admin/event/rooms/$roomId$/messages/$mid$" ;
		String expect = "/admin/event/rooms/{roomId}/messages/{mid}" ;
		
		Debug.line(source.replaceAll("\\$(\\w*)\\$", "{$1}")) ;
	}
}
