package net.ion.talk;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.logging.LogBroker;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectId;
import net.ion.message.push.sender.Sender;
import net.ion.message.sms.sender.SMSConfig;
import net.ion.message.sms.sender.SMSSender;
import net.ion.nradon.AbstractWebSocketResource;
import net.ion.nradon.WebSocketConnection;
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.IService;
import net.ion.radon.core.SectionService;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.config.WSPathConfiguration;
import net.ion.radon.core.context.OnOrderEventObject;
import net.ion.talk.account.AccountManager;
import net.ion.talk.bot.BotManager;
import net.ion.talk.handler.TalkHandler;
import net.ion.talk.handler.TalkHandlerGroup;
import net.ion.talk.handler.craken.NotifyStrategy;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.responsebuilder.TalkResponseBuilder;

public class TalkEngine extends AbstractWebSocketResource implements OnOrderEventObject {

    public enum Reason {
		OK, NOTALLOW, DOPPLE, TIMEOUT, CLIENT, INTERNAL;
	}

    public static int HEARTBEAT_WATING = 15;
    public static int HEARTBEAT_KILLING = 15;

	private ConnManager cmanger= ConnManager.create();
	private List<TalkHandler> handlers = ListUtil.newList();
	private Aradon aradon;
	private Logger logger = LogBroker.getLogger(TalkEngine.class);


	private TalkEngine(){
		// called by aradon reflection
	}

	protected TalkEngine(Aradon aradon) {
		if (aradon == null)
			throw new IllegalStateException("aradon is null");

		this.aradon = aradon;
		aradon.getServiceContext().putAttribute(TalkEngine.class.getCanonicalName(), this);
	}
	public static TalkEngine create(Aradon aradon) {
		return new TalkEngine(aradon);
	}

	public static TalkEngine test() throws Exception {
		RepositoryEntry repo = RepositoryEntry.test();
		Aradon aradon = Aradon.create();
		aradon.getServiceContext().putAttribute(RepositoryEntry.EntryName, repo);
		aradon.getServiceContext().putAttribute(RhinoEntry.EntryName, RhinoEntry.test());
		final TalkEngine result = TalkEngine.create(aradon);
		return result;
	}

	public TalkEngine startForTest() throws Exception {
		aradon.start();
		return this;
	}

	// Only test
	public void stopForTest() {
        onEvent(AradonEvent.STOP, null);
	}

	public void onInit(SectionService parent, TreeContext context, WSPathConfiguration wsconfig) {
		super.onInit(parent, context, wsconfig);
		this.aradon = parent.getAradon();
        aradon.getServiceContext().putAttribute(TalkEngine.class.getCanonicalName(), this);
        NewClient nc = NewClient.create(ClientConfig.newBuilder().setMaxRequestRetry(5).setMaxRequestRetry(2).build());
        aradon.getServiceContext().putAttribute(NewClient.class.getCanonicalName(), nc);


        SMSSender smsSender = new SMSConfig(nc).newInternational().create();
        aradon.getServiceContext().putAttribute(SMSSender.class.getCanonicalName(), smsSender);
        try {

            aradon.getServiceContext().putAttribute(BotManager.class.getCanonicalName(), BotManager.create(readSession()));
            aradon.getServiceContext().putAttribute(AccountManager.class.getCanonicalName(), new AccountManager(this, NotifyStrategy.createSender(readSession())));

        } catch (IOException e) {
            e.printStackTrace();
        }
        TalkHandlerGroup hg = context.getAttributeObject(TalkHandlerGroup.class.getCanonicalName(), TalkHandlerGroup.class);
		hg.set(this);

	}

	public ReadSession readSession() throws IOException {
		RepositoryEntry re = aradon.getServiceContext().getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
		return re.login();
	}

	public RhinoEntry rhinoEntry() {
		return aradon.getServiceContext().getAttributeObject(RhinoEntry.EntryName, RhinoEntry.class);
	}

	public TalkEngine registerHandler(TalkHandler hanlder) {
		handlers.add(hanlder);
		return this;
	}

	public TalkEngine unregisterHander(TalkHandler handler) {
		handlers.remove(handler);
		return this;
	}

	public TreeContext context() {
		return aradon.getServiceContext();
	}

	@Override
	public void onOpen(WebSocketConnection conn) {
		UserConnection created = UserConnection.create(conn);
		cmanger.add(created);
        cmanger.heartBeat(created);

		for (TalkHandler handler : handlers) {
			Reason reason = handler.onConnected(this, created);
			if (reason != Reason.OK) {
				cmanger.remove(created, reason) ;
				break ;
			}
		}
	}

	@Override
	public void onClose(WebSocketConnection conn) {
		final UserConnection found = cmanger.findBy(conn);
		for (TalkHandler handler : handlers) {
			handler.onClose(this, found);
		}
		cmanger.remove(found, Reason.CLIENT);
	}

	@Override
	public void onMessage(WebSocketConnection conn, String msg) {
		try {

			final UserConnection found = cmanger.findBy(conn);
            cmanger.heartBeat(found);
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
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public void onPong(WebSocketConnection conn, byte[] msg) {
		throw new UnsupportedOperationException("not supported");
	}

	ConnManager connManger() {
		return cmanger;
	}

	@Override
	public void onStop() {
		onEvent(AradonEvent.STOP, null);
	}

	@Override
	public void onEvent(AradonEvent event, IService service) {
		try {
			if (event == AradonEvent.START) {
				for (TalkHandler handler : handlers) {
					handler.onEngineStart(this);
				}
			} else if (event == AradonEvent.STOP) {
				for (TalkHandler handler : handlers) {
					handler.onEngineStop(this);
				}
                RepositoryEntry r = context().getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
                r.onEvent(AradonEvent.STOP, service);
                NewClient nc = context().getAttributeObject(NewClient.class.getCanonicalName(), NewClient.class);
                if(nc!=null)
                    nc.close();
			}

		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
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

	@Override
	public int order() {
		return 2;
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

    public UserConnection getUserConnection(WebSocketConnection wconn){
        return connManger().findBy(wconn);
    }

	public void sendMessage(String userId, Sender sender, TalkResponse tresponse) {
		UserConnection uconn = findConnection(userId);
		if (uconn != null) {
			uconn.sendMessage(tresponse.talkMessage());
		} else {
			sender.sendTo(userId).sendAsync(tresponse.pushMessage());
		}
	}
}

class ConnManager {

	private Map<String, UserConnection> conns = MapUtil.newMap();
    private ScheduledExecutorService es = Executors.newScheduledThreadPool(5);

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

    public void heartBeat(final UserConnection conn) {

        conn.cancelHeartBeatJob(true);

        ScheduledFuture<?> watingJob = es.schedule(new Runnable() {
            @Override
            public void run() {

                conn.sendMessage("HEARTBEAT");

                ScheduledFuture<?> killingJob = es.schedule(new Runnable() {
                    @Override
                    public void run() {
                        conn.close(TalkEngine.Reason.TIMEOUT);
                    }
                }, TalkEngine.HEARTBEAT_WATING, TimeUnit.SECONDS);

                conn.setHeartBeatJob(killingJob);
            }
        }, TalkEngine.HEARTBEAT_KILLING, TimeUnit.SECONDS);

        conn.setHeartBeatJob(watingJob);

    }
}
