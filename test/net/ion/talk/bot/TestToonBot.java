package net.ion.talk.bot;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.emotion.Empathyscope;
import net.ion.framework.util.Debug;
import junit.framework.TestCase;

public class TestToonBot extends TestCase {

	private ReadSession session;
	private ToonBot tb;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		RepositoryImpl r = RepositoryImpl.inmemoryCreateWithTest() ;
		this.session = r.login("test");
		this.tb = ToonBot.create(session) ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		session.workspace().repository().shutdown() ;
		super.tearDown();
	}
	
	
	public void testCreate() throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/roomroom/messages/12345").property("message", "T-T") ;
				return null;
			}
		}) ;
		
		Debug.line(tb.sendSVG("roomroom", "12345", "bat")) ;
	}
	

	
}
