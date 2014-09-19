package net.ion.talk.let;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.nradon.Radon;
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.client.StubServer;
import net.ion.talk.bean.Const;

public class TestSVGLet extends TestCase {

	private Radon radon;
	private RepositoryEntry rentry;
	private StubServer ss;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// Aradon aradon = AradonTester.create().register("svg", "/message/{roomId}/{messageId}", SVGLet.class).getAradon();
		this.ss = StubServer.create(SVGLet.class) ;
		this.rentry = ss.treeContext().putAttribute(rentry.EntryName, RepositoryEntry.test()) ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		radon.stop().get(); 
		super.tearDown();
	}
	
	public void testGet() throws Exception {
		ReadSession session = rentry.login() ;
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				
				wsession.pathBy("/users/bleujin").property("userId", "bleujin").property("nickname", "bleujin") ;
				wsession.pathBy("/users/wowe").property("userId", "wowe").property("nickname", "wow") ;
				wsession.pathBy("/bots/wowe").property("userId", "wowe").property("nickname", "wow")
					.property("senderSVG", "<svg width='100%' height='100%' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink'><text>${message}</text></svg>") ;
				
				wsession.pathBy("/rooms/roomroom").refTo(Const.Bot.PostBot, "/bots/wowe") ;
				wsession.pathBy("/rooms/roomroom/messages/12345").property("message", "Heloo World").refTo("sender", "/users/bleujin") ;
				wsession.pathBy("/notifies/12345").refTo("message", "/rooms/roomroom/messages/12345") ;
				return null;
			}
		}) ;
		
		NewClient nc = NewClient.create();
		StubHttpResponse response = ss.request("/svg/message/roomroom/12345?type=sender&botId=wowe").get() ;
		Debug.line(response.contentsString()) ;
		nc.close(); 
	}
}
