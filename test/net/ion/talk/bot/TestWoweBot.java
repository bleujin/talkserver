package net.ion.talk.bot;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.util.csv.CsvReader;
import net.ion.talk.TalkMessage;
import net.ion.talk.fake.FakeUserConnection;
import net.ion.talk.handler.craken.UserInAndOutRoomHandler;
import net.ion.talk.script.BotScript;
import net.ion.talk.script.WhisperMessage;

public class TestWoweBot extends TestCase {
	private RepositoryEntry r;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.r = RepositoryEntry.test() ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		r.shutdown(); 
		super.tearDown();
	}

	
	public void testStringFormat() throws Exception {
		CsvReader reader = new CsvReader(new InputStreamReader(new FileInputStream("./bot/wow.txt"), "UTF-8")) ;
		reader.setBlockDelimiter('\n');
		reader.setFieldDelimiter('-');
		
		reader.readLine() ;
		while(true){
			String[] line = reader.readLine() ;
			if (line == null || line.length == 0 || net.ion.framework.util.StringUtil.isBlank(line[0])) break ;
			String[] names = net.ion.framework.util.StringUtil.split(line[0], "/") ;
		}
		
		
	}

	public void testBotLoad() throws Exception {
		ReadSession rsession = r.login() ;
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(2); 
		BotScript bs = BotScript.create(rsession, ses, NewClient.create()) ;
		bs.scriptExtension(".script") ;
		bs.readDir(new File("./bot")) ;
		
		FakeUserConnection source = FakeUserConnection.fake("bleujin@i-on.net");
		TalkMessage tm = TalkMessage.fromJsonString("{'id':'773','script':'/room/sendMessageWith','params':{'receivers':'','roomId':'234','message':'@wowe 궁금 당신','sender':'hero@i-on.net','senderNickname':'hero','clientScript':'client.room().message(args);','requestId':'773'}}");
		WhisperMessage whisperMsg = WhisperMessage.create(source, tm);
		Object rtn = bs.whisper(source, whisperMsg) ;
		
		assertEquals(1, rsession.pathBy("/rooms/234/messages").children().count()) ; 
	}
	
	
	public void testOwner() throws Exception {
		ReadSession session = r.login() ;
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(2); 
		BotScript bs = BotScript.create(session, ses, NewClient.create()) ;
		bs.scriptExtension(".script") ;
		bs.readDir(new File("./bot")) ;
		
		assertEquals(Boolean.TRUE, session.pathBy("/bots/wowe").property("owner").asBoolean()) ;
		session.workspace().cddm().add(UserInAndOutRoomHandler.test()) ;

		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/1234/members/wowe") ;
				return null;
			}
		}) ;
		
		assertEquals("wowe", session.pathBy("/rooms/1234").ref("owner").fqn().name()) ; 
		
	}
	
	
	public void testNotFound() throws Exception {
		ReadSession rsession = r.login() ;
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(2); 
		BotScript bs = BotScript.create(rsession, ses, NewClient.create()) ;
		bs.scriptExtension(".script") ;
		bs.readDir(new File("./bot")) ;
		
		FakeUserConnection source = FakeUserConnection.fake("bleujin@i-on.net");
		TalkMessage tm = TalkMessage.fromJsonString("{'id':'773','script':'/room/sendMessageWith','params':{'receivers':'','roomId':'234','message':'@wowe 낫 당신','sender':'hero@i-on.net','senderNickname':'hero','clientScript':'client.room().message(args);','requestId':'773'}}");
		WhisperMessage whisperMsg = WhisperMessage.create(source, tm);
		Object rtn = bs.whisper(source, whisperMsg) ;
		Debug.line(source.receivedMessage());
	}
	

}
