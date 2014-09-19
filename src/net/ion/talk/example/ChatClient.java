package net.ion.talk.example;

import net.ion.nradon.WebSocketConnection;


@Remote
public interface ChatClient {

	public final static ChatClient DUMMY = null;

	WebSocketConnection source() ;
	
	String userId() ;
	
    void say(String username, String message);

    void leave(String username);

    void entry(String username);

    void echo(String msg) ;
    
    void listrooms(String rooms) ;
}
