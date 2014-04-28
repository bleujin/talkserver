package net.ion.talk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.logging.LogBroker;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.message.push.sender.Pusher;
import net.ion.message.sms.sender.SMSSender;
import net.ion.nradon.WebSocketConnection;
import net.ion.nradon.WebSocketHandler;
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.core.TreeContext;
import net.ion.talk.account.AccountManager;
import net.ion.talk.bot.BotManager;
import net.ion.talk.handler.TalkHandler;
import net.ion.talk.handler.craken.NotifyStrategy;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.script.TalkScript;

import org.restlet.Context;
import org.restlet.routing.VirtualHost;

public class TalkEngine implements WebSocketHandler {

	public enum Reason {
		OK, NOTALLOW, DOPPLE, TIMEOUT, CLIENT, INTERNAL;
	}

	public static int HEARTBEAT_WATING = 15000;
	public static int HEARTBEAT_KILLING = 30000;
	public static int HEARTBEAT_DELAY = 1000;

	private static final Pattern hearbeatPtn = Pattern.compile("^HEARTBEAT$");

	protected ConnManager cmanager = ConnManager.create();
	private List<TalkHandler> handlers = ListUtil.newList();
	private Logger logger = LogBroker.getLogger(TalkEngine.class);

	private final TreeContext context;
	private final ScheduledExecutorService worker;
	private AtomicReference<Boolean> started = new AtomicReference<Boolean>(Boolean.FALSE);

	protected TalkEngine(TreeContext context) {
		if (context == null)
			throw new IllegalStateException("context is null");

		this.context = context;
		this.worker = context.getAttributeObject(ScheduledExecutorService.class.getCanonicalName(), ScheduledExecutorService.class);
		context.putAttribute(TalkEngine.class.getCanonicalName(), this);
	}

	public static TalkEngine testCreate() throws Exception {

		TreeContext context = TreeContext.createRootContext(new VirtualHost(new Context())) ;
		ScheduledExecutorService worker = Executors.newScheduledThreadPool(5);
		context.putAttribute(ScheduledExecutorService.class.getCanonicalName(), worker) ;
		
		RepositoryEntry repo = RepositoryEntry.test();
		repo.start(); 
		
		TalkScript ts = TalkScript.create(repo.login(), worker);
		ts.readDir(new File("./script"), true);

		context.putAttribute(RepositoryEntry.EntryName, repo);
		context.putAttribute(TalkScript.class.getCanonicalName(), ts);

		NewClient nc = NewClient.create(ClientConfig.newBuilder().setMaxRequestRetry(5).setMaxRequestRetry(2).build());
		context.putAttribute(NewClient.class.getCanonicalName(), nc);

		SMSSender smsSender = SMSSender.create(nc);
		context.putAttribute(SMSSender.class.getCanonicalName(), smsSender);
		context.putAttribute(BotManager.class.getCanonicalName(), BotManager.create(repo.login()));


		final TalkEngine result = new TalkEngine(context);
		Pusher pusher = NotifyStrategy.createSender(worker, repo.login());
		context.putAttribute(AccountManager.class.getCanonicalName(), AccountManager.create(result, pusher));

		return result;
	}

	public TreeContext context() {
		return context;
	}

	public <T> T contextAttribute(Class<T> clz) {
		return context.getAttributeObject(clz.getCanonicalName(), clz);
	}

	public TalkEngine registerHandler(TalkHandler hanlder) throws Exception {
		if (started.get())
			hanlder.onEngineStart(this);

		handlers.add(hanlder);
		return this;
	}

	public TalkEngine unregisterHandler(TalkHandler handler) {
		handlers.remove(handler);
		return this;
	}

	public TalkEngine startEngine() throws Exception {
		for (TalkHandler handler : handlers) {
			handler.onEngineStart(this);
		}

		started.set(Boolean.TRUE);

		worker.schedule(new HeartBeatJob(), TalkEngine.HEARTBEAT_DELAY, TimeUnit.MILLISECONDS);
		return this;
	}

	class HeartBeatJob implements Callable<Void> {
		@Override
		public Void call() {
			final long gmtTime = GregorianCalendar.getInstance().getTime().getTime();
			cmanager.handle(new ConnHandler<Void>(){
				@Override
				public Void handle(UserConnection conn) {
					if (conn.isOverTime(gmtTime)) cmanager.remove(conn, Reason.TIMEOUT) ;
					return null;
				}
			}) ;
			
			if (started.get()) worker.schedule(this, TalkEngine.HEARTBEAT_DELAY, TimeUnit.MILLISECONDS);
			return null ;
		}
	}

