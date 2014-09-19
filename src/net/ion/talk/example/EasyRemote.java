package net.ion.talk.example;

import net.ion.nradon.WebSocketConnection;
import net.ion.nradon.WebSocketHandler;
import net.ion.talk.example.inbound.InboundDispatcher;
import net.ion.talk.example.inbound.JsonInboundDispatcher;
import net.ion.talk.example.outbound.ClientMaker;
import net.ion.talk.example.outbound.Exporter;
import net.ion.talk.example.outbound.JsonClientMaker;

@SuppressWarnings({"unchecked"})
public class EasyRemote<CLIENT> implements WebSocketHandler {

    public static final String CLIENT_KEY = EasyRemote.class.getPackage().getName() + ".client";

    private final Class<CLIENT> clientType;
    private final ClientMaker clientMaker;
    private final Server<CLIENT> server;
    private final InboundDispatcher in;

    public EasyRemote(Class<CLIENT> clientType, Server<CLIENT> server, ClientMaker clientMaker) {
        this.clientType = clientType;
        this.clientMaker = clientMaker;
        this.in = new JsonInboundDispatcher(server, clientType);
        this.server = server;
    }

    public static <T> WebSocketHandler easyRemote(Class<T> clientType, Server<T> server) {
        return new EasyRemote<T>(clientType, server, new JsonClientMaker());
    }

    public static <T> WebSocketHandler easyRemote(Class<T> clientType, Server<T> server, ClientMaker clientMaker) {
        return new EasyRemote<T>(clientType, server, clientMaker);
    }

    @Override
    public void onOpen(WebSocketConnection connection) throws Exception {
        CLIENT client = clientMaker.implement(clientType, connection);
        ((Exporter) client).__exportMethods(in.availableMethods());
        connection.data(CLIENT_KEY, client);
        server.onOpen(connection, client);
    }

    @Override
    public void onMessage(WebSocketConnection connection, String msg) throws Throwable {
        in.dispatch(connection, msg, connection.data(CLIENT_KEY));
    }

    @Override
    public void onMessage(WebSocketConnection webSocketConnection, byte[] bytes) throws Throwable {
    }

    @Override
    public void onClose(WebSocketConnection connection) throws Exception {
        server.onClose(connection, (CLIENT) connection.data(CLIENT_KEY));
    }

	@Override
	public void onPing(WebSocketConnection arg0, byte[] arg1) throws Throwable {
	}

	@Override
	public void onPong(WebSocketConnection arg0, byte[] arg1) throws Throwable {
	}
}



