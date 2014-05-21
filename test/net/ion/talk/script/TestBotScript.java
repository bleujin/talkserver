package net.ion.talk.script;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.MapUtil;
import net.ion.radon.aclient.NewClient;
import net.ion.talk.ParameterMap;
import net.ion.talk.bean.Const;
import junit.framework.TestCase;

public class TestBotScript extends TestCase{

	
	public void testEcho() throws Exception {
		RepositoryImpl r = RepositoryImpl.inmemoryCreateWithTest() ;
		ReadSession rsession = r.login("test") ;
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(2) ;
		NewClient nc = NewClient.create() ;
		
		rsession.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/notifies/123456").refTo("message", "/rooms/roomroom/messages/1234") ;
				wsession.pathBy("/rooms/roomroom/messages/1234").property(Const.Message.ClientScript, "client.room().onMessage(args);")
					.property(Const.Message.Options, "{event:'onMessage'}")
					.property(Const.Message.Message, "Hello Echo").refTo(Const.Message.Sender, "/users/bleujin") ;
				wsession.pathBy("/users/bleujin").property("userId", "bleujin") ;
				return null;
			}
		}) ;
		
		BotScript bs = BotScript.create(rsession, ses, nc) ;
		bs.readDir(new File("./bot"), true) ;
		
		ReadNode mnode = rsession.pathBy("/notifies/123456") ;
		BotMessage bm = BotMessage.create("echo", mnode);
		Object result = bs.callFromOnMessage(bm) ;
	
		assertEquals("Hello Echo", rsession.pathBy("/rooms/roomroom/messages").children().gt("roomId", " ").firstNode().property("message").asString()) ; 
		
//		assertEquals("Hello", result);
	}
}