	public void stopEngine() {
		for (TalkHandler handler : handlers) {
			handler.onEngineStop(this);
		}
		started.set(Boolean.FALSE);

		RepositoryEntry r = context().getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
		r.shutdown();
		
		
		NewClient nc = context().getAttributeObject(NewClient.class.getCanonicalName(), NewClient.class);
		if (nc != null)
			nc.close();
	}

	public ReadSession readSession() throws IOException {
		RepositoryEntry re = context.getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
		return re.login();
	}

	public TalkScript talkScript() {
		return context.getAttributeObject(TalkScript.class.getCanonicalName(), TalkScript.class);
	}

	@Override
	public void onOpen(WebSocketConnection conn) {
		UserConnection created = UserConnection.create(conn);
		created.updateHeartBeat();
		cmanager.add(created);

		for (TalkHandler handler : handlers) {
			Reason reason = handler.onConnected(this, created);
			if (reason != Reason.OK) {
				cmanager.remove(created, reason);
				break;
			}
		}
	}

	@Override
	public void onClose(WebSocketConnection conn) {
		final UserConnection found = cmanager.findBy(conn);
		for (TalkHandler handler : handlers) {
			handler.onClose(this, found);
		}
		cmanager.remove(found, Reason.CLIENT);
	}

	@Override
	public void onMessage(WebSocketConnection conn, String msg) {
		try {
			final UserConnection found = cmanager.findBy(conn);

			// heartbeat
			found.updateHeartBeat();
			if (hearbeatPtn.matcher(msg).matches())
				return;

			TalkMessage tmessage = TalkMessage.fromJsonString(msg);

			RepositoryEntry r = context().getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
			ReadSession rsession = r.login();

			for (TalkHandler handler : handlers) {
				handler.onMessage(this, found, rsession, tmessage);
			}
		} catch (IOException ex) {
			logger.warning(ex.getLocalizedMessage());
		}
	}

	@Override
	public void onMessage(WebSocketConnection conn, byte[] msg) {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public void onPing(WebSocketConnection conn, byte[] msg) {
		conn.pong(msg);
		// throw new UnsupportedOperationException("not supported");
	}

	@Override
	public void onPong(WebSocketConnection conn, byte[] msg) {
		// throw new UnsupportedOperationException("not supported");
	}

	ConnManager connManger() {
		return cmanager;
	}

	public <T extends TalkHandler> T handler(Class<T> clz) {
		for (TalkHandler handler : handlers) {
			if (clz.isInstance(handler))
				return clz.cast(handler);
		}
		throw new IllegalArgumentException();
	}

	public Logger getLogger() {
		return logger;
	}

	public void remove(UserConnection uconn, Reason reason) {
		connManger().remove(uconn, reason);
	}

	public boolean existUser(String id) {
		return connManger().contains(id);
	}

	public UserConnection findConnection(String id) {
		return connManger().findBy(id);
	}

	public UserConnection getUserConnection(WebSocketConnection wconn) {
		return connManger().findBy(wconn);
	}

	public void sendMessage(String userId, Pusher sender, TalkResponse tresponse) {
		UserConnection uconn = findConnection(userId);
		if (uconn != null) {
			uconn.sendMessage(tresponse.talkMessage());
		} else {
			sender.sendTo(userId).sendAsync(tresponse.pushMessage());
		}
	}
}

interface ConnHandler<T> {
	public T handle(UserConnection conn) ;
}

class ConnManager {

	private Map<String, UserConnection> conns = MapUtil.newMap();

	private ConnManager() {
	}

	public UserConnection findBy(String id) {
		return conns.get(id);
	}

	public UserConnection findBy(WebSocketConnection wconn) {
		return conns.get(wconn.getString("id"));
	}

	public static ConnManager create() {
		return new ConnManager();
	}

	public UserConnection add(UserConnection uconn) {
		UserConnection existConn = conns.put(uconn.id(), uconn);
		if (existConn != null)
			existConn.close(TalkEngine.Reason.DOPPLE);
		return uconn;
	}
	
	public <T> List<T> handle(ConnHandler<T> handler){
		ArrayList<UserConnection> copyed = new ArrayList<UserConnection>(conns.values()) ;
		List<T> result = ListUtil.newList() ;
		for (UserConnection uconn : copyed) {
			result.add(handler.handle(uconn)) ;
		}
		return result ;
	}

	public UserConnection remove(UserConnection uconn, TalkEngine.Reason reason) {
		conns.remove(uconn.id());
		uconn.close(reason);
		return uconn;
	}

	public boolean contains(String id) {
		return conns.containsKey(id);
	}

	public boolean contains(WebSocketConnection conn) {
		return conns.containsValue(conn);
	}

}
