package net.ion.talk.script;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

	
	public void testFirst() throws Exception {
		RepositoryImpl r = RepositoryImpl.inmemoryCreateWithTest() ;
		ReadSession rsession = r.login("test") ;
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(2) ;
		NewClient nc = NewClient.create() ;
		
		BotScript bs = BotScript.create(rsession, ses, nc) ;
		bs.readDir(new File("./bot"), true) ;
		
		BotMessage bm = BotMessage.create().botId("echo").clientScript("client.room().onMessage(args.message);").message("Hello Echo").roomId("roomroom").sender("bleujin") ;
		Object result = bs.callFn("test.onMessage", bm) ;
		
		assertEquals("Hello", result);
	}
	
	public void testEcho() throws Exception {
		RepositoryImpl r = RepositoryImpl.inmemoryCreateWithTest() ;
		ReadSession rsession = r.login("test") ;
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(2) ;
		NewClient nc = NewClient.create() ;
		
		BotScript bs = BotScript.create(rsession, ses, nc) ;
		bs.readDir(new File("./bot"), true) ;

		rsession.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/roomroom/members/bleujin") ;
				return null;
			}
		}) ;
		
		BotMessage bm = BotMessage.create().botId("echo").clientScript("client.room().onMessage(args.message);").message("Hello Echo").roomId("roomroom").sender("bleujin").messageId("ddd") ;
		Object result = bs.callFn("echo.onMessage", bm) ;
		
		rsession.pathBy("/rooms/roomroom/messages").children().debugPrint(); 
	}
}
