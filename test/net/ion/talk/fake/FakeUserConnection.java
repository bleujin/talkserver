package net.ion.talk.fake;

import net.ion.nradon.WebSocketConnection;
import net.ion.talk.FakeWebSocketConnection;
import net.ion.talk.UserConnection;


public class FakeUserConnection extends UserConnection {
	private String received;

	protected FakeUserConnection(WebSocketConnection inner) {
		super(inner);
	}
	
	public final static FakeUserConnection fake(String id) {
		return new FakeUserConnection(FakeWebSocketConnection.create(id)) ;
	}


	public String receivedMessage() {
		return received;
	}

	public void sendMessage(String message) {
		this.received = message;
	}
	
	public String asString(String key) {
		return super.asString(key);
	}

}