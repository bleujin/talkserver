package net.ion.talk.handler.engine;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;
import net.ion.talk.handler.TalkHandler;

public class ServerHandler implements TalkHandler {

	private final String hostName;
	private final String serverHost;
	private final int port;
	private ReadSession session;

	public ServerHandler(String hostName, String serverHost, int port) {
		this.hostName = hostName;
		this.serverHost = serverHost;
		this.port = port;
	}

	public static ServerHandler test() throws UnknownHostException {
		InetAddress address = InetAddress.getLocalHost();
		return new ServerHandler(address.getHostName(), currentIp(), 9000);
	}

	public String serverHost() {
		return serverHost;
	}

	@Override
	public void onClose(TalkEngine tengine, UserConnection uconn) {
		// TODO Auto-generated method stub1`

	}

	@Override
	public Reason onConnected(TalkEngine tengine, UserConnection uconn) {
		return Reason.OK;
	}

	@Override
	public void onEngineStart(TalkEngine tengine) throws IOException {
		this.session = tengine.readSession();

		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/servers/" + hostName).property("host", serverHost).property("port", port);
				return null;
			}
		});
	}

	public boolean registered(String hostName) {
		return session.exists("/servers/" + hostName);
	}

	@Override
	public void onEngineStop(TalkEngine tengine) {
		try {
			this.session.tran(new TransactionJob<Void>() {
				@Override
				public Void handle(WriteSession wsession) throws Exception {
					wsession.pathBy("/servers/" + hostName).removeSelf();
					return null;
				}
			});
		} catch (Exception e) {
			tengine.getLogger().warning(e.getMessage());
		}
	}

	@Override
	public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmsg) {
	}

	public String hostName() {
		return hostName;
	}

	private static String currentIp() throws UnknownHostException {
		Enumeration netInterfaces = null;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			return getLocalIp();
		}

		while (netInterfaces.hasMoreElements()) {
			NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
			Enumeration address = ni.getInetAddresses();
			if (address == null) {
				return getLocalIp();
			}
			while (address.hasMoreElements()) {
				InetAddress addr = (InetAddress) address.nextElement();
				if (!addr.isLoopbackAddress() && !addr.isSiteLocalAddress() && !addr.isAnyLocalAddress()) {
					String ip = addr.getHostAddress();
					if (ip.indexOf(".") != -1 && ip.indexOf(":") == -1) {
						return ip;
					}
				}
			}
		}
		return getLocalIp();
	}

	private static String getLocalIp() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}

}
