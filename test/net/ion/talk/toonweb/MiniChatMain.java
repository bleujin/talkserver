package net.ion.talk.toonweb;

import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.config.RadonConfigurationBuilder;
import net.ion.nradon.handler.EmbeddedResourceHandler;
import net.ion.nradon.handler.logging.LoggingHandler;
import net.ion.nradon.handler.logging.SimpleLogSink;
import net.ion.talk.toonweb.outbound.CsvClientMaker;

public class MiniChatMain {

    public static void main(String[] args) throws Exception {
    	
        Radon webServer = RadonConfiguration.newBuilder(9877)
                .add(new LoggingHandler(new SimpleLogSink(ChatServer.USERNAME_KEY)))
                .add("/chatsocket", EasyRemote.easyRemote(ChatClient.class, new ChatServer(), new CsvClientMaker()))
                .add(new EmbeddedResourceHandler("net/ion/talk/toonweb/content"))
                .start().get();

        System.out.println("Chat room running on: " + webServer.getUri());
    }

}