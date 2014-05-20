package net.ion.talk.bot;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.talk.TalkEngine;
import net.ion.talk.bean.Const;

public class TestBBot extends TestCase {

	private RepositoryEntry rentry;
	private TalkEngine talkEngine;
	private ReadSession session;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		this.rentry = RepositoryEntry.test();

		session = rentry.login();

		this.talkEngine = TalkEngine.testCreate(rentry);

		talkEngine.init().startEngine();
	}

	@Override
	protected void tearDown() throws Exception {
		this.talkEngine.stopEngine();
		this.rentry.shutdown();
		super.tearDown();
	}

	public void testOnLoad() {
		// after loading...
		assertTrue(session.exists("/bots/b@"));
		assertTrue(session.exists("/users/b@"));
	}

	public void testOnMessage() throws Exception {
		final String bbotCmd = "b@도움말";
		
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/airkjh@i-on.net").property(Const.User.UserId, "airkjh@i-on.net");
				wsession.pathBy("/rooms/roomroom/members/airkjh@i-on.net");
				wsession.pathBy("/rooms/roomroom/members/b@");
				wsession.pathBy("/rooms/roomroom/messages/1234").property(Const.Message.Message, bbotCmd).property("event", "onMessage").refTo("sender", "/users/airkjh@i-on.net");
				
				return null;
			}
		});
		
		session.pathBy("/rooms/roomroom/messages").children().debugPrint();
	}
}
