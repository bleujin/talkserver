package net.ion.talk;

import java.net.HttpCookie;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executor;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.WebSocketConnection;

import org.jboss.netty.channel.ChannelFuture;

public class FakeWebSocketConnection implements WebSocketConnection {

	private Map<String, Object> datas = MapUtil.newMap();
	private List<String> received = ListUtil.newList();
    private boolean closed;
	private String agent;

    private FakeWebSocketConnection() {}
    
    public static FakeWebSocketConnection create(String id) {
		final FakeWebSocketConnection result = new FakeWebSocketConnection();
		result.data("id", id);
		
		return result;
	}

    public static FakeWebSocketConnection createFromApp(String id) {
		final FakeWebSocketConnection result = new FakeWebSocketConnection();
		result.data("id", id);
		result.agent = "AradonClient" ;
		
		return result;
	}



    
	@Override
	public WebSocketConnection close() {
        closed = true;
        return this;
	}

	@Override
	public WebSocketConnection data(String key, Object value) {
		datas.put(key, value);
		return this;
	}

	@Override
	public String getString(String key) {
		return ObjectUtil.toString(datas.get(key));
	}

	@Override
	public WebSocketConnection ping(byte[] message) {
		return this;
	}

	@Override
	public WebSocketConnection pong(byte[] message) {
		return this;
	}

	@Override
	public WebSocketConnection send(String message) {
		received.add(message);
		return this;
	}

	@Override
	public void execute(Runnable command) {
		command.run();
	}

	@Override
	public Map<String, Object> data() {
		return datas;
	}

	@Override
	public Object data(String key) {
		return datas.get(key);
	}

	@Override
	public Set<String> dataKeys() {
		return datas.keySet();
	}

	public String recentMsg() {
		return received.get(received.size() - 1);
	}

	@Override
	public WebSocketConnection send(byte[] message) {
		return this;
	}

	@Override
	public WebSocketConnection send(byte[] message, int offset, int length) {
		return this;
	}

	@Override
	public Executor handlerExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpRequest httpRequest() {
		return new HttpRequest() {
			
			@Override
			public Set<String> dataKeys() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object data(String s) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Map<String, Object> data() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HttpRequest uri(String s) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String uri() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public long timestamp() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public SocketAddress remoteAddress() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<String> queryParams(String s) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Set<String> queryParamKeys() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String queryParam(String s) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<String> postParams(String s) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Set<String> postParamKeys() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String postParam(String s) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String method() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object id() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<String> headers(String s) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String header(String s) {
				return FakeWebSocketConnection.this.agent;
			}
			
			@Override
			public boolean hasHeader(String s) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public HttpRequest data(String s, Object obj) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<HttpCookie> cookies() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String cookieValue(String s) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HttpCookie cookie(String s) {
				// TODO Auto-generated method stub
				return null;
			}
			
			
			@Override
			public byte[] bodyAsBytes() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String body() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<Entry<String, String>> allHeaders() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	@Override
	public ChannelFuture sendFuture(String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String version() {
		// TODO Auto-generated method stub
		return null;
	}

    public boolean isClosed() {
        return closed;
    }
}
