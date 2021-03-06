package net.ion.talk.bot;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.*;
import net.ion.talk.bean.Const;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.responsebuilder.TalkResponseBuilder;


public class TestCrakenBase extends TestCase {

	protected ReadSession rsession;
	protected RepositoryEntry rentry;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		rentry = RepositoryEntry.test();
		rentry.start();
		rsession = rentry.login();
	}

	@Override
	public void tearDown() throws Exception {
		rentry.shutdown();
		super.tearDown();
	}

	protected ReadNode getFirstNotify(String userId) {
		return rsession.pathBy("/notifies/" + userId).children().firstNode();
	}

	protected void createBotToCraken(final String botId, final String requestURL, final boolean isSync) {
		rsession.tran(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/bots/" + botId).property(Const.Bot.isSyncBot, isSync);
				return null;
			}
		});
	}

	protected void createUserToCraken(final String user) {
		rsession.tran(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/" + user);
				return null;
			}
		});
	}

	protected void addUserToRoom(final String roomId, final String... users) {
		rsession.tran(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				for (String userId : users) {
					wsession.pathBy("/rooms/" + roomId + "/members").child(userId).refTo(Const.Ref.User, "/users/" + userId);
				}
				return null;
			}
		});
	}

	protected void removeUserToRoom(final String roomId, final String... users) {
		rsession.tran(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				for (String userId : users) {
					wsession.pathBy("/rooms/" + roomId + "/members/" + userId).removeSelf();
				}
				return null;
			}
		});
	}

	protected TalkResponse createNotify(final String user, final String notifyId) {
		rsession.tran(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/notifies/" + user + "/" + notifyId);
				return null;
			}
		});

		return TalkResponseBuilder.create().newInner().property(Const.Notify.NotifyId, notifyId).build();
	}
}
