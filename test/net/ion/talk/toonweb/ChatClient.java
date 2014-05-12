package net.ion.talk.toonweb;

import net.ion.nradon.WebSocketConnection;


@Remote
interface ChatClient {

	WebSocketConnection source() ;
	
    void say(String username, String message);

    void leave(String username);

    void join(String username);

    void echo(String msg) ;
}
