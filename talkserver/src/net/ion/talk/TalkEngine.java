package net.ion.talk;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.logging.LogBroker;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.AbstractWebSocketResource;
import net.ion.nradon.WebSocketConnection;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.IService;
import net.ion.radon.core.SectionService;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.config.WSPathConfiguration;
import net.ion.radon.core.context.OnEventObject;
import net.ion.radon.core.context.OnOrderEventObject;
import net.ion.talk.let.TalkHandlerGroup;

public class TalkEngine extends AbstractWebSocketResource implements OnOrderEventObject {

	public enum DisConnectReason {
		DOPPLE, CLIENT, TIMEOUT;
	}

	private ConnManager cmanger = ConnManager.create();
	private List<TalkHandler> handlers = ListUtil.newList();

	private Aradon aradon;
	private Logger logger = LogBroker.getLogger(TalkEngine.class);

	private TalkEngine(Aradon aradon) {
		if (aradon == null)
			throw new IllegalStateException("aradon is null");

		this.aradon = aradon;
		aradon.getServiceContext().putAttribute(TalkEngine.class.getCanonicalName(), this);
	}

	public TalkEngine() {
		;
	}

	public static TalkEngine create(Aradon aradon) {
		return new TalkEngine(aradon);
	}

	public static TalkEngine test() throws Exception {
		Aradon aradon = Aradon.create();
		aradon.getServiceContext().putAttribute(RepositoryEntry.EntryName, RepositoryEntry.test());
		aradon.getServiceContext().putAttribute(RhinoEntry.EntryName, RhinoEntry.test());
		final TalkEngine result = TalkEngine.create(aradon);

		aradon.start();
		return result;
	}

	public void onInit(SectionService parent, TreeContext context, WSPathConfiguration wsconfig) {
		super.onInit(parent, context, wsconfig);
		this.aradon = parent.getAradon();

		aradon.getServiceContext().putAttribute(TalkEngine.class.getCanonicalName(), this);
		TalkHandlerGroup hg = context.getAttributeObject(TalkHandlerGroup.class.getCanonicalName(), TalkHandlerGroup.class);
		hg.set(this);
	}

	public ReadSession readSession() throws IOException {
		RepositoryEntry re = aradon.getServiceContext().getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
		return re.login("test");
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

		for (TalkHandler handler : handlers) {
			handler.onConnected(this, created);
		}
	}

	@Override
	public void onClose(WebSocketConnection conn) {
		final UserConnection found = cmanger.findBy(conn);
		for (TalkHandler handler : handlers) {
			handler.onClose(this, found);
		}
		cmanger.remove(found, DisConnectReason.CLIENT);
	}

	@Override
	public void onMessage(WebSocketConnection conn, String msg) {
		try {

			final UserConnection found = cmanger.findBy(conn);
			TalkMessage tmessage = TalkMessage.fromJsonString(msg);

			RepositoryEntry r = context().getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
			ReadSession rsession = r.login("test");

			for (TalkHandler handler : handlers) {
				handler.onMessage(this, found, rsession, tmessage);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
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
	public void onStop(){
		onEvent(AradonEvent.STOP, null) ;
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
			}
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	// Only test
	void stop() {
		aradon.stop();
	}

	public <T extends TalkHandler> T handler(Class<T> clz) {
		for (TalkHandler handler : handlers) {
			if (clz.isInstance(handler)) return clz.cast(handler) ;
		}
		throw new IllegalArgumentException() ;
	}

	public Logger getLogger() {
		return logger;
	}

	@Override
	public int order() {
		return 2;
	}
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
			existConn.close(this, TalkEngine.DisConnectReason.DOPPLE);
		return uconn;
	}

	public UserConnection remove(UserConnection uconn, TalkEngine.DisConnectReason reason) {
		conns.remove(uconn.id());
		uconn.close(this, reason);
		return uconn;
	}

	public boolean contains(WebSocketConnection conn) {
		return conns.containsValue(conn);
	}

}