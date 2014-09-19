package net.ion.talk.example;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.crud.RepositoryImpl;

public class MiniChatMain {

    public static void main(String[] args) throws Exception {
    	
    	final RepositoryImpl r = RepositoryImpl.inmemoryCreateWithTest() ;
    	ReadSession rsession = r.login("test") ;
    	final ScheduledExecutorService ses = Executors.newScheduledThreadPool(3) ;
    	
//        Radon webServer = RadonConfiguration.newBuilder(9877)
//                .add(new LoggingHandler(new SimpleLogSink(ChatServer.USERNAME_KEY)))
//                .add("/chatsocket", EasyRemote.easyRemote(ChatClient.class, ChatServer.create(CommandScript.create(rsession, ses).readDir(new File("./command"), true)), new CsvClientMaker()))
//                .add(new EmbeddedResourceHandler("net/ion/talk/toonweb/content"))
//                .start().get();
//
//        System.out.println("Chat room running on: " + webServer.getUri());
//        
//        Runtime.getRuntime().addShutdownHook(new Thread(){
//        	public void run(){
//        		r.shutdown() ;
//        		ses.shutdown(); 
//        	}
//        });
        
    }

}