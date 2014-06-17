package net.ion.talk.let;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.nradon.Radon;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.core.Aradon;
import net.ion.radon.util.AradonTester;
import net.ion.talk.bean.Const;

public class TestSVGLet extends TestCase {

	private Radon radon;
	private RepositoryEntry rentry;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Aradon aradon = AradonTester.create().register("svg", "/message/{roomId}/{messageId}", MessageSVGLet.class).getAradon();
		this.rentry = RepositoryEntry.test();
		aradon.getServiceContext().putAttribute(rentry.EntryName, rentry) ;
		
		this.radon = aradon.toRadon(9000).start().get() ;
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
		Response response = nc.prepareGet("http://61.250.201.157:9000/svg/message/roomroom/12345?type=sender&botId=wowe").execute().get() ;
		
		Debug.line(response.getTextBody()) ;
		nc.close(); 
	}
}
