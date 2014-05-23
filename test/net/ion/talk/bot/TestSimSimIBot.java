package net.ion.talk.bot;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.talk.bean.Const.User;

public class TestSimSimIBot extends TestBaseServer {

	public void testOnLoad() throws Exception {
		ReadSession session = talkEngine.readSession();

		assertEquals(true, session.exists("/bots/simsimi"));
		assertEquals(true, session.exists("/users/simsimi"));

		assertEquals("SimSimI bot", session.pathBy("/users/simsimi").property("nickname").asString());
	}

	public void testOnEnter() throws Exception {
		ReadSession session = talkEngine.readSession();

		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/bleujin").property(User.UserId, "bleujin");
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
		assertEquals(1, session.pathBy("/notifies/bleujin").children().count());

		session.pathBy("/rooms/roomroom/messages").children().debugPrint();
//		new InfinityThread().startNJoin();
	}

	public void testOnMessage() throws Exception {
		ReadSession session = talkEngine.readSession();

		talkToSimsimi(session, "안녕 심심이");
		
		session.workspace().cddm().await();
		Thread.sleep(2000);
		
		session.pathBy("/rooms/roomroom/messages").children().debugPrint();

		assertEquals(true, session.exists("/notifies/bleujin/123456"));
	}
	
	public void testOnEnglishMessage() throws Exception {
		ReadSession session = talkEngine.readSession();

		talkToSimsimi(session, "Hello sim");
		
		session.workspace().cddm().await();
		Thread.sleep(2000);
		
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
				wsession.pathBy("/rooms/roomroom/messages/123456").property("message", message).property("options", "{event:'onMessage'}").property("clientScript", "client.room().message(args);").refTo("sender", "/users/bleujin");
				return null;
			}
		});
		
	}
}