package net.ion.talk.bot;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.FileUtil;
import net.ion.talk.TalkEngine;
import net.ion.talk.ToonServer;
import net.ion.talk.bean.Const.User;
import junit.framework.TestCase;

public class TestBaseServer extends TestCase {

	protected TalkEngine talkEngine;
	protected ToonServer tserver;

	@Override
	protected void setUp() throws Exception {

		String filePath = "./resource/craken";
		FileUtil.deleteDirectory(new File(filePath));
		
//		RepositoryEntry rentry = RepositoryEntry.testSoloFile(filePath) ;
		this.tserver = ToonServer.testCreate();
		tserver.ready().startRadon();
		
		
		ReadSession rsession = tserver.talkEngine().readSession();
		rsession.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/hero@i-on.net").property(User.UserId, "hero@i-on.net").property(User.Password, "1").property(User.NickName, "hero").property(User.StateMessage, "-_-;").property(User.Phone, "1042216492") ;
				wsession.pathBy("/users/bleujin@i-on.net").property(User.UserId, "bleujin@i-on.net").property(User.Password, "1").property(User.NickName, "bleujin").property(User.StateMessage, "-_-a").property(User.Phone, "1042216492") ;
				return null;
			}
		});
		this.talkEngine = tserver.talkEngine() ;
	}
	
	

	@Override
	protected void tearDown() throws Exception {
		tserver.stop() ;
		super.tearDown();
	}


}
