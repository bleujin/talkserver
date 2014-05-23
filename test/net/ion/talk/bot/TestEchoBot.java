package net.ion.talk.bot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import junit.framework.TestCase;
import net.ion.craken.aradon.NodeLet;
import net.ion.craken.aradon.UploadLet;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.nradon.Radon;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.talk.TalkEngine;
import net.ion.talk.bean.Const.User;
import net.ion.talk.let.ResourceLet;
import net.ion.talk.let.ScriptDoLet;
import net.ion.talk.toonweb.ClientLet;
import net.ion.talk.toonweb.ToonWebResourceLet;

public class TestEchoBot extends TestBaseServer {

	public void testonLoad() throws Exception {
		ReadSession session = talkEngine.readSession();
		
		assertEquals(true, session.exists("/bots/echo"));
		assertEquals(true, session.exists("/users/echo"));
		
		assertEquals("echo bot", session.pathBy("/users/echo").property("nickname").asString()) ;
	}
	
	
	public void testOnEnter() throws Exception {
		ReadSession session = talkEngine.readSession();

		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/bleujin").property(User.UserId, "bleujin") ;
				wsession.pathBy("/rooms/roomroom/members/echo") ;
				return null;
			}
		});

		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/roomroom/members/bleujin") ;
				return null;
			}
		}) ;
		session.workspace().cddm().await(); 
		Thread.sleep(500); // wait workspace Listener(NotificationListener)

		assertEquals(3, session.pathBy("/rooms/roomroom/messages").children().count()) ;
		assertEquals(1, session.pathBy("/notifies/bleujin").children().count()) ; // exclusive sender ; true
		
		session.pathBy("/rooms/roomroom/messages").children().debugPrint();
//		new InfinityThread().startNJoin(); 
	}
	
	

	public void testOnMessage() throws Exception {
		ReadSession session = talkEngine.readSession();
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/bleujin") ;

				wsession.pathBy("/rooms/roomroom/members/echo") ;
				wsession.pathBy("/rooms/roomroom/members/bleujin") ;
				return null;
			}
		});
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/roomroom/messages/123456").property("message", "Hello World").property("options", "{event:'onMessage'}")
					.refTo("sender", "/users/bleujin") ;
				return null;
			}
		}) ;
		
		session.workspace().cddm().await(); 
		
		assertEquals("Hello World", session.pathBy("/notifies/bleujin/123456").ref("message").property("message").asString()) ; 
	}
	
	
	
}
