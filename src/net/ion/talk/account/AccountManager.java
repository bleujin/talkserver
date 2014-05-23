package net.ion.talk.account;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.ecs.xhtml.a;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.tree.PropertyValue;
import net.ion.framework.logging.LogBroker;
import net.ion.framework.util.MapUtil;
import net.ion.message.push.sender.Pusher;
import net.ion.radon.aclient.NewClient;
import net.ion.talk.TalkEngine;
import net.ion.talk.UserConnection;
import net.ion.talk.account.Account.Type;
import net.ion.talk.bean.Const;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.script.BotScript;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 20. Time: 오후 4:15 To change this template use File | Settings | File Templates.
 */
public class AccountManager {

	private final Pusher pusher;
	private BotScript bs;
	private final TalkEngine tengine;
	private ReadSession session;
	private Logger log = LogBroker.getLogger(AccountManager.class);

	private Map<Type, AccountHandler> handlers = MapUtil.newMap() ;
	
	private Account NotRegisteredUser = new Account("notFound", Type.NOT_REGISTERED) {
		@Override
		public void onMessage(String notifyId) {
			log.warning("not registed user");
		}
	};

	protected AccountManager(BotScript bs, TalkEngine tengine, Pusher pusher) throws IOException {
		this.bs = bs;
		this.tengine = tengine;
		this.pusher = pusher;
		init();
	}

	protected void init() throws IOException {
		session = tengine.readSession();
		handlers.put(Type.CONNECTED_USER, new AccountHandler() {
			@Override
			public Account create(AccountManager am, String userId, UserConnection uconn) {
				return new ConnectedUserAccount(userId, session, uconn);
			}
		}) ;
		handlers.put(Type.DISCONNECTED_USER, new AccountHandler() {
			@Override
			public Account create(AccountManager am, String userId, UserConnection uconn) {
				return new DisconnectedAccount(userId, session, pusher);
			}
		}) ;
		handlers.put(Type.BOT, new AccountHandler() {
			@Override
			public Account create(AccountManager am, String userId, UserConnection uconn) {
				return new BotAccount(am.tengine, bs, session, userId);
			}
		}) ;
		handlers.put(Type.NOT_REGISTERED, new AccountHandler() {
			@Override
			public Account create(AccountManager am, String userId, UserConnection uconn) {
				return NotRegisteredUser ;
			}
		}) ;
		
		handlers.put(Type.PROXY, new AccountHandler() {
			@Override
			public Account create(AccountManager am, String userId, UserConnection uconn) {
				Account appAccount = handlers.get(Type.DISCONNECTED_USER).create(am, userId, uconn) ;
				return ProxyAccount.create(am, userId, uconn, appAccount);
			}
		}) ;
	}

	public static AccountManager create(BotScript bs, TalkEngine tengine, Pusher sender) throws IOException {
		return new AccountManager(bs, tengine, sender);
	}

	
	public Account newAccount(String toUserId) {
		UserConnection uconn = tengine.findConnection(toUserId);

		if (uconn != UserConnection.NOTFOUND) { // connected
			Account account = handlers.get(Type.CONNECTED_USER).create(this, toUserId, uconn);
			return uconn.fromApp() ? account :  handlers.get(Type.PROXY).create(this, toUserId, uconn) ;
		} else if (uconn == UserConnection.NOTFOUND && session.exists("/bots/" + toUserId)) {
			return handlers.get(Type.BOT).create(this, toUserId, uconn);
		} else if (uconn == UserConnection.NOTFOUND && session.exists("/users/" + toUserId)) {
			return handlers.get(Type.DISCONNECTED_USER).create(this, toUserId, uconn);
		}
		return handlers.get(Type.NOT_REGISTERED).create(this, toUserId, uconn);
		
	}

	public AccountManager defineHandle(Type type, AccountHandler accountHandler) {
		handlers.put(type, accountHandler) ;
		return this ;
	}

}

