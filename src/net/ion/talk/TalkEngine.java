package net.ion.talk;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import junit.framework.Assert;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.logging.LogBroker;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.message.push.sender.Pusher;
import net.ion.message.sms.sender.SMSSender;
import net.ion.nradon.WebSocketConnection;
import net.ion.nradon.WebSocketHandler;
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.core.TreeContext;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.account.AccountManager;
import net.ion.talk.bot.BBot;
import net.ion.talk.bot.BotManager;
import net.ion.talk.bot.ChatBot;
import net.ion.talk.bot.EchoBot;
import net.ion.talk.engine.HeartBeat;
import net.ion.talk.handler.TalkHandler;
import net.ion.talk.handler.craken.NotificationListener;
import net.ion.talk.handler.craken.NotifyStrategy;
import net.ion.talk.handler.craken.TalkMessageHandler;
import net.ion.talk.handler.craken.UserInAndOutRoomHandler;
import net.ion.talk.handler.engine.ServerHandler;
import net.ion.talk.handler.engine.UserConnectionHandler;
import net.ion.talk.handler.engine.TalkCommandHandler;
import net.ion.talk.handler.engine.TalkScriptHandler;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.script.BotScript;
import net.ion.talk.script.TalkScript;
import net.ion.talk.util.CalUtil;

import org.restlet.Context;
import org.restlet.routing.VirtualHost;

public class TalkEngine implements WebSocketHandler {

	public enum Reason {
		OK, NOTALLOW, DOPPLE, TIMEOUT, CLIENT, INTERNAL;
	}

	private static final Pattern hearbeatPtn = Pattern.compile("^HEARTBEAT$");

	protected ConnManager cmanager = ConnManager.create();
	private List<TalkHandler> handlers = ListUtil.newList();
	private Logger logger = LogBroker.getLogger(TalkEngine.class);

	private final TreeContext context;
	private final ScheduledExecutorService worker;

	private AtomicReference<Boolean> started = new AtomicReference<Boolean>(Boolean.FALSE);
	private HeartBeat heartBeat ;


	private TalkEngine(TreeContext context) throws IOException {
		
		this.context = context ;
		this.worker = context.getAttributeObject(ScheduledExecutorService.class.getCanonicalName(), ScheduledExecutorService.class);
		context.putAttribute(TalkEngine.class.getCanonicalName(), this);
		this.heartBeat = new HeartBeat(worker) ;
	}
	
	
	public static TalkEngine create(TreeContext context) throws Exception {
		return testCreate(context) ;
	}

	public static TalkEngine testCreate() throws Exception {
		TreeContext context = TreeContext.createRootContext(new VirtualHost(new Context())) ;
		context.putAttribute(RepositoryEntry.EntryName, RepositoryEntry.test()) ;
		context.putAttribute(ScheduledExecutorService.class.getCanonicalName(), Executors.newScheduledThreadPool(5)) ;
		return testCreate(context) ;
	}

	
	public static TalkEngine testCreate(TreeContext context) throws Exception {
		if (context == null)
			throw new IllegalStateException("context is null");
		
		Assert.assertNotNull(context.getAttributeObject(ScheduledExecutorService.class.getCanonicalName(), ScheduledExecutorService.class)) ;
		Assert.assertNotNull(context.getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class)) ;
		return new TalkEngine(context) ;
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
	
	public TalkEngine init() throws Exception {
		NewClient nc = NewClient.create(ClientConfig.newBuilder().setMaxRequestRetry(5).setMaxRequestRetry(2).build());
		SMSSender smsSender = SMSSender.create(nc);
		
		context().putAttribute(NewClient.class.getCanonicalName(), nc) ;
		context().putAttribute(SMSSender.class.getCanonicalName(), smsSender) ;
		
		ReadSession rsession = readSession();

		final BotScript bs = BotScript.create(rsession, worker, nc) ;
		bs.readDir(new File("./bot"), true) ;
		context.putAttribute(BotScript.class.getCanonicalName(), bs) ;
		context.putAttribute(AccountManager.class.getCanonicalName(), AccountManager.create(bs, this, NotifyStrategy.createPusher(worker, readSession()))) ;

		AccountManager am = context().getAttributeObject(AccountManager.class.getCanonicalName(), AccountManager.class);

		registerHandler(new UserConnectionHandler()).registerHandler(ServerHandler.test()).registerHandler(new TalkScriptHandler()).registerHandler(new TalkCommandHandler()) ;

		rsession.workspace().cddm().add(new UserInAndOutRoomHandler());
		rsession.workspace().cddm().add(new TalkMessageHandler(nc));
		rsession.workspace().addListener(new NotificationListener(am));

		TalkScript ts = TalkScript.create(rsession, worker);
		ts.readDir(new File("./script"), true);
		context.putAttribute(TalkScript.class.getCanonicalName(), ts);

		heartBeat().delaySecond(15) ;
		return this;
	}


