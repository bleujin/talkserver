package net.ion.talk.util;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class NetworkUtil {
	
	public static String hostAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "127.0.0.1";
		}

	}
	
	public static String httpAddress(int port, String remainPath) {
		return "http://" + hostAddress() + ":" + port + remainPath;
	}

	public static String wsAddress(int port, String remainPath) {
		return "ws://" + hostAddress() + ":" + port + remainPath;
	}
}
