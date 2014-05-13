package net.ion.talk.fake;

import net.ion.nradon.WebSocketConnection;
import net.ion.talk.UserConnection;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 4. 16. Time: 오후 5:14 To change this template use File | Settings | File Templates.
 */
public class FakeUserConnection extends UserConnection {
	private String received;

	protected FakeUserConnection(WebSocketConnection inner) {
		super(inner);
	}

	public String receivedMessage() {
		return received;
	}

	public void sendMessage(String message) {
		this.received = message;
	}
}