package net.ion.talk;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHeaders;

import net.ion.framework.util.ObjectUtil;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.WebSocketConnection;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.util.CalUtil;

public class UserConnection {

	public static final UserConnection NOTFOUND = new UserConnection(null);

	private WebSocketConnection inner;

	protected UserConnection(WebSocketConnection inner) {
		this.inner = inner;
	}

	static UserConnection create(WebSocketConnection conn) {
		return new UserConnection(conn);
	}

	public String id() {
		return inner.getString("id");
	}

	
	
	
	public boolean isAllowUser(String accessToken) {
		return accessToken().equals(accessToken);
	}

	public void updateHeartBeat() {
		inner.data("_sessionTime", CalUtil.gmtTime());
	}

	public long sessionTime() {
		return (Long) inner.data("_sessionTime");
	}

	
	
	
	
	
	
	void close(Reason reason) {
		inner.close();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserConnection) {
			return ((UserConnection) obj).inner.equals(this.inner);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.id().hashCode();
	}

	public String toString() {
		return "UserConnection[" + id() + "/" + accessToken() + "]";
	}

	WebSocketConnection inner() {
		return inner;
	}

	private String accessToken() {
		return inner.getString("accessToken");
	}

	public HttpRequest request() {
		return inner.httpRequest();
	}
	
	private Map<String, Object> asMap() {
		return Collections.unmodifiableMap(inner.data());
	}

	private String asString(String key) {
		return ObjectUtil.toString(inner.data(key));
	}

	private Object asObject(String key) {
		return inner.data(key);
	}


	public void sendMessage(String message) {
		inner.send(message);
	}

	public boolean fromApp() {
		HttpRequest request = request();
		if (request == null) return false ;
		String agent = ObjectUtil.toString(request.header(HttpHeaders.USER_AGENT)) ;
		return agent.startsWith("AradonClient") ;
	}

}

class DoppleUserConnection extends UserConnection {

	private List<WebSocketConnection> dopple;
	private DoppleUserConnection(WebSocketConnection first, List<WebSocketConnection> dopple) {
		super(first);
		this.dopple = dopple ;
	}
	
	public static UserConnection create(List<WebSocketConnection> found) {
		return new DoppleUserConnection(found.get(0), found);
	}
	
	
	public boolean isAllowUser(String accessToken) {
		throw new IllegalArgumentException("this is dopple") ;
	}

	public void updateHeartBeat() {
		throw new IllegalArgumentException("this is dopple") ;
	}

	public long sessionTime() {
		throw new IllegalArgumentException("this is dopple") ;
	}
	
	void close(Reason reason) {
		throw new IllegalArgumentException("this is dopple") ;
	}

	public HttpRequest request() {
		throw new IllegalArgumentException("this is dopple") ;
	}
	
	public boolean fromApp() {
		for (WebSocketConnection inner : dopple) {
			HttpRequest request = inner.httpRequest() ;
			if (request == null) continue ;
			String agent = ObjectUtil.toString(request.header(HttpHeaders.USER_AGENT)) ;
			if (agent.startsWith("AradonClient")) return true ;
		}
		return false ;
	}
	
	public void sendMessage(String message) {
		for (WebSocketConnection inner : dopple) {
			inner.send(message);
		}
	}
}
