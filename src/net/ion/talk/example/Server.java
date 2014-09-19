package net.ion.talk.example;

import net.ion.nradon.WebSocketConnection;

public interface Server<C> {

    void onOpen(WebSocketConnection connection, C client) throws Exception;

    void onClose(WebSocketConnection connection, C client) throws Exception;

}
