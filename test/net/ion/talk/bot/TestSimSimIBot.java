package net.ion.talk.bot;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;
import net.ion.craken.aradon.NodeLet;
import net.ion.craken.aradon.UploadLet;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.nradon.Radon;
import net.ion.radon.aclient.AsyncCompletionHandler;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.talk.TalkEngine;
import net.ion.talk.let.ResourceLet;
import net.ion.talk.let.ScriptDoLet;
import net.ion.talk.toonweb.ClientLet;

public class TestSimSimIBot extends TestCase {

	// 22441b90-e7a7-4a68-9f5b-6486b0a59676

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
				// wsession.pathBy("/servers/home").property("host", "61.250.201.157").property("port", 9000);
				wsession.pathBy("/users/bleujin").property("password", "1234");
				return null;
			}
		});

		ConfigurationBuilder cbuilder = ConfigurationBuilder.newBuilder().aradon().addAttribute(RepositoryEntry.EntryName, rentry).sections().restSection("admin").addAttribute("baseDir", "./resource/template").path("node").addUrlPattern("/repository/{renderType}").matchMode(IMatchMode.STARTWITH)
				.handler(NodeLet.class).path("template").addUrlPattern("/template").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ResourceLet.class).path("doscript").addUrlPattern("/script").matchMode(EnumClass.IMatchMode.EQUALS).handler(ScriptDoLet.class).path("upload")
				.addUrlPattern("/upload").matchMode(IMatchMode.STARTWITH).handler(UploadLet.class).restSection("session").path("client").addUrlPattern("/{userId}/{roomId}").handler(ClientLet.class).toBuilder();
		// .restSection("toonweb")
		// .path("toonweb").addUrlPattern("/").matchMode(IMatchMode.STARTWITH).handler(ToonWebResourceLet.class).toBuilder();

		Aradon aradon = Aradon.create(cbuilder.build());

		this.radon = aradon.toRadon(9000).start().get();

		this.talkEngine = TalkEngine.testCreate(rentry);

		radon.add("/websocket/{id}/{accessToken}", talkEngine);
	}

	@Override
	protected void tearDown() throws Exception {
		this.talkEngine.stopEngine();
		this.rentry.shutdown();
		this.radon.stop();
		super.tearDown();
	}


	public void testOnLoad() throws Exception {
		talkEngine.init().startEngine();
		ReadSession session = talkEngine.readSession();

		assertEquals(true, session.exists("/bots/simsimi"));
		assertEquals(true, session.exists("/users/simsimi"));

		assertEquals("SimSimI bot", session.pathBy("/users/simsimi").property("nickname").asString());
	}

	public void testOnEnter() throws Exception {
		talkEngine.init().startEngine();
		ReadSession session = talkEngine.readSession();

		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/bleujin");
				wsession.pathBy("/rooms/roomroom/members/simsimi");
				return null;
			}
		});

		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/roomroom/members/bleujin");
				return null;
			}
		});
		session.workspace().cddm().await();
		Thread.sleep(500); // wait workspace Listener(NotificationListener)

		assertEquals(3, session.pathBy("/rooms/roomroom/messages").children().count());
		assertEquals(2, session.pathBy("/notifies/bleujin").children().count());

		session.pathBy("/rooms/roomroom/messages").children().debugPrint();
		// new InfinityThread().startNJoin();
	}

	public void testOnMessage() throws Exception {
		talkEngine.init().startEngine();
		ReadSession session = talkEngine.readSession();

		talkToSimsimi(session, "안녕 심심이");
		
		session.workspace().cddm().await();
		Thread.sleep(5000);
		
		session.pathBy("/rooms/roomroom/messages").children().debugPrint();

		assertEquals(true, session.exists("/notifies/bleujin/123456"));
	}
	
	public void testOnEnglishMessage() throws Exception {
		talkEngine.init().startEngine();
		ReadSession session = talkEngine.readSession();

		talkToSimsimi(session, "Hello sim");
		
		session.workspace().cddm().await();
		Thread.sleep(5000);
		
		session.pathBy("/rooms/roomroom/messages").children().debugPrint();

		assertEquals(true, session.exists("/notifies/bleujin/123456"));
	}
	
	private void talkToSimsimi(ReadSession session, final String message) throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/bleujin");

				wsession.pathBy("/rooms/roomroom/members/simsimi");
				wsession.pathBy("/rooms/roomroom/members/bleujin");
				return null;
			}
		});

		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/roomroom/messages/123456").property("message", message).property("event", "onMessage").refTo("sender", "/users/bleujin");
				return null;
			}
		});
		
	}
}