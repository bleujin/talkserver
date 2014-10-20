package net.ion.talk.toonweb;

import static net.ion.nradon.helpers.Hex.toHex;

import java.io.Flushable;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.Date;

import org.apache.log4j.Logger;

import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.EventSourceConnection;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.WebSocketConnection;
import net.ion.nradon.handler.logging.LogSink;

public class ToonLogSink implements LogSink {

	// TODO: Offload filesystem IO to another thread

	protected final Appendable out;
	protected final String[] dataValuesToLog;

	protected final String lineSeparator = System.getProperty("line.separator", "\n");
	private Logger logger = Logger.getLogger(ToonLogSink.class) ;
	
	protected boolean trouble = false;

	public ToonLogSink(Appendable out, String... dataValuesToLog) {
		this.out = out;
		this.dataValuesToLog = dataValuesToLog;
		try {
			formatHeader(out);
			flush();
		} catch (IOException e) {
			trouble = true;
			panic(e);
		}
	}

	public ToonLogSink(String... dataValuesToLog) {
		this(System.out, dataValuesToLog);
	}

	public void httpStart(HttpRequest request) {
//		custom(request, "HTTP-START", null);
	}

	public void httpEnd(HttpRequest request, HttpResponse response) {
		long etime = (Long)ObjectUtil.coalesce(request.data("_etime"), 0L) ;
		String uri = request.uri() ;
		if (isIgnoreURI(uri)) return ;
		
		custom(request, "HTTP-END[" + response.status() + "]", null); // TODO: Time request
	}

	private boolean isIgnoreURI(String uri) {
		if (uri == null) return true ;
		if (uri.startsWith("/toonweb/")) return true ;
		
		return false;
	}

	public void webSocketConnectionOpen(WebSocketConnection connection) {
		custom(connection.httpRequest(), "WEB-SOCKET-" + connection.version() + "-OPEN", null);
	}

	public void webSocketConnectionClose(WebSocketConnection connection) {
		custom(connection.httpRequest(), "WEB-SOCKET-" + connection.version() + "-CLOSE", null);
	}

	public void webSocketInboundData(WebSocketConnection connection, String data) {
		if ("HEARTBEAT".equals(data)) return ;
		custom(connection.httpRequest(), "WEB-SOCKET-" + connection.version() + "-IN-STRING", data);
	}

	public void webSocketInboundData(WebSocketConnection connection, byte[] data) {
		custom(connection.httpRequest(), "WEB-SOCKET-" + connection.version() + "-IN-HEX", toHex(data));
	}

	public void webSocketInboundPing(WebSocketConnection connection, byte[] message) {
		custom(connection.httpRequest(), "WEB-SOCKET-" + connection.version() + "-IN-PONG", toHex(message));
	}

	public void webSocketInboundPong(WebSocketConnection connection, byte[] message) {
		custom(connection.httpRequest(), "WEB-SOCKET-" + connection.version() + "-IN-PONG", toHex(message));
	}

	public void webSocketOutboundData(WebSocketConnection connection, String data) {
		if ("HEARTBEAT".equals(data)) return ;
		custom(connection.httpRequest(), "WEB-SOCKET-" + connection.version() + "-OUT-STRING", data);
	}

	public void webSocketOutboundData(WebSocketConnection connection, byte[] data) {
		custom(connection.httpRequest(), "WEB-SOCKET-" + connection.version() + "-OUT-HEX", toHex(data));
	}

	public void webSocketOutboundPing(WebSocketConnection connection, byte[] message) {
		custom(connection.httpRequest(), "WEB-SOCKET-" + connection.version() + "-OUT-PING", toHex(message));
	}

	public void webSocketOutboundPong(WebSocketConnection connection, byte[] message) {
		custom(connection.httpRequest(), "WEB-SOCKET-" + connection.version() + "-OUT-PING", toHex(message));
	}

	public void error(HttpRequest request, Throwable error) {
		custom(request, "ERROR-OPEN", error.toString());
	}

	public void custom(HttpRequest request, String action, String data) {
		if (trouble) {
			return;
		}
		try {
			formatLogEntry(out, request, action, data);
			flush();
		} catch (IOException e) {
			trouble = true;
			panic(e);
		}
	}

	public void eventSourceConnectionOpen(EventSourceConnection connection) {
		custom(connection.httpRequest(), "EVENT-SOURCE-OPEN", null);
	}

	public void eventSourceConnectionClose(EventSourceConnection connection) {
		custom(connection.httpRequest(), "EVENT-SOURCE-CLOSE", null);
	}

	public void eventSourceOutboundData(EventSourceConnection connection, String data) {
		custom(connection.httpRequest(), "EVENT-SOURCE-OUT", data);
	}

	protected void flush() throws IOException {
		if (out instanceof Flushable) {
			Flushable flushable = (Flushable) out;
			flushable.flush();
		}
	}

	protected void panic(IOException exception) {
		// If we can't log, be rude!
		exception.printStackTrace();
	}

	protected Appendable formatLogEntry(Appendable out, HttpRequest request, String action, String data) throws IOException {
		long cumulativeTimeOfRequest = cumulativeTimeOfRequest(request);
		Date now = new Date();
		StringBuilder sb = new StringBuilder() ;
		formatValue(sb, now);
		formatValue(sb, now.getTime());
		formatValue(sb, cumulativeTimeOfRequest);
		formatValue(sb, request.id());
		formatValue(sb, address(request.remoteAddress()));
		formatValue(sb, action);
		formatValue(sb, request.uri());
		formatValue(sb, data);
		for (String key : dataValuesToLog) {
			formatValue(sb, request.data(key));
		}
		sb.append(lineSeparator);
		
		logger.info(sb);
		
		return out.append(sb) ;
	}

	protected Appendable formatHeader(Appendable out) throws IOException {
		out.append("#Log started at ").append(new Date().toString()).append(" (").append(String.valueOf(System.currentTimeMillis())).append(")").append(lineSeparator).append('#');
		formatValue(out, "Date");
		formatValue(out, "Timestamp");
		formatValue(out, "MillsSinceRequestStart");
		formatValue(out, "RequestID");
		formatValue(out, "RemoteHost");
		formatValue(out, "Action");
		formatValue(out, "Path");
		formatValue(out, "Payload");
		for (String key : dataValuesToLog) {
			formatValue(out, "Data:" + key);
		}
		return out.append(lineSeparator);
	}

	private long cumulativeTimeOfRequest(HttpRequest request) {
		return System.currentTimeMillis() - request.timestamp();
	}

	protected Appendable formatValue(Appendable out, Object value) throws IOException {
		if (value == null) {
			return out.append("-\t");
		}
		String string = value.toString().trim();
		if (StringUtil.isEmpty(string)) {
			return out.append("-\t");
		}
		return out.append(string).append('\t');
	}

	protected String address(SocketAddress address) {
		return address.toString();
	}
}