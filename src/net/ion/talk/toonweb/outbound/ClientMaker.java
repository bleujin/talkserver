package net.ion.talk.toonweb.outbound;

import net.ion.nradon.WebSocketConnection;

public interface ClientMaker {
    <T> T implement(Class<T> type, WebSocketConnection connection);
}