	public TalkEngine startEngine() throws Exception {

		for (TalkHandler handler : handlers) {
			handler.onEngineStart(this);
		}


		started.set(Boolean.TRUE);
		heartBeat.startBeat(new Runnable(){
			@Override
			public void run() {
				cmanager.handle(new ConnHandler<Void>(){
					@Override
					public Void handle(UserConnection conn) {
						if (heartBeat.isOverTime(conn)) {
							conn.close(Reason.TIMEOUT);
						}
						return null;
					}
				}) ;
			}
			
		});
		
		return this;
	}

	public HeartBeat heartBeat(){
		return heartBeat ;
	}
	
	public void stopEngine() {
		for (TalkHandler handler : handlers) {
			handler.onEngineStop(this);
		}
		heartBeat.endBeat(); 
		started.set(Boolean.FALSE);

		RepositoryEntry r = context().getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
		r.shutdown();
		
		worker.shutdown(); 
		
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
		UserConnection created = cmanager.singleConnection(conn); 
		created.updateHeartBeat();

		for (TalkHandler handler : handlers) {
			Reason reason = handler.onConnected(this, created);
			if (reason != Reason.OK) {
				created.close(reason);
				return;
			}
		}
	}

	@Override
	public void onClose(WebSocketConnection conn) {
		if (conn == null) return ;
		
		final UserConnection found = UserConnection.create(conn); // if server do close
		
		for (TalkHandler handler : handlers) {
			handler.onClose(this, found);
		}
		cmanager.remove(conn, Reason.CLIENT);
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


	public boolean isConnected(String id) {
		return connManger().contains(id);
	}

	public UserConnection findConnection(String id) {
		return connManger().findBy(id);
	}

	public TalkEngine clearHandler() {
		handlers.clear(); 
		return this;
	}


}

interface ConnHandler<T> {
	public T handle(UserConnection conn) ;
}

class ConnManager {

	private CopyOnWriteArraySet<WebSocketConnection> conns = new CopyOnWriteArraySet<WebSocketConnection>() ;

	private ConnManager() {
	}

	UserConnection findBy(String id) {
		List<WebSocketConnection> found = ListUtil.newList() ;
		for(WebSocketConnection conn : conns){
			if (id.equals(conn.data("id"))) found.add(conn) ;
		}
		if (found.size() == 0) return UserConnection.NOTFOUND ;
		else if (found.size() == 1) return UserConnection.create(found.get(0)) ;
		else return DoppleUserConnection.create(found);
	}

	public UserConnection findBy(WebSocketConnection wconn) {
		return UserConnection.create(wconn) ;
	}

	static ConnManager create() {
		return new ConnManager();
	}

	UserConnection singleConnection(WebSocketConnection wconn) {
//		UserConnection existConn = findBy(wconn.data("id").toString()) ;
//		if (existConn != UserConnection.NOTFOUND) {
//			existConn.close(Reason.DOPPLE); 
//		}
		conns.add(wconn);
		return UserConnection.create(wconn) ;
	}
	
	<T> List<T> handle(ConnHandler<T> handler){
		List<T> result = ListUtil.newList() ;
		for (WebSocketConnection conn : conns) {
			result.add(handler.handle(UserConnection.create(conn))) ;
		}
		return result ;
	}

	void remove(WebSocketConnection uconn, TalkEngine.Reason reason) {
		Debug.line(uconn, uconn.data("id"), reason);
		
		conns.remove(uconn);
	}

	boolean contains(String id) {
		for(WebSocketConnection conn : conns){
			if (id.equals(conn.data("id"))) return true ;
		}
		return false ;
	}

}
