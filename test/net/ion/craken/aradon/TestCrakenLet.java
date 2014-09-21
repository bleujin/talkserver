package net.ion.craken.aradon;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.client.StubServer;
import junit.framework.TestCase;

public class TestCrakenLet extends TestCase{

	public void testView() throws Exception {
		StubServer ss = StubServer.create(CrakenLet.class) ;
		RepositoryEntry t = ss.treeContext().putAttribute(RepositoryEntry.EntryName, RepositoryEntry.test()) ;
		
		t.login().tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/r1").property("name", "room");
				wsession.pathBy("/rooms/r1/msg1").property("content", "hello").property("sender", "/members/bleujin") ;
				wsession.pathBy("/members/bleujin").property("name", "bleujin") ;
				return null;
			}
		}) ;
		
		StubHttpResponse response = ss.request("/craken/rooms/r1").get() ;
		
		Debug.line(response.contentsString());
	}
}
