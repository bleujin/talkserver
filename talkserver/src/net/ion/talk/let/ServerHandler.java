package net.ion.talk.let;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.ecs.xhtml.p;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkHandler;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;

public class ServerHandler implements TalkHandler {

	private final String hostName;
	private final String serverHost;
	private final int port;
	private ReadSession session;

	public ServerHandler(String hostName, String serverHost, int port) {
		this.hostName = hostName ;
		this.serverHost = serverHost;
		this.port = port;
	}

	public static ServerHandler test() throws UnknownHostException {
		InetAddress address = InetAddress.getLocalHost();
		return new ServerHandler(address.getHostName(), address.getHostAddress(), 9000);
	}

	public String serverHost() {
		return serverHost;
	}

	@Override
	public void onClose(TalkEngine tengine, UserConnection uconn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(TalkEngine tengine, UserConnection uconn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEngineStart(TalkEngine tengine) throws IOException {
		try {
			this.session = tengine.readSession();
			
			session.tranSync(new TransactionJob<Void>() {
				@Override
				public Void handle(WriteSession wsession) throws Exception {
					wsession.pathBy("/servers/" + hostName).property("host", serverHost).property("port", port);
					return null;
				}
			});
		} catch (Exception ex) {
			throw new IOException(ex) ;
		}
	}
	
	public boolean registered(String hostName) {
		return session.exists("/servers/" + hostName) ;
	}


	@Override
	public void onEngineStop(TalkEngine tengine) {
		try {
			session.tranSync(new TransactionJob<Void>() {
				@Override
				public Void handle(WriteSession wsession) throws Exception {
					wsession.pathBy("/servers/" + hostName).removeSelf() ;
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace() ;
			tengine.getLogger().warning(e.getMessage()) ;
		}
	}

	@Override
	public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmsg) {

	}

	public String hostName() {
		return hostName;
	}


	

}
