package net.ion.craken.aradon;

import java.io.ByteArrayInputStream;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.client.StubServer;
import junit.framework.TestCase;

public class TestCrakenLet extends TestCase{

	private StubServer ss;
	private RepositoryEntry t;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.ss = StubServer.create(CrakenLet.class) ;
		this.t = ss.treeContext().putAttribute(RepositoryEntry.EntryName, RepositoryEntry.test()) ;
		
		t.login().tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/r1").property("name", "room").blob("blob", new ByteArrayInputStream("Hello".getBytes()));
				wsession.pathBy("/rooms/r1/msg1").property("content", "hello").property("sender", "/members/bleujin") ;
				wsession.pathBy("/members/bleujin").property("name", "bleujin") ;
				return null;
			}
		}) ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		t.shutdown();
		super.tearDown();
	}
	
	
	public void testViewNode() throws Exception {
		StubHttpResponse response = ss.request("/craken/rooms/r1").get() ;
		assertEquals(true, response.contentsString().startsWith("<!--craken.tpl-->"));
	}
	
	public void testProperty() throws Exception {
		StubHttpResponse response = ss.request("/craken/rooms/r1.name").get() ;
		assertEquals("room", response.contentsString());
		assertEquals("Hello", ss.request("/craken/rooms/r1.blob").get().contentsString());
	}
}
