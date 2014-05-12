package net.ion.talk.toonweb;

import java.util.HashSet;
import java.util.Set;

import net.ion.framework.util.Debug;
import net.ion.nradon.DataHolder;
import net.ion.nradon.WebSocketConnection;

public class ChatServer implements Server<ChatClient> {

    public static final String USERNAME_KEY = "username";

    private Set<ChatClient> clients = new HashSet<ChatClient>();

    @Override
    public void onOpen(WebSocketConnection conn, ChatClient client) throws Exception {
        clients.add(client);
    }

    @Override
    public void onClose(WebSocketConnection conn, ChatClient client) throws Exception {
        String username = (String) conn.data(USERNAME_KEY);
        if (username != null) {
            for (ChatClient other : clients) {
                other.leave(username);
            }
        }
        clients.remove(client);
    }

    @Remote
    public void login(DataHolder dholder, String username) {
        dholder.data(USERNAME_KEY, username); // associate username with connection

        for (ChatClient other : clients) {
            other.join(username);
        }
    }

    @Remote
    public void say(WebSocketConnection conn, String message) {
        String username = (String) conn.data(USERNAME_KEY);
        if (username == null || message == null) {
        	
        	
            for (ChatClient other : clients) {
                other.say(username, message);
            }
        }
    }
    
    @Remote
    public void echo(WebSocketConnection conn, String msg){
    	for (ChatClient other : clients) {
    		if (conn == other.source()) other.echo(msg.toUpperCase());
    	}
    }
    
}