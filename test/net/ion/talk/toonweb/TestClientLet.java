package net.ion.talk.toonweb;

import org.restlet.data.Method;

import junit.framework.TestCase;
import net.ion.bleujin.restlet.TestComponentAsServer;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Realm;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Realm.RealmBuilder;
import net.ion.radon.aclient.Response;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass.ILocation;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.util.AradonTester;
import net.ion.talk.TalkEngine;
import net.ion.talk.account.AccountManager;
import net.ion.talk.bot.BotManager;
import net.ion.talk.filter.ToonAuthenticator;
import net.ion.talk.handler.craken.NotificationListener;
import net.ion.talk.handler.craken.NotifyStrategy;
import net.ion.talk.handler.craken.TalkMessageHandler;
import net.ion.talk.handler.craken.UserInAndOutRoomHandler;
import net.ion.talk.handler.engine.ServerHandler;
import net.ion.talk.handler.engine.UserConnectionHandler;
import net.ion.talk.handler.engine.WebSocketScriptHandler;

public class TestClientLet extends TestCase {

	private Radon radon;
	private RepositoryEntry rentry;
	private TalkEngine talkEngine;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.rentry = RepositoryEntry.test();
		ReadSession session = rentry.login();

		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
//				wsession.pathBy("/servers/home").property("host", "61.250.201.157").property("port", 9000);
				wsession.pathBy("/users/bleujin").property("password", "1234");
				return null;
			}
		});

		
		Aradon aradon = AradonTester.create()
				.register("session", "/{userId}/{roomId}", ClientLet.class)
				.register("toonweb", "/", "resource", IMatchMode.STARTWITH, ToonWebResourceLet.class)
				.mergeSection("session").putAttribute(RepositoryEntry.EntryName, rentry)
//				.addFilter(ILocation.PRE, new ToonAuthenticator("toon"))
				
				.getAradon();

		
		this.radon = aradon.toRadon(9000)
					.start().get();
		
		this.talkEngine = TalkEngine.testCreate(rentry) ;
		
		radon.add("/websocket/{id}/{accessToken}", talkEngine) ;
	}

	@Override
	protected void tearDown() throws Exception {
		this.talkEngine.stopEngine(); 
		this.rentry.shutdown();
		this.radon.stop();
		super.tearDown();
	}

	public void testPost() throws Exception {
		talkEngine.startEngine() ;
		NewClient nc = NewClient.create();
		Realm realm = new RealmBuilder().setPrincipal("bleujin").setPassword("1234").build();
		Request request = new RequestBuilder(Method.POST).setRealm(realm).setUrl("http://localhost:9000/session/bleujin@i-on.net/roomroom").build();
		Response response = nc.prepareRequest(request).execute().get();

		Debug.line(response.getTextBody());
		nc.close();
	}
	
	public void testRun() throws Exception {
		talkEngine.init().startEngine() ;
		ReadSession rsession = talkEngine.readSession();
		
		rsession.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/roomroom/members/bleujin@i-on.net") ;
				return null;
			}
		});
		
		new InfinityThread().startNJoin(); 
	}
}
